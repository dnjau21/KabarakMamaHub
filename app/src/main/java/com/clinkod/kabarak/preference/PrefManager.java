package com.clinkod.kabarak.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }


    public static void saveMotherDetails(int id, String name, String firstname, String lastname, int id_number, String phone, String dob, int chv_id, int otp, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
        editor.putString("name", name);
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.putInt("id_number", id_number);
        editor.putString("phone", phone);
        editor.putString("dob", dob);
        editor.putInt("chv_id", chv_id);
        editor.putInt("otp", otp);

        editor.commit();
    }

    public static void saveNextOfKinDetails(int id, String firstname, String lastname, int id_number, String phone, String relationship, int community_health_id, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.putInt("id_number", id_number);
        editor.putString("phone", phone);
        editor.putString("relationship", relationship);
        editor.putInt("community_health_id", community_health_id);

        editor.commit();
    }



    public Integer getId_number() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id_number", 0);
    }

    public static Integer getId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", 0);
    }
    public static Integer getNextOfKinId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", 0);
    }

    public static Integer getNextOfKinIdNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id_number", 0);
    }

    public static Integer getIdNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id_number", 0);
    }

    public static Integer getNextOfKincommunity_health_id(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("community_health_id", 0);
    }

    public static Integer getOtp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("otp", 0);
    }

    public static String getName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("firstname", "") + sharedPreferences.getString("lastname", "");
    }

    public static String getNextOfKinFirstName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("firstname", "");
    }
    public static String getFirstName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("firstname", "");
    }
    public static String getNextOfKinLastname(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("lastname", "");
    }
    public static String getLastname(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("lastname", "");
    }
    public static String getNextOfKinPhoneNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("phone", "");
    }

    public static String getNextOfKinRelationship(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NextOfKinDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("relationship", "");
    }

    public static String getPhoneNumber(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("phone", "");
    }
    public static String getDateOfBirth(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MothersDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("dob", "");
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }
}
