package com.example.yaacoov.utracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.R;
import com.example.yaacoov.utracker.models.ActionList;

/**
 * Created by yaacoov on 18/04/17.
 * UTracker.
 */


public final class SharedPreferencesUtils {

    public static ActionList.SortOrder getSortOrder(@NonNull final Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        int defaultValue = ActionList.SortOrder.NAME.getValue();
        int sortOrder = sharedPreferences.getInt(
                context.getString(R.string.saved_sort_order), defaultValue);
        return ActionList.SortOrder.fromValue(sortOrder);
    }

    public static void setSortOrder(@NonNull final Context context, ActionList.SortOrder sortOrder) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.saved_sort_order), sortOrder.getValue());
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(@NonNull final Context context) {
        return context.getSharedPreferences(context.getString(R.string.preference_sort_file_key),
                Context.MODE_PRIVATE);
    }

    private SharedPreferencesUtils() {
    }

}
