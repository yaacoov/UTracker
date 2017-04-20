package com.example.yaacoov.utracker.utils;

import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ActionRecord;
import com.example.yaacoov.utracker.models.ResetFrequency;
import com.example.yaacoov.utracker.sync.FirebaseSyncUtils;

import java.util.List;

/**
 * Created by yaacoov on 24/03/17.
 * UTracker.
 */


public final class ActionScoreUtils {

    public static void processAll(@NonNull final List<Action> actions) {
        for (Action action : actions) {
            if (isNeedToResetScore(action)) {
                resetScore(action);
                FirebaseSyncUtils.applyChangesForAction(action);
            }
        }
    }

    public static void increaseScore(@NonNull final Action action) {
        action.getRecord().getCheckmarks().add(System.currentTimeMillis());
        reloadScoreValue(action);
    }

    public static void decreaseScore(@NonNull final Action action) {
        ActionRecord record = action.getRecord();

        int checkmarksCount = record.getCheckmarks().size();
        if (checkmarksCount <= 0) return;

        long lastCheckmark = record.getCheckmarks().get(checkmarksCount - 1);
        ResetFrequency.Type type = ResetFrequency.typeFrom(record.getResetFreq());

        if (ActionDateUtils.isDateInType(lastCheckmark, type)) {
            removeLastCheckmark(action);
            reloadScoreValue(action);
        }
    }

    public static boolean isNeedToResetScore(@NonNull final Action action) {
        long lastReset = action.getRecord().getResetTimestamp();
        ResetFrequency.Type type = ResetFrequency.typeFrom(action.getRecord().getResetFreq());
        return !ActionDateUtils.isDateInType(lastReset, type);
    }

    public static void resetScoreIfNeeded(@NonNull final Action action) {
        if (isNeedToResetScore(action)) resetScore(action);
    }

    public static void resetScore(@NonNull final Action action) {
        action.getRecord().setResetTimestamp(System.currentTimeMillis());
        reloadScoreValue(action);
    }

    public static void reloadScoreValue(@NonNull final Action action) {
        ActionRecord record = action.getRecord();
        ResetFrequency resetFrequency = new ResetFrequency(record.getResetFreq());

        ActionListUtils listUtils = new ActionListUtils(record.getCheckmarks());
        List<Long> filtered = listUtils.filteredBy(resetFrequency.getType());
        record.setScore(filtered.size());
    }

    private static void removeLastCheckmark(@NonNull final Action action) {
        ActionRecord record = action.getRecord();
        if (record.getCheckmarks().size() > 0) {
            removeCheckmarkAtIndex(action, record.getCheckmarks().size() - 1);
        }
    }

    private static void removeCheckmarkAtIndex(@NonNull final Action action, int index) {
        ActionRecord record = action.getRecord();
        if (index < 0 || index >= record.getCheckmarks().size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            record.getCheckmarks().remove(index);
        }
    }

}
