package com.example.yaacoov.utracker.utils;

import android.support.annotation.NonNull;

/**
 * Created by yaacoov on 18/04/17.
 * UTracker.
 */


public final class ActionStringUtils {

    public static String capitalized(@NonNull final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private ActionStringUtils() {
    }

}
