package com.example.yaacoov.utracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.analytics.ActionAnalytics;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ActionRecord;
import com.example.yaacoov.utracker.models.ReminderTime;
import com.example.yaacoov.utracker.models.ResetFrequency;
import com.example.yaacoov.utracker.pickers.TimePickerFragment;
import com.example.yaacoov.utracker.sync.FirebaseSyncUtils;
import com.example.yaacoov.utracker.utils.ActionScoreUtils;
import com.example.yaacoov.utracker.utils.ReminderUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Created by yaacoov on 19/03/17.
 * UTracker.
 */

public class EditActionActivity extends AppCompatActivity implements TimePickerFragment.OnTimeSetListener {

    public static final String EDIT_HABIT_RESULT = "com.ivanmagda.habito.activities.edit_result";
    public static final String EDIT_ACTION_EXTRA_KEY = "com.ivanmagda.habito.activities.edit";

    private static final String TAG = "EditActionActivity";

    @BindView(R.id.et_habit_name)
    EditText nameEditText;

    @BindView(R.id.spinner_reset)
    Spinner resetFrequencySpinner;

    @BindView(R.id.et_habit_target)
    EditText targetEditText;

    @BindView(R.id.tv_reminder_time)
    TextView reminderTimeTextView;

    /**
     * The original habit.
     * If original habit is not null, then we are in editing mode, otherwise creating new.
     */
    private Action mOriginalAction;
    private Action mEditingAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_habit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                onBackPressed();
                return true;
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configure() {
        setContentView(R.layout.activity_edit_action);

        ButterKnife.bind(this);
        getExtras();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int titleId = (mOriginalAction == null ? R.string.activity_create_label
                    : R.string.activity_edit_label);
            actionBar.setTitle(titleId);
        }

        ActionRecord record = mEditingAction.getRecord();
        nameEditText.setText(record.getName());
        if (record.getColor() != ActionRecord.DEFAULT_COLOR) {
            nameEditText.setTextColor(record.getColor());
        }

        targetEditText.setText(String.valueOf(record.getTarget()));
        updateTimeText();

        List<String> resetFrequencies = Arrays.asList(ResetFrequency.ALL);
        ArrayAdapter<String> resetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                resetFrequencies);
        resetFrequencySpinner.setAdapter(resetAdapter);
        resetFrequencySpinner.setPrompt(getResources().getString(R.string.spinner_prompt));
        String selection = (mOriginalAction == null ? ResetFrequency.NEVER
                : mOriginalAction.getRecord().getResetFreq());
        resetFrequencySpinner.setSelection(resetFrequencies.indexOf(selection));
        resetFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                mEditingAction.getRecord().setResetFreq(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra(EDIT_ACTION_EXTRA_KEY)) {
            mOriginalAction = intent.getParcelableExtra(EDIT_ACTION_EXTRA_KEY);
            mEditingAction = mOriginalAction.copy();
        } else {
            mOriginalAction = null;
            mEditingAction = new Action();
        }
    }

    @OnClick(R.id.tv_reminder_time)
    void onDateSpinnerClick() {
        TimePickerFragment timePickerFragment;
        if (mEditingAction.isReminderOn()) {
            ActionRecord record = mEditingAction.getRecord();
            timePickerFragment = TimePickerFragment.newInstance(record.getReminderHour(),
                    record.getReminderMin());
        } else {
            timePickerFragment = new TimePickerFragment();
        }
        timePickerFragment.setOnTimeSetListener(this);
        timePickerFragment.show(getSupportFragmentManager(), "TimePicker");
    }

    @OnClick(R.id.bt_pick_color)
    void showColorPicker() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setDefaultColorButton(mEditingAction.getRecord().getColor());
        colorPicker.getPositiveButton().setTextColor(getResources().getColor(R.color.colorAccent));
        colorPicker.setRoundColorButton(true);
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                mEditingAction.getRecord().setColor(color);
                nameEditText.setTextColor(color);
            }

            @Override
            public void onCancel() {
            }
        });
        colorPicker.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mEditingAction.getRecord().setReminderHour(hourOfDay);
        mEditingAction.getRecord().setReminderMin(minute);
        updateTimeText();
    }

    @Override
    public void onCancel() {
        mEditingAction.getRecord().setReminderHour(ActionRecord.REMINDER_OFF);
        mEditingAction.getRecord().setReminderMin(ActionRecord.REMINDER_OFF);
        updateTimeText();
    }

    private void updateTimeText() {
        if (mEditingAction.isReminderOn()) {
            ActionRecord record = mEditingAction.getRecord();
            reminderTimeTextView.setText(ReminderTime.getTimeString(record.getReminderHour(),
                    record.getReminderMin()));
        } else {
            reminderTimeTextView.setText(R.string.off);
        }
    }

    private void save() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || !isInputCorrect()) return;

        putChanges();

        if (mOriginalAction == null) {
            createNew();
        } else {
            applyChanges();
        }
    }

    private void createNew() {
        ActionAnalytics.logCreateHabitWithName(mEditingAction.getRecord().getName());
        FirebaseSyncUtils.createNewHabitRecord(mEditingAction.getRecord());
        onBackPressed();
    }

    private void applyChanges() {
        Intent data = new Intent();
        data.putExtra(EDIT_HABIT_RESULT, mEditingAction);
        setResult(RESULT_OK, data);

        ReminderUtils.processOn(mEditingAction, this);
        ActionScoreUtils.resetScore(mEditingAction);
        FirebaseSyncUtils.applyChangesForAction(mEditingAction);

        finish();
    }

    private void putChanges() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        long now = System.currentTimeMillis();

        ActionRecord record = mEditingAction.getRecord();
        if (mOriginalAction == null) {
            record.setCreatedAt(now);
            record.setResetTimestamp(now);
        }
        record.setUserId(currentUser.getUid());
        record.setName(nameEditText.getText().toString().trim());
    }

    private boolean isInputCorrect() {
        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.toast_empty_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        String targetString = targetEditText.getText().toString().trim();
        if (TextUtils.isEmpty(targetString)) {
            Toast.makeText(this, R.string.toast_target_empty, Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            mEditingAction.getRecord().setTarget(Integer.parseInt(targetString));
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_failed_target_value, Toast.LENGTH_LONG).show();
            targetEditText.requestFocus();
            return false;
        }

        return true;
    }

}
