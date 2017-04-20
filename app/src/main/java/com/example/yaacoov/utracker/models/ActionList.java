package com.example.yaacoov.utracker.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yaacoov on 20/03/17.
 * UTracker.
 */


public final class ActionList {

    public enum SortOrder {
        NAME(0), DATE(1);

        private final int value;

        SortOrder(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static SortOrder fromValue(int value) {
            if (value == NAME.getValue()) {
                return NAME;
            } else if (value == DATE.getValue()) {
                return DATE;
            } else {
                throw new IllegalArgumentException("Illegal sort type value");
            }
        }
    }

    private List<Action> mActions;
    private SortOrder mSortOrder;

    public ActionList(@NonNull List<Action> actions, @NonNull SortOrder sortOrder) {
        this.mActions = new ArrayList<>(actions);
        this.mSortOrder = sortOrder;
        sort();
    }

    public ActionList(List<Action> actions) {
        this(actions, SortOrder.NAME);
    }

    public List<Action> getHabits() {
        return mActions;
    }

    public void setHabits(List<Action> actions) {
        if (actions == null) {
            clear();
        } else {
            this.mActions = actions;
            sort();
        }
    }

    public SortOrder getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        if (mSortOrder != sortOrder) {
            this.mSortOrder = sortOrder;
            sort();
        }
    }

    public void add(@NonNull final Action action) {
        this.mActions.add(action);
        sort();
    }

    public void clear() {
        this.mActions.clear();
    }

    private void sort() {
        if (mActions.size() == 0) return;
        switch (mSortOrder) {
            case NAME:
                Collections.sort(mActions, new SortByName());
                break;
            case DATE:
                // Sort in decreasing order.
                Collections.sort(mActions, new SortByDate());
                Collections.reverse(mActions);
                break;
        }
    }

    // Sort in increasing order.
    private class SortByName implements Comparator<Action> {
        @Override
        public int compare(Action lhs, Action rhs) {
            String lhsName = lhs.getRecord().getName();
            String rhsName = rhs.getRecord().getName();
            return lhsName.toLowerCase().compareTo(rhsName.toLowerCase());
        }
    }

    private class SortByDate implements Comparator<Action> {
        @Override
        public int compare(Action lhs, Action rhs) {
            Long lhsDate = lhs.getRecord().getCreatedAt();
            Long rhsDate = rhs.getRecord().getCreatedAt();
            return lhsDate.compareTo(rhsDate);
        }
    }

}
