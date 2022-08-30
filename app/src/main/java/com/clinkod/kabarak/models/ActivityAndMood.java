package com.clinkod.kabarak.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ActivityAndMood {
    @Id
    public long _id;

    private Date timeRecorded;
    private String activity;
    private String mood;
    private Date synchronizedAt;
    private String remoteId;

    public ActivityAndMood() {
    }

    public ActivityAndMood(Date timeRecorded, String activity, String mood, Date synchronizedAt, String remoteId) {
        this.timeRecorded = timeRecorded;
        this.activity = activity;
        this.mood = mood;
        this.synchronizedAt = synchronizedAt;
        this.remoteId = remoteId;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            jsonObject.put("timeTaken", simpleDateFormat.format(this.timeRecorded));
            jsonObject.put("activity", this.activity);
            jsonObject.put("mood", this.mood);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public Date getTimeRecorded() {
        return timeRecorded;
    }

    public void setTimeRecorded(Date timeRecorded) {
        this.timeRecorded = timeRecorded;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Date getSynchronizedAt() {
        return synchronizedAt;
    }

    public void setSynchronizedAt(Date synchronizedAt) {
        this.synchronizedAt = synchronizedAt;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }
}
