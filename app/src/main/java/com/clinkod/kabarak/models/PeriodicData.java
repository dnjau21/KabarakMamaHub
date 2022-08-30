package com.clinkod.kabarak.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

import static com.clinkod.kabarak.utils.Utils.convertStringToDate;

@Deprecated
@Entity
public class PeriodicData{
    @Id
    public long _id;

    private String remoteId;
    private Date dateRecorded;
    private String exerciseFrequency;
    private String alcoholConsumption;
    private int numberOfCigarettes;
    private String sleepPattern;
    private String medication;
    private Date synchronizedAt;

    public PeriodicData(){}

    public PeriodicData(Date dateRecorded, String exerciseFrequency,
                        String alcoholConsumption, int numberOfCigarettes,
                        String sleepPattern, String medication){
        this.dateRecorded = dateRecorded;
        this.exerciseFrequency = exerciseFrequency;
        this.alcoholConsumption = alcoholConsumption;
        this.numberOfCigarettes = numberOfCigarettes;
        this.sleepPattern = sleepPattern;
        this.medication = medication;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            jsonObject.put("dateRecorded", simpleDateFormat.format(this.dateRecorded));
            jsonObject.put("exerciseFrequency", this.exerciseFrequency);
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


    public static PeriodicData fromJson(JSONObject data) {
        try {
            String remoteId = data.getString("id");
            String dateRecorded = data.getString("dateRecorded");
            double weight = data.getDouble("weight");
            String exerciseFrequency = data.getString("exerciseFrequency");
            String alcoholConsumption = data.getString("alcoholConsumption");
            int numberOfCigarettes = data.getInt("numberOfCigarettes");
            String sleepPattern = data.getString("sleepPattern");
            String medication = data.getString("medication");

            return new PeriodicData(convertStringToDate(dateRecorded), exerciseFrequency,
                    alcoholConsumption, numberOfCigarettes, sleepPattern, medication);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Date getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Date dateRecorded) {
        this.dateRecorded = dateRecorded;
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

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getExerciseFrequency() {
        return exerciseFrequency;
    }

    public void setExerciseFrequency(String exerciseFrequency) {
        this.exerciseFrequency = exerciseFrequency;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public void setSleepPattern(String sleepPattern) {
        this.sleepPattern = sleepPattern;
    }

    public String getSleepPattern() {
        return sleepPattern;
    }

    public Date getSynchronizedAt() {
        return synchronizedAt;
    }

    public void setSynchronizedAt(Date synchronizedAt) {
        this.synchronizedAt = synchronizedAt;
    }
}

