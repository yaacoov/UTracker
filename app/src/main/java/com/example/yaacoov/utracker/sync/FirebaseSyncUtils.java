package com.example.yaacoov.utracker.sync;

import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.models.Action;
import com.example.yaacoov.utracker.models.ActionRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by yaacoov on 24/03/17.
 * UTracker.
 */


public final class FirebaseSyncUtils {

    public static final String HABITS_REFERENCE_PATH = "actions";
    public static final String USER_ID_KEY = "userId";

    public static DatabaseReference getHabitsReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference().child(HABITS_REFERENCE_PATH);
    }

    public static Query getCurrentUserHabitsQuery() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return getHabitsReference().orderByChild(USER_ID_KEY).equalTo(user.getUid());
        }
        return null;
    }

    public static void setOfflineModeEnabled(boolean enabled) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(enabled);
    }

    public static void createNewHabitRecord(ActionRecord record) {
        getHabitsReference().push().setValue(record);
    }


    public static void deleteHabit(@NonNull final Action action) {
        getHabitsReference().child(action.getId()).setValue(null);
    }

    private FirebaseSyncUtils() {
    }





    public static void applyChangesForAction(@NonNull final Action action) {
        getHabitsReference().child(action.getId()).setValue(action.getRecord());
    }
}
