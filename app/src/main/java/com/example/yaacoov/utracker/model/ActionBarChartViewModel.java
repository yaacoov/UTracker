package com.example.yaacoov.utracker.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.barchart.ActionBarChartRange;
import com.example.yaacoov.utracker.barchart.formatters.ActionBaseIAxisValueFormatter;
import com.example.yaacoov.utracker.barchart.formatters.MonthAxisValueFormatter;
import com.example.yaacoov.utracker.barchart.formatters.WeekDayAxisValueFormatter;
import com.example.yaacoov.utracker.barchart.formatters.YearAxisValueFormatter;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.utils.ActionStringUtils;

/**
 * Created by yaacoov on 01/04/17.
 * UTracker.
 */


public class ActionBarChartViewModel {

    private Action mAction;
    private ActionBarChartRange.DateRange mDateRange;

    public ActionBarChartViewModel(Action action, ActionBarChartRange.DateRange dateRange) {
        this.mAction = action;
        this.mDateRange = dateRange;
    }

    public ActionBaseIAxisValueFormatter getXAxisFormatter() {
        switch (mDateRange) {
            case WEEK:
                return new WeekDayAxisValueFormatter();
            case MONTH:
                return new MonthAxisValueFormatter();
            case YEAR:
                return new YearAxisValueFormatter();
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }

    public String getBarDataSetName(@NonNull final Context context) {
        Resources resources = context.getResources();
        switch (mDateRange) {
            case WEEK:
                String week = resources.getString(R.string.week).toLowerCase();
                return ActionStringUtils.capitalized(
                        resources.getString(R.string.bar_chart_set_name, week));
            case MONTH:
                String month = resources.getString(R.string.month).toLowerCase();
                return ActionStringUtils.capitalized(
                        resources.getString(R.string.bar_chart_set_name, month));
            case YEAR:
                String year = resources.getString(R.string.year).toLowerCase();
                return ActionStringUtils.capitalized(
                        resources.getString(R.string.bar_chart_set_name, year));
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }

}
