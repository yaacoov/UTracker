package com.example.yaacoov.utracker.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ResetFrequency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yaacoov on 25/04/17.
 * UTracker.
 */


public class ActionListItemViewModel {

    private Action mAction;
    private Context mContext;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

    public ActionListItemViewModel(Context context) {
        this.mContext = context;
    }

    public ActionListItemViewModel(Context context, Action action) {
        this.mContext = context;
        this.mAction = action;
    }

    public void setAction(Action action) {
        this.mAction = action;
    }

    public int getBackgroundColor() {
        return mAction.getRecord().getColor();
    }

    public String getHabitName() {
        return mAction.getRecord().getName();
    }

    public int getHabitNameTextColor() {
        int color = mAction.getRecord().getColor();
        if (color == Color.WHITE) {
            return mContext.getResources().getColor(R.color.primary_text);
        } else {
            return Color.WHITE;
        }
    }

    public String getResetFreq() {
        Resources resources = mContext.getResources();
        switch (mAction.getRecord().getResetFreq()) {
            case ResetFrequency.DAY:
                return resources.getString(R.string.list_item_reset_today);
            case ResetFrequency.WEEK:
                return resetFreqStringWithParameter(ResetFrequency.WEEK);
            case ResetFrequency.MONTH:
                return resetFreqStringWithParameter(ResetFrequency.MONTH);
            case ResetFrequency.YEAR:
                return resetFreqStringWithParameter(ResetFrequency.YEAR);
            case ResetFrequency.NEVER:
                Date date = new Date(mAction.getRecord().getCreatedAt());
                return resources.getString(R.string.list_item_reset_never, FORMAT.format(date));
            default:
                throw new IllegalArgumentException("Unsupported reset freq time");
        }
    }

    public String getScore() {
        final int score = mAction.getRecord().getScore();
        final int target = mAction.getRecord().getTarget();
        if (target > 0) {
            return String.valueOf(score) + "/"
                    + String.valueOf(target);
        } else {
            return String.valueOf(score);
        }
    }

    private String resetFreqStringWithParameter(String freqPeriod) {
        return mContext.getResources().getString(R.string.list_item_reset_freq, freqPeriod);
    }

}
