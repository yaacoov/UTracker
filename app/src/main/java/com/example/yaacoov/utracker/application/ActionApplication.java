package com.example.yaacoov.utracker.application;

import android.app.Application;

import com.example.yaacoov.utracker.analytics.ActionAnalytics;
import com.example.yaacoov.utracker.sync.FirebaseSyncUtils;

/**
 * Created by yaacoov on 20/03/17.
 * UTracker.
 */

public class ActionApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActionAnalytics.initAnalytics(this);
        ActionAnalytics.logAppOpen();
        FirebaseSyncUtils.setOfflineModeEnabled(true);
    }

}
