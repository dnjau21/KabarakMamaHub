package com.clinkod.kabarak.models;

import com.clinkod.kabarak.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Deprecated
@Entity
public class HourlyData{

    @Id
    public long _id;

    private Integer id;
    private Date timeTaken;
    private int heartRate;
    private int systolicBp;
    private int diastolicBp;
    private String activity;
    private String mood;
    private Date synchronizedAt;
    private String remoteId;

    public HourlyData(){}

    public HourlyData(Integer id, Date timeTaken, int heartRate, int systolicBp, int diastolicBp,
                      String activity, String mood){
        this.id = id;
        this.timeTaken = timeTaken;
        this.heartRate = heartRate;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.activity = activity;
        this.mood = mood;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            jsonObject.put("timeTaken", simpleDateFormat.format(this.timeTaken));
            jsonObject.put("heartRate", this.heartRate);
            jsonObject.put("systolicBp", this.systolicBp);
            jsonObject.put("diastolicBp", this.diastolicBp);
            jsonObject.put("activity", this.activity);
            jsonObject.put("mood", this.mood);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HourlyData fromJson(JSONObject data) {
        try {
            Integer id = data.getInt("id");
            String timeTakenStr = data.getString("timeTaken");
            int heartRate = data.getInt("heartRate");
            int systolicBp = data.getInt("systolicBp");
            int diastolicBp = data.getInt("diastolicBp");
            String activity = data.getString("activity_id");
            String mood = data.getString("mood");

            return new HourlyData(id, Utils.convertStringToDate(timeTakenStr), heartRate, systolicBp, diastolicBp, activity, mood);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getId() {
        if(this.id == null)
            return -1;

        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }


    public Date getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Date timeTaken) {
        this.timeTaken = timeTaken;
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

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
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