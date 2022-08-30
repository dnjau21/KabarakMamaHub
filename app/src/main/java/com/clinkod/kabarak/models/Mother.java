package com.clinkod.kabarak.models;

import android.content.Context;

import com.clinkod.kabarak.preference.PrefManager;

public class Mother {
    public static final String NAME_PROPERTY = "monther_name";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String ID_NUMBER = "id_number";
    public static final String PHONE_PROPERTY = "phone";
    public static final String DOB_PROPERTY = "dob";
    public static final String REMOTE_ID_PROPERTY = "id";
    public static final String CHV_ID_PROPERTY = "chv_id";
    public static final String OTP_PROPERTY = "otp";

    private static volatile Mother instance;

    private int id;
    private String name;
    private String message;
    private String firstname;
    private String lastname;
    private int id_number;
    private String phone;
    private String dob;
    private int chv_id;
    private int otp;

    public Mother(int id,String name, String firstname,String lastname,int id_number,String phone,String dob,int chv_id,int otp){
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.id_number = id_number;
        this.phone = phone;
        this.dob = dob;
        this.chv_id = chv_id;
        this.otp = otp;
    }

    public Mother() {

    }

    private static Mother loadData() {
        int  id = PropertyUtils.getIntValue(REMOTE_ID_PROPERTY);
        String name = PropertyUtils.getStringValue(NAME_PROPERTY);
        String firstname = PropertyUtils.getStringValue(FIRST_NAME);
        String lastname = PropertyUtils.getStringValue(LAST_NAME);
        int id_number = PropertyUtils.getIntValue(ID_NUMBER);
        String phone = PropertyUtils.getStringValue(PHONE_PROPERTY);
        String dob = PropertyUtils.getStringValue(DOB_PROPERTY);
        int chv_id = PropertyUtils.getIntValue(CHV_ID_PROPERTY);
        int otp = PropertyUtils.getIntValue(OTP_PROPERTY);

        return new Mother(id, name, firstname, lastname, id_number, phone, dob, chv_id, otp);
    }

    public static void createMotherData(int id, String name, String firstname, String lastname, int id_number, String phone, String dob, int chv_id, int otp, Context context) {
        Mother motherData = new Mother();
        motherData.setId(id);
        motherData.setName(name);
        motherData.setFirstname(firstname);
        motherData.setLastname(lastname);
        motherData.setId_number(id_number);
        motherData.setPhone(phone);
        motherData.setDob(dob);
        motherData.setChv_id(chv_id);
        motherData.setOtp(otp);

        PrefManager.saveMotherDetails(id,name,firstname,lastname,id_number,phone,dob,chv_id,otp, context);


        instance = motherData;
    }

    public synchronized static Mother getMotherData() {
        if (instance == null) {
            instance = loadData();
        }
        return instance;
    }
//
//    public static JSONObject toMessageJson() {
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("message", this.message);
//            return jsonObject;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getId_number() {
        return id_number;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public int getChv_id() {
        return chv_id;
    }

    public int getOtp() {
        return otp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setId_number(int id_number) {
        this.id_number = id_number;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setChv_id(int chv_id) {
        this.chv_id = chv_id;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }
}
