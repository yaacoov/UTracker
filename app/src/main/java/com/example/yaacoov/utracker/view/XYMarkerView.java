package com.example.yaacoov.utracker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.barchart.formatters.ActionBaseIAxisValueFormatter;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Locale;

@SuppressLint("ViewConstructor")
public class XYMarkerView extends MarkerView {

    private static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

    private TextView mContentTextView;
    private ActionBaseIAxisValueFormatter mXAxisValueFormatter;

    public XYMarkerView(Context context, ActionBaseIAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);
        this.mXAxisValueFormatter = xAxisValueFormatter;
        this.mContentTextView = (TextView) findViewById(R.id.tvContent);
    }

    // Callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long date = mXAxisValueFormatter.getDateForValue(e.getX());
        mContentTextView.setText(FORMATTER.format(date) + ". " + String.valueOf((int) e.getY()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
