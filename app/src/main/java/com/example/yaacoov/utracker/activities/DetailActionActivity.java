package com.example.yaacoov.utracker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.barchart.ActionBarChartConfigurator;
import com.example.yaacoov.utracker.barchart.ActionBarChartDataLoader;
import com.example.yaacoov.utracker.barchart.ActionBarChartDataSource;
import com.example.yaacoov.utracker.barchart.ActionBarChartRange;
import com.example.yaacoov.utracker.model.ActionBarChartViewModel;
import com.example.yaacoov.utracker.model.ActionDetailViewModel;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.sync.FirebaseSyncUtils;
import com.github.mikephil.charting.charts.BarChart;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yaacoov on 19/03/17.
 * UTracker.
 */

public class DetailActionActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<ActionBarChartDataSource> {

    public static final String ACTION_EXTRA_KEY = "com.ivanmagda.habito.activities.habit";

    private static final String TAG = "DetailActionActivity";

    private static final int BAR_CHART_DATA_SOURCE_LOADER = 1;
    private static final int RC_EDIT_HABIT = 1234;

    @BindView(R.id.bar_chart)
    BarChart barChart;

    @BindView(R.id.tv_score)
    TextView scoreTextView;

    @BindView(R.id.sp_date_range)
    Spinner dateRangeSpinner;

    @BindView(R.id.tv_date_range)
    TextView dateRangeTextView;

    private Action mAction;
    private ActionBarChartConfigurator mBarChartConfigurator;
    private ActionBarChartRange.DateRange mBarChartRange = ActionBarChartRange.DateRange.WEEK;
    private ActionDetailViewModel mViewModel = new ActionDetailViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_habit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                editHabit();
                return true;
            case R.id.action_delete:
                delete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_EDIT_HABIT && resultCode == RESULT_OK) {
            mAction = data.getParcelableExtra(EditActionActivity.EDIT_HABIT_RESULT);
            updateUI();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void configure() {
        setContentView(R.layout.activity_detail_action);
        ButterKnife.bind(this);

        mBarChartConfigurator = new ActionBarChartConfigurator(barChart);

        getHabitFromExtras();
        configureDateSpinner();
        updateUI();
    }

    private void updateUI() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mAction.getRecord().getName());
        }

        String scoreString = mViewModel.getScoreString(mAction.getRecord().getScore());
        scoreTextView.setText(scoreString);
        dateRangeTextView.setText(mViewModel.getDateRangeString());

        getSupportLoaderManager().restartLoader(BAR_CHART_DATA_SOURCE_LOADER, null, this);
    }

    private void getHabitFromExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra(ACTION_EXTRA_KEY)) {
            mAction = intent.getParcelableExtra(ACTION_EXTRA_KEY);
        } else {
            throw new IllegalArgumentException("Put habit in the intent extras to be able to see details");
        }
    }

    private void editHabit() {
        Intent intent = new Intent(this, EditActionActivity.class);
        intent.putExtra(EditActionActivity.EDIT_ACTION_EXTRA_KEY, mAction);
        startActivityForResult(intent, RC_EDIT_HABIT);
    }

    @OnClick(R.id.bt_increase)
    void onIncreaseScoreClick() {
        final int oldScore = mAction.getRecord().getScore();
        mAction.increaseScore();
        updateScoreIfNeeded(oldScore);
    }

    @OnClick(R.id.bt_decrease)
    void onDecreaseClick() {
        final int oldScore = mAction.getRecord().getScore();
        mAction.decreaseScore();
        updateScoreIfNeeded(oldScore);
    }

    private void updateScoreIfNeeded(int oldValue) {
        if (oldValue != mAction.getRecord().getScore()) {
            updateUI();
            FirebaseSyncUtils.applyChangesForAction(mAction);
        }
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage(R.string.delete_action_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseSyncUtils.deleteHabit(mAction);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void configureDateSpinner() {
        List<String> dateRanges = ActionBarChartRange.allStringValues(this);
        ArrayAdapter<String> resetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                dateRanges);
        dateRangeSpinner.setAdapter(resetAdapter);
        dateRangeSpinner.setSelection(dateRanges.indexOf(mBarChartRange.stringValue(this)));
        dateRangeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        if (!selected.equals(mBarChartRange.stringValue(this))) {
            mBarChartRange = ActionBarChartRange.DateRange.fromString(selected, this);
            mViewModel.setDateRange(mBarChartRange);
            updateUI();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public Loader<ActionBarChartDataSource> onCreateLoader(int id, Bundle args) {
        return new ActionBarChartDataLoader(this, mAction, mBarChartRange);
    }

    @Override
    public void onLoadFinished(Loader<ActionBarChartDataSource> loader,
                               ActionBarChartDataSource dataSource) {
        ActionBarChartViewModel viewModel = new ActionBarChartViewModel(mAction, mBarChartRange);
        mBarChartConfigurator.setup(dataSource, viewModel);
        barChart.animateY(1000);
    }

    @Override
    public void onLoaderReset(Loader<ActionBarChartDataSource> loader) {
        barChart.clear();
    }

}
