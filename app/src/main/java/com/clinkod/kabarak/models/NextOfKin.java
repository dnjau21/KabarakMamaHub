package com.clinkod.kabarak.models;

import android.content.Context;

import com.clinkod.kabarak.preference.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class NextOfKin {
    public static final String NAME_PROPERTY = "nextofkin_name";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String ID_NUMBER = "id_number";
    public static final String PHONE_PROPERTY = "phone";
    public static final String RELATIONSHIP_PROPERTY = "relationship";
    public static final String REMOTE_ID_PROPERTY = "id";
    public static final String COMMUNITY_HEALTH_UNIT_ID_PROPERTY = "community_health_id";

    private static volatile NextOfKin instance;

    private int id;
    private String message;
    private String firstname;
    private String lastname;
    private int id_number;
    private String phone;
    private String relationship;
    private int community_health_id;


    public NextOfKin(int id, String firstname,String lastname,int id_number,String phone,String relationship,int community_health_id){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.id_number = id_number;
        this.phone = phone;
        this.relationship = relationship;
        this.community_health_id = community_health_id;
    }

    public int getId() {
        return id;
    }

    public NextOfKin() {

    }

    private static NextOfKin loadData() {
        int  id = PropertyUtils.getIntValue(REMOTE_ID_PROPERTY);
        String firstname = PropertyUtils.getStringValue(FIRST_NAME);
        String lastname = PropertyUtils.getStringValue(LAST_NAME);
        int id_number = PropertyUtils.getIntValue(ID_NUMBER);
        String phone = PropertyUtils.getStringValue(PHONE_PROPERTY);
        String relationship = PropertyUtils.getStringValue(RELATIONSHIP_PROPERTY);
        int community_health_id = PropertyUtils.getIntValue(COMMUNITY_HEALTH_UNIT_ID_PROPERTY);

        return new NextOfKin(id, firstname, lastname, id_number, phone, relationship, community_health_id);
    }

    public static void createNextOfKinDataData(int id, String firstname, String lastname, int id_number, String phone, String relationship, int community_health_id, Context context) {
        NextOfKin nextOfKinData = new NextOfKin();
        nextOfKinData.setId(id);
        nextOfKinData.setFirstname(firstname);
        nextOfKinData.setLastname(lastname);
        nextOfKinData.setId_number(id_number);
        nextOfKinData.setPhone(phone);
        nextOfKinData.setRelationship(relationship);
        nextOfKinData.setCommunity_health_id(community_health_id);

        PrefManager.saveNextOfKinDetails(id,firstname,lastname,id_number,phone,relationship,community_health_id, context);
        instance = nextOfKinData;
    }

    public JSONObject toJson(Context context) {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        int mother_id = PrefManager.getId(context.getApplicationContext());
        try {
            jsonObject.put("firstname", this.firstname);
            jsonObject.put("lastname", this.lastname);
            jsonObject.put("id_number", this.id_number);
            jsonObject.put("phone", this.phone);
            jsonObject.put("relationship", this.relationship);
            jsonObject.put("community_health_unit_id", "1");
            jsonObject.put("mother_id", String.valueOf(mother_id));


            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized static NextOfKin getNextOfKinData() {
        if (instance == null) {
            instance = loadData();
        }
        return instance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getId_number() {
        return id_number;
    }

    public void setId_number(int id_number) {
        this.id_number = id_number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public int getCommunity_health_id() {
        return community_health_id;
    }

    public void setCommunity_health_id(int community_health_id) {
        this.community_health_id = community_health_id;
    }
}
