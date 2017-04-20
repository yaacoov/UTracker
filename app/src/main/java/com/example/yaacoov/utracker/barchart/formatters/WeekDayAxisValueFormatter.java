package com.example.yaacoov.utracker.barchart.formatters;

import com.github.mikephil.charting.components.AxisBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.yaacoov.utracker.utils.ActionDateUtils.getCalendarWithTime;
import static com.example.yaacoov.utracker.utils.ActionDateUtils.getStartOfCurrentWeek;

public class WeekDayAxisValueFormatter extends ActionBaseIAxisValueFormatter {

    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE", Locale.getDefault());

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return FORMATTER.format(new Date(getDateForValue(value)));
    }

    @Override
    public long getDateForValue(float value) {
        Calendar calendar = getCalendarWithTime(getStartOfCurrentWeek());
        calendar.add(Calendar.DATE, (int) value);
        return calendar.getTimeInMillis();
    }

}

