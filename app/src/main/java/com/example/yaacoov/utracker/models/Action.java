package com.example.yaacoov.utracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.yaacoov.utracker.utils.ActionScoreUtils;


/**
 * Created by yaacoov on 19/03/17.
 * UTracker.
 */


public final class Action implements Parcelable, Cloneable {

    private String mId;
    private ActionRecord mRecord;

    public Action() {
        this.mRecord = new ActionRecord();
    }

    public Action(String id, ActionRecord record) {
        this.mId = id;
        this.mRecord = record;
    }

    public Action(Parcel in) {
        this.mId = in.readString();
        this.mRecord = in.readParcelable(ActionRecord.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeParcelable(mRecord, flags);
    }

    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel parcel) {
            return new Action(parcel);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    public Action copy() {
        return new Action(mId, mRecord.copy());
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public ActionRecord getRecord() {
        return mRecord;
    }

    public void setRecord(ActionRecord record) {
        this.mRecord = record;
    }

    public boolean isReminderOn() {
        return (mRecord.getReminderHour() != ActionRecord.REMINDER_OFF &&
                mRecord.getReminderMin() != ActionRecord.REMINDER_OFF);
    }

    public synchronized void increaseScore() {
        ActionScoreUtils.increaseScore(this);
    }

    public synchronized void decreaseScore() {
        ActionScoreUtils.decreaseScore(this);
    }

    @Override
    public String toString() {
        return "Action{" +
                "id='" + mId + '\'' +
                ", record=" + mRecord +
                '}';
    }

}
