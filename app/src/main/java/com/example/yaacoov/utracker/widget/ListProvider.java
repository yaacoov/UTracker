package com.example.yaacoov.utracker.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.activities.DetailActionActivity;
import com.example.yaacoov.utracker.model.ActionListItemViewModel;
import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.sync.WidgetFetchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaacoov on 18/04/17.
 */



public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<Action> mActionList = new ArrayList<>();
    private Context mContext = null;
    private int appWidgetId;

    public ListProvider(Context context, Intent intent) {
        this.mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        populateListItem();
    }

    private void populateListItem() {
        if (WidgetFetchService.actionList != null) {
            mActionList = new ArrayList<>(WidgetFetchService.actionList);
        } else {
            mActionList = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return mActionList.size();
    }

    @Override
    public long getItemId(int position) {
        return mActionList.get(position).getRecord().getCreatedAt();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Action action = mActionList.get(position);
        ActionListItemViewModel viewModel = new ActionListItemViewModel(mContext, action);

        final RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);
        views.setTextViewText(R.id.tv_widget_name, viewModel.getHabitName());
        views.setTextViewText(R.id.tv_widget_score, viewModel.getScore());

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(DetailActionActivity.ACTION_EXTRA_KEY, action);
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return views;
    }


    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}
