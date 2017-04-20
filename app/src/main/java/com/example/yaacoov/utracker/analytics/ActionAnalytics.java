package com.example.yaacoov.utracker.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.models.Action;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by yaacoov on 20/03/17.
 * UTracker.
 */

public final class ActionAnalytics {

    private static FirebaseAnalytics firebaseAnalytics;

    public static void initAnalytics(Context context) {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static FirebaseAnalytics getInstance(Context context) {
        if (firebaseAnalytics == null) initAnalytics(context);
        return firebaseAnalytics;
    }

    public static void logCreateHabitWithName(@NonNull final String name) {
        Bundle params = new Bundle();
        params.putString("action_name", name);
        firebaseAnalytics.logEvent("create_action", params);
    }

    public static void logAppOpen() {
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.START_DATE, System.currentTimeMillis());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    public static void logLogin(FirebaseUser user) {
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.START_DATE, System.currentTimeMillis());
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, user.getUid());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, user.getDisplayName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public static void logViewHabitListItem(Action action) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, action.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, action.getRecord().getName());
        bundle.putInt(FirebaseAnalytics.Param.SCORE, action.getRecord().getScore());
        bundle.putInt("color", action.getRecord().getColor());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    private ActionAnalytics() {
    }

}
