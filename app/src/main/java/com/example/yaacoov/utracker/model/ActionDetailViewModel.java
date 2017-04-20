package com.example.yaacoov.utracker.model;

import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.barchart.ActionBarChartRange;
import com.example.yaacoov.utracker.utils.ActionDateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yaacoov on 29/03/17.
 * UTracker.
 */


public final class ActionDetailViewModel {

    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("d MMM yyyy",
            Locale.getDefault());
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("d MMM yyyy",
            Locale.getDefault());
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("MMM yyyy",
            Locale.getDefault());

    private ActionBarChartRange.DateRange mDateRange = ActionBarChartRange.DateRange.WEEK;

    public ActionDetailViewModel() {
    }

    public ActionDetailViewModel(@NonNull ActionBarChartRange.DateRange dateRange) {
        this.mDateRange = dateRange;
    }

    public void setDateRange(ActionBarChartRange.DateRange dateRange) {
        this.mDateRange = dateRange;
    }

    public String getScoreString(int score) {
        return String.valueOf(score);
    }

    public String getDateRangeString() {
        switch (mDateRange) {
            case WEEK:
                return getFormattedWeek(ActionDateUtils.getStartOfCurrentWeek(),
                        ActionDateUtils.getEndOfCurrentWeek());
            case MONTH:
                return getFormattedMonth(ActionDateUtils.getStartOfCurrentMonth(),
                        ActionDateUtils.getEndOfCurrentMonth());
            case YEAR:
                return getFormattedYear(ActionDateUtils.getStartOfCurrentYear(),
                        ActionDateUtils.getEndOfCurrentYear());
            default:
                throw new IllegalArgumentException("Receive illegal date range");
        }
    }

    private String getFormattedWeek(long start, long end) {
        return formatDates(WEEK_FORMAT, start, end);
    }

    private String getFormattedMonth(long start, long end) {
        return formatDates(MONTH_FORMAT, start, end);
    }

    private String getFormattedYear(long start, long end) {
        return formatDates(YEAR_FORMAT, start, end);
    }

    private String formatDates(SimpleDateFormat dateFormat, long start, long end) {
        return dateFormat.format(new Date(start))
                + " - "
                + dateFormat.format(new Date(end));
    }

}
