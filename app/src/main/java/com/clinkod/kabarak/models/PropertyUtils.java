package com.clinkod.kabarak.models;

import com.clinkod.kabarak.beans.ObjectBox;
import com.clinkod.kabarak.exceptions.LocalPropertyNotFound;
import com.clinkod.kabarak.exceptions.WrongValueTypeException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.objectbox.Box;

public class PropertyUtils {
    public static final int INT_VALUE = 0;
    public static final int LONG_VALUE = 5;
    public static final int DOUBLE_VALUE = 1;
    public static final int STRING_VALUE = 2;
    public static final int BOOLEAN_VALUE = 3;
    public static final int JSON_VALUE = 4;

    private static final String LAST_PERIODIC_COLLECTION = "last_periodic_collection";
    private static final String HAS_COMPLETED_ONBOARDING = "has_completed_onboarding";
    private static final String HAS_NEXT_OF_KIN = "has_next_of_kin";
    private static final String DATA_SYNCHRONIZED = "data_sync";
    private static final String HAS_BEEN_REGISTERED = "has_been_registered";
    private static final String MEASURE_FREQUENCY = "measure_frequency";
    private static final String SELECTED_VIEW = "selected_view";
    private static final String DEVICE_NAME = "device_name";
    private static final String DEVICE_ADDRESS = "device_address";
    private static final String BED_TIME = "bed_time";
    private static final String WAKE_UP_TIME = "wake_up_time";
    private static final String AUTOMATIC_MEASURE = "automatic_measure";
    private static final String IS_LOGGED_IN = "is_logged_in";

    private static final String CURRENT_USER_TEMP = "current_temp";
    public static void setLastPeriodicCollection(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        putProperty(LAST_PERIODIC_COLLECTION, simpleDateFormat.format(date));
    }


