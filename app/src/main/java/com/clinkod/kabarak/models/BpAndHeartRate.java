package com.clinkod.kabarak.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class BpAndHeartRate {
    @Id
    public long _id;

    private Date timeTaken;
    private int heartRate;
    private int systolicBp;
    private int diastolicBp;
    private String remoteId;
    private int mother_id;
    private String category;
    public String random_id;
    public String activity;
    public String mood;

    public BpAndHeartRate() {
    }

    public BpAndHeartRate(Date timeTaken, int heartRate, int systolicBp, int diastolicBp, String remoteId, int mother_id, String category, String random_id, String activity, String mood) {
        this.timeTaken = timeTaken;
        this.heartRate = heartRate;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.remoteId = remoteId;
        this.mother_id = mother_id;
        this.category = category;
        this.random_id = random_id;
        this.activity = activity;
        this.mood = mood;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String getTimestamp = String.valueOf(System.currentTimeMillis());
        try {
            jsonObject.put("dateMeasured", simpleDateFormat.format(this.timeTaken));
            jsonObject.put("heartRate", this.heartRate);
            jsonObject.put("systole", this.systolicBp);
            jsonObject.put("diastole", this.diastolicBp);
            jsonObject.put("heart_rate", this.heartRate);
            jsonObject.put("mother_id", this.mother_id);
            jsonObject.put("category", this.category);
            jsonObject.put("random_id", this.random_id);
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

    public Date getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Date timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(int systolicBp) {
        this.systolicBp = systolicBp;
    }

    public int getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(int diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public int getMother_id() {
        return mother_id;
    }

    public void setMother_id(int mother_id) {
        this.mother_id = mother_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getRandom_id() {
        return random_id;
    }

    public void setRandom_id(String random_id) {
        this.random_id = random_id;
    }
}
