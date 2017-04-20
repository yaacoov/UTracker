package com.example.yaacoov.utracker.sync;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;

import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ActionRecord;
import com.example.yaacoov.utracker.widget.DetailWidgetProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaacoov on 24/03/17.
 * UTracker.
 */

public class WidgetFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Query mUserHabitsQuery;
    private ValueEventListener mValueEventListener;
    public static List<Action> actionList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        fetchData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchData() {
        mUserHabitsQuery = FirebaseSyncUtils.getCurrentUserHabitsQuery();
        if (mUserHabitsQuery == null) {
            populateWidget();
        } else {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    processOnDataChange(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mUserHabitsQuery.addListenerForSingleValueEvent(mValueEventListener);
        }
    }

    private void processOnDataChange(DataSnapshot dataSnapshot) {
        actionList = new ArrayList<>((int) dataSnapshot.getChildrenCount());
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            ActionRecord parsedRecord = data.getValue(ActionRecord.class);
            actionList.add(new Action(data.getKey(), parsedRecord));
        }
        populateWidget();
    }

    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == DetailWidgetProvider.DATA_FETCHED
     */
    private void populateWidget() {
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(DetailWidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }

}
