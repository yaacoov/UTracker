package com.example.yaacoov.utracker.barchart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yaacoov.utracker.models.Action;


/**
 * Prefetch data for the bar chart and returns the data source object.
 */
public class ActionBarChartDataLoader extends AsyncTaskLoader<ActionBarChartDataSource> {

    private Action mAction;
    private ActionBarChartRange.DateRange mDateRange;

    public ActionBarChartDataLoader(Context context,
                                    @NonNull Action action,
                                    @NonNull ActionBarChartRange.DateRange dateRange) {
        super(context);
        this.mAction = action;
        this.mDateRange = dateRange;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ActionBarChartDataSource loadInBackground() {
        ActionBarChartDataSource dataSource = new ActionBarChartDataSource(mAction, mDateRange);
        dataSource.prefetch();
        return dataSource;
    }

}
