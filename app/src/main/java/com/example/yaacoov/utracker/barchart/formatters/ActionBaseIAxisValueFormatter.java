package com.example.yaacoov.utracker.barchart.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public abstract class ActionBaseIAxisValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf(value);
    }

    public abstract long getDateForValue(float value);

}
