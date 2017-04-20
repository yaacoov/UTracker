package com.example.yaacoov.utracker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaacoov.utracker.GridSpacingItemDecoration;
import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.adapters.ActionAdapter;
import com.example.yaacoov.utracker.analytics.ActionAnalytics;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ActionList;
import com.example.yaacoov.utracker.models.ActionRecord;
import com.example.yaacoov.utracker.sync.FirebaseSyncUtils;
import com.example.yaacoov.utracker.utils.ActionScoreUtils;
import com.example.yaacoov.utracker.utils.ReminderUtils;
import com.example.yaacoov.utracker.utils.SharedPreferencesUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yaacoov on 20/03/17.
 * *UTracker.
 */

public class ActionListActivity extends AppCompatActivity implements ActionAdapter.ActionAdapterOnClickListener {

    private static final String TAG = "ActionListActivity";

    private static final int NUM_OF_COLUMNS = 2;
    private static final int SPACE_BETWEEN_ITEMS = 32;
    private static final int RC_SIGN_IN = 10;

    @BindView(R.id.rv_habits)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private ActionAdapter mHabitsAdapter;

    // Firebase instance variables.
    private Query mUserHabitsQuery;
    private ValueEventListener mValueEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure();
    }

    private void configure() {
        setContentView(R.layout.activity_action_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initFirebase();

        ActionList.SortOrder sortOrder = SharedPreferencesUtils.getSortOrder(this);
        ActionList actionList = new ActionList(new ArrayList<Action>(), sortOrder);
        mHabitsAdapter = new ActionAdapter(actionList);
        mHabitsAdapter.setClickListener(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUM_OF_COLUMNS));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(NUM_OF_COLUMNS,
                SPACE_BETWEEN_ITEMS, true));
        mRecyclerView.setAdapter(mHabitsAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                } else if (dy < 0 && !floatingActionButton.isShown()) {
                    floatingActionButton.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(findViewById(R.id.activity_habit_list), R.string.auth_success_msg,
                        Snackbar.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.auth_canceled_msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_habit_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_sort_by_name:
                sortBy(ActionList.SortOrder.NAME);
                return true;
            case R.id.action_sort_by_created_date:
                sortBy(ActionList.SortOrder.DATE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Action selectedAction, int position) {
        showDetail(selectedAction);
    }

    @OnClick(R.id.fab)
    void onAddClick() {
        createHabit();
    }

    private void initFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    onSignedInInitialize();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                                    ).build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void sortBy(ActionList.SortOrder sortOrder) {
        SharedPreferencesUtils.setSortOrder(this, sortOrder);
        mHabitsAdapter.setSortOrder(sortOrder);
    }

    private void signOut() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance().signOut(ActionListActivity.this);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void onSignedInInitialize() {
        detachDatabaseReadListener();

        ActionAnalytics.logLogin(FirebaseAuth.getInstance().getCurrentUser());
        mUserHabitsQuery = FirebaseSyncUtils.getCurrentUserHabitsQuery();
        assert mUserHabitsQuery != null;
        mUserHabitsQuery.keepSynced(true);

        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        ReminderUtils.cancelAll(mHabitsAdapter.getActions(), this);
        mHabitsAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mValueEventListener != null) return;

        showProgressIndicator();

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressIndicator();
                processOnDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressIndicator();
                Log.e(TAG, "Cancelled to query habits: " + databaseError.toString());
            }
        };
        mUserHabitsQuery.addValueEventListener(mValueEventListener);
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mUserHabitsQuery.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
        mUserHabitsQuery = null;
    }

    private void processOnDataChange(DataSnapshot dataSnapshot) {
        List<Action> actions = new ArrayList<>((int) dataSnapshot.getChildrenCount());
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            ActionRecord parsedRecord = data.getValue(ActionRecord.class);
            actions.add(new Action(data.getKey(), parsedRecord));
        }

        mHabitsAdapter.setActions(actions);
        emptyView.setVisibility(mHabitsAdapter.getActions().isEmpty() ? View.VISIBLE : View.INVISIBLE);

        ActionScoreUtils.processAll(actions);
        ReminderUtils.processAll(actions, this);
    }

    private void showDetail(Action action) {
        ActionAnalytics.logViewHabitListItem(action);
        Intent intent = new Intent(this, DetailActionActivity.class);
        intent.putExtra(DetailActionActivity.ACTION_EXTRA_KEY, action);
        startActivity(intent);
    }

    private void createHabit() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, EditActionActivity.class));
        }
    }

    private void showProgressIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressIndicator() {
        progressBar.setVisibility(View.INVISIBLE);
    }

}
