package com.clinkod.kabarak.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class DailyData {
    @Id
    public long _id;

    private String remoteId;
    private Date dateRecorded;
    private Boolean exercised;
    private String alcoholConsumption;
    private int numberOfCigarettes;
    private String sleepPattern;
    private String medication;
    private Date synchronizedAt;

    public DailyData() {
    }

    public DailyData(String remoteId, Date dateRecorded, Boolean exercised, String alcoholConsumption,
                     int numberOfCigarettes, String sleepPattern, String medication, Date synchronizedAt) {
        this.remoteId = remoteId;
        this.dateRecorded = dateRecorded;
        this.exercised = exercised;
        this.alcoholConsumption = alcoholConsumption;
        this.numberOfCigarettes = numberOfCigarettes;
        this.sleepPattern = sleepPattern;
        this.medication = medication;
        this.synchronizedAt = synchronizedAt;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            jsonObject.put("dateRecorded", simpleDateFormat.format(this.dateRecorded));
            jsonObject.put("exercised", this.exercised);
            jsonObject.put("alcoholConsumption", this.alcoholConsumption);
            jsonObject.put("numberOfCigarettes", this.numberOfCigarettes);
            jsonObject.put("sleepPattern", this.sleepPattern);
            jsonObject.put("medication", this.medication);

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

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public Date getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Date dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public Boolean getExercised() {
        return exercised;
    }

    public void setExercised(Boolean exercised) {
        this.exercised = exercised;
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public int getNumberOfCigarettes() {
        return numberOfCigarettes;
    }

    public void setNumberOfCigarettes(int numberOfCigarettes) {
        this.numberOfCigarettes = numberOfCigarettes;
    }

    public String getSleepPattern() {
        return sleepPattern;
    }

    public void setSleepPattern(String sleepPattern) {
        this.sleepPattern = sleepPattern;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public Date getSynchronizedAt() {
        return synchronizedAt;
    }

    public void setSynchronizedAt(Date synchronizedAt) {
        this.synchronizedAt = synchronizedAt;
    }
}