    public static Date getLastPeriodicCollection(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String date = getStringValue(LAST_PERIODIC_COLLECTION, null);

        if(date == null)
            return null;

        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static  boolean isLoggedIn(){
        return getBooleanValue(IS_LOGGED_IN);
    }
    public  static  void  logIn(boolean hasLoggedIn){
        putProperty(IS_LOGGED_IN, hasLoggedIn);
    }

    public static boolean hasFinishedOnboardingData(){
        return getBooleanValue(HAS_COMPLETED_ONBOARDING);
    }

    public static void setHasCompletedOnboarding(boolean hasCompletedOnboarding){
        putProperty(HAS_COMPLETED_ONBOARDING, hasCompletedOnboarding);
    }

    public static void setHasNextOfKin(boolean hasHasNextOfKin){
        putProperty(HAS_NEXT_OF_KIN, hasHasNextOfKin);
    }

    public static void setSynchronized(boolean sycn){
        putProperty(DATA_SYNCHRONIZED, sycn);
    }

    public static boolean hasBeenSynch(){
        return getBooleanValue(DATA_SYNCHRONIZED);
    }

    public static boolean hasHasNextOfKin(){
        return getBooleanValue(HAS_NEXT_OF_KIN);
    }

    public static boolean hasBeenRegistered(){
        return getBooleanValue(HAS_BEEN_REGISTERED);
    }

    public static void setHasBeenRegistered(boolean hasBeenRegistered){
        putProperty(HAS_BEEN_REGISTERED, hasBeenRegistered);
    }

    public static void putProperty(String key, int value){
        putProperty(key, String.valueOf(value), INT_VALUE);
    }

    public static void putProperty(String key, long value){
        putProperty(key, String.valueOf(value), LONG_VALUE);
    }

    public static void putProperty(String key, double value){
        putProperty(key, String.valueOf(value), DOUBLE_VALUE);
    }
    public static void putProperty(String key, String value){
        putProperty(key, value, STRING_VALUE);
    }
    public static void putProperty(String key, boolean value){
        putProperty(key, String.valueOf(value), BOOLEAN_VALUE);
    }
    public static void putProperty(String key, JSONObject value){
        putProperty(key, value.toString(), STRING_VALUE);
    }

    public static int getIntValue(String key){
        LocalProperty localProperty = getProperty(key);

        if(localProperty.getValueType() == INT_VALUE){
            return Integer.parseInt(localProperty.getValue());
        }

        throw new WrongValueTypeException(String.format("The property '%s' is not of type 'int'", key));
    }

    public static int getIntValue(String key, int defaultValue){
        try{
            return getIntValue(key);
        }catch (LocalPropertyNotFound e){
            putProperty(key, defaultValue);
            return defaultValue;
        }
    }

    public static long getLongValue(String key){
        LocalProperty localProperty = getProperty(key);

        if(localProperty.getValueType() == LONG_VALUE || localProperty.getValueType() == INT_VALUE){
            return Long.parseLong(localProperty.getValue());
        }

        throw new WrongValueTypeException(String.format("The property '%s' is not of type 'long'", key));
    }

    public static long getLongValue(String key, long defaultValue){
        try{
            return getLongValue(key);
        }catch (LocalPropertyNotFound e){
            putProperty(key, defaultValue);
            return defaultValue;
        }
    }

    public static double getDoubleValue(String key){
        LocalProperty localProperty = getProperty(key);

        if(localProperty.getValueType() == DOUBLE_VALUE){
            return Double.parseDouble(localProperty.getValue());
        }

        throw new WrongValueTypeException(String.format("The property '%s' is not of type 'double'", key));
    }
    public static String getStringValue(String key){
        return getProperty(key).getValue();
    }

    public static String getStringValue(String key, String defaultValue){
        try {
            return getStringValue(key);
        }catch (LocalPropertyNotFound e){
            return defaultValue;
        }
    }
    public static boolean getBooleanValue(String key){
        LocalProperty localProperty;

        try {
            localProperty = getProperty(key);
        }catch (LocalPropertyNotFound e){
            putProperty(key, false);

            return false;
        }

        if(localProperty.getValueType() == BOOLEAN_VALUE){
            return Boolean.parseBoolean(localProperty.getValue());
        }

        throw new WrongValueTypeException(String.format("The property '%s' is not of type 'boolean'", key));
    }

    public static boolean getBooleanValue(String key, boolean defaultValue){

        LocalProperty localProperty;

        try {
            localProperty = getProperty(key);
        }catch (LocalPropertyNotFound e){
            putProperty(key, defaultValue);

            return defaultValue;
        }

        if(localProperty.getValueType() == BOOLEAN_VALUE){
            return Boolean.parseBoolean(localProperty.getValue());
        }

        throw new WrongValueTypeException(String.format("The property '%s' is not of type 'boolean'", key));
    }

    public static JSONObject getJsonValue(String key){
        LocalProperty localProperty = getProperty(key);

        if(localProperty.getValueType() == JSON_VALUE){
            try {
                return new JSONObject(localProperty.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        throw new WrongValueTypeException(String.format("The property '%s' is not of type 'JSON'", key));
    }

    private static LocalProperty getProperty(String key){
        Box<LocalProperty> localPropertyBox = ObjectBox.get().boxFor(LocalProperty.class);

        LocalProperty localProperty = localPropertyBox
                .query()
                .equal(LocalProperty_.key, key)
                .build().findFirst();

        if(localProperty != null)
            return localProperty;

        throw new LocalPropertyNotFound(String.format("The property '%s' does not exist", key));
    }

    private static void putProperty(String key, String value, int valueType){
        Box<LocalProperty> localPropertyBox = ObjectBox.get().boxFor(LocalProperty.class);
        try{
            LocalProperty localProperty = getProperty(key);
            localProperty.setValue(value);
            localProperty.setValueType(valueType);

            localPropertyBox.put(localProperty);
        }catch (LocalPropertyNotFound e){
            LocalProperty localProperty = new LocalProperty(key, value, valueType);
            localPropertyBox.put(localProperty);
        }
    }

    public static long getMeasureFrequency() {
        return getLongValue(MEASURE_FREQUENCY, 30L);
    }

    public static long getSelectedView() {
        return getLongValue(SELECTED_VIEW, 21313026);
    }

    public static void setMeasureFrequency(long frequency){
        putProperty(MEASURE_FREQUENCY, frequency);
    }
    public static void setSelectedView(long viewID){
        putProperty(SELECTED_VIEW, viewID);
    }

    public static String getDeviceName(){
        return getStringValue(DEVICE_NAME, null);
    }

    public static void setDeviceName(String deviceName){
        putProperty(DEVICE_NAME, deviceName);
    }
    public static void setCurrentTemp(String cuurentTemp){
        putProperty(CURRENT_USER_TEMP, cuurentTemp);
    }

    public static String getUserTemp(){
        return getStringValue(CURRENT_USER_TEMP, null);
    }


    public static String getDeviceAddress() {
        return getStringValue(DEVICE_ADDRESS, null);
    }

    public static void setDeviceAddress(String address){
        putProperty(DEVICE_ADDRESS, address);
    }

    public static String getBedTime() {
        return getStringValue(BED_TIME, "10:00");
    }

    public static boolean doesAutomaticMeasure() {
        return getBooleanValue(AUTOMATIC_MEASURE);
    }

    public void setBedTime(String bedTime){
        putProperty(BED_TIME, bedTime);
    }

    public static String getWakeUpTime() {
        return getStringValue(WAKE_UP_TIME, "05:00");
    }

    public void setWakeUpTime(String wakeUpTime){
        putProperty(WAKE_UP_TIME, wakeUpTime);
    }
}
