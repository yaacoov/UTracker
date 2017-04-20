package com.example.yaacoov.utracker.utils;

import android.support.annotation.NonNull;

import com.example.yaacoov.utracker.models.ResetFrequency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaacoov on 25/03/17.
 * UTracker.
 */


public final class ActionListUtils {

    private List<Long> mDates = new ArrayList<>();

    public ActionListUtils(@NonNull List<Long> dates) {
        this.mDates = new ArrayList<>(dates);
    }


    public List<Long> filteredBy(ResetFrequency.Type type) {
        if (type == ResetFrequency.Type.NEVER) return mDates;

        List<Long> resultList = new ArrayList<>(mDates.size());
        for (Long date : mDates) {
            if (ActionDateUtils.isDateInType(date, type)) resultList.add(date);
        }

        return resultList;
    }
}
