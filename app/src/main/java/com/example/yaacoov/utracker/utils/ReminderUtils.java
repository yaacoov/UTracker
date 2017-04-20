package com.example.yaacoov.utracker.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.receivers.ReminderReceiver;

import java.util.Calendar;
import java.util.List;

/**
 * Created by yaacoov on 17/04/17.
 * UTracker.
 */


public class ReminderUtils {

    private static final String TAG = "ReminderUtils";

    public static final String ACTION_EXTRA_KEY = "action";

    private static PendingIntent buildPendingIntent(@NonNull final Action action,
                                                    @NonNull final Context context) {
        Intent alarmIntent = new Intent(context, ReminderReceiver.class);
        alarmIntent.putExtra(ACTION_EXTRA_KEY, action);
        return PendingIntent.getBroadcast(context, (int) action.getRecord().getCreatedAt(),
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void setAlarm(@NonNull final Action action, @NonNull final Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = buildPendingIntent(action, context);

        if (!action.isReminderOn()) {
            alarmManager.cancel(pendingIntent);
        } else {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTimeInMillis(System.currentTimeMillis());
            alarmTime.set(Calendar.HOUR_OF_DAY, action.getRecord().getReminderHour());
            alarmTime.set(Calendar.MINUTE, action.getRecord().getReminderMin());
            alarmTime.set(Calendar.SECOND, 0);

            if (now.after(alarmTime)) {
                alarmTime.add(Calendar.DATE, 1);
            }

            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public static void cancelAlarm(@NonNull final Action action, @NonNull final Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = buildPendingIntent(action, context);
        alarmManager.cancel(pendingIntent);
    }

    public static void processOn(@NonNull final Action action, @NonNull final Context context) {
        if (action.isReminderOn()) {
            setAlarm(action, context);
        } else {
            cancelAlarm(action, context);
        }
    }

    public static void processAll(@NonNull final List<Action> actions, @NonNull final Context context) {
        for (Action action : actions) {
            processOn(action, context);
        }
    }

    public static void scheduleAll(@NonNull final List<Action> actions, @NonNull final Context context) {
        for (Action action : actions) {
            setAlarm(action, context);
        }
    }

    public static void cancelAll(@NonNull final List<Action> actions, @NonNull final Context context) {
        for (Action action : actions) {
            cancelAlarm(action, context);
        }
    }

    private ReminderUtils() {
    }

}
