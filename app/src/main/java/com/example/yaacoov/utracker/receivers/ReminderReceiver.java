package com.example.yaacoov.utracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.sync.ReminderIntentService;
import com.example.yaacoov.utracker.utils.ReminderUtils;

/**
 * Created by yaacoov on 10/04/17.
 * UTracker.
 */


public class ReminderReceiver extends BroadcastReceiver {

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(ReminderUtils.ACTION_EXTRA_KEY)) {
            Action action = intent.getParcelableExtra(ReminderUtils.ACTION_EXTRA_KEY);
            Intent serviceIntent = ReminderIntentService.buildInstance(action, context);
            context.startService(serviceIntent);
        }
    }

}
