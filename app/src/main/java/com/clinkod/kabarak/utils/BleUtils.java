package com.clinkod.kabarak.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.clinkod.kabarak.beans.ObjectBox;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.models.BpAndHeartRate_;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.models.SkinSetting;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.YCBTClient;
import io.objectbox.Box;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;




    public class BleUtils {
        private static final String TAG = BleUtils.class.getSimpleName();

        public static void setSkinColor() {
            YCBTClient.settingSkin(SkinSetting.getSkinColor(), (i, v, hashMap) -> {
                // Log.d(TAG, "Skin Color set: " + SkinSetting.getSkinColor());
            });
        }

        public static void setCollectionInterval() {

            int collectionInterval = (int) PropertyUtils.getMeasureFrequency();
            //Heart rate collection
            //YCBTClient.settingHeartMonitor(0x00, 0, (i, v, hashMap) -> // Log.e("device", "Set interval to collect heart rate"));

            //No sense detection
            //YCBTClient.settingPpgCollect(0x01, collectionInterval, collectionInterval, (i, v, hashMap) ->  Log.e("device", "Set up sensorless data collection"));
        }

        public static void syncBPData(Activity activity) {
            YCBTClient.healthHistoryData(0x0509, new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    System.out.println("chong----hashmap==" + hashMap);
                    if (hashMap != null) {
                        //clockInfo.c_hour
                        ArrayList<HashMap> lists = (ArrayList) hashMap.get("data");

                        for (HashMap map : lists) {
                            long time = (long) map.get("startTime");
                            int systole = (int) map.get("DBPValue");
                            int diastole = (int) map.get("SBPValue");
                            int heratVal = (int) map.get("heartValue");

                            // Log.d(TAG, String.valueOf(time));
                            // Log.d(TAG, String.valueOf(systole));
                            // Log.d(TAG, String.valueOf(diastole));
                            // Log.d(TAG, String.valueOf(heratVal));

                            Date collectionTime = new Date(time);

                            Box<BpAndHeartRate> hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);

                            BpAndHeartRate collectedHourlyData = hourlyDataBox.query().equal(BpAndHeartRate_.timeTaken, collectionTime).build().findFirst();

//                            if (collectedHourlyData != null) {
//                                collectedHourlyData.setDiastolicBp(diastole);
//                                collectedHourlyData.setSystolicBp(systole);
//
//                                hourlyDataBox.put(collectedHourlyData);
//                            } else {
//                                int mother_id = PrefManager.getId(activity);
//                                String category = BleUtils.getCategory(systole,diastole);
//                                BpAndHeartRate hourlyData = new BpAndHeartRate(new Date(time),-1, systole, diastole, null,mother_id,category );
//
//                                hourlyDataBox.put(hourlyData);
//                            }


                        }

                        deleteData(0x0543, activity);
                    }
                }
            });
        }

        public static void getDeviceLog() {
            YCBTClient.getDeviceLog(0x00,new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                        // Log.d(TAG, hashMap.toString());
                    }
                }
            });


        }
        public static void getDeviceConfig() {
            YCBTClient.getDeviceUserConfig(new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                        // Log.d(TAG, hashMap.toString());
                    }
                }
            });
        }

        public static void getDeviceInfo() {

            YCBTClient.getDeviceInfo(new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                         Log.d(TAG, hashMap.toString());
                    }
                }
            });
        }
        public static void getScheduleInfo() {
            YCBTClient.getScheduleInfo(new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                        // Log.d(TAG, hashMap.toString());
                    }
                }
            });
        }
        public static void settingRestoreFactory() {
            YCBTClient.getScheduleInfo(new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                        // Log.d(TAG, hashMap.toString());
                    }
                }
            });
        }

        public static int getDeviceStatus() {
            return YCBTClient.connectState();
        }





        public static void deleteData(int dataType, Activity activity) {
            activity.runOnUiThread(() -> new Handler().postDelayed(() -> {
                YCBTClient.deleteHealthHistoryData(dataType, (i, v, hashMap) -> {
                    if (i == 0) {//delete success

                    }
                });
            }, 3));

        }

        public static void setFindDeviceOn() {
            YCBTClient.settingFindPhone(0x01,new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                        // Log.d(TAG, hashMap.toString());
                    }
                }
            });
        }


        public static void recoonectDevice(String devicemac) {
            YCBTClient.connectBle(devicemac, new BleConnectResponse() {
                @Override
                public void onConnectResponse(int code) {
                    if (code == Constants.Code_OK){

                    }
                    else if (code == Constants.Code_Failed){

                    }
                }
            });
        }

        public static int checkConnectedStatus() {
            int bleState = YCBTClient.connectState();
            return bleState;
        }


        public static void getCurrentUserTemprature(){
            YCBTClient.appTemperatureMeasure(0x01, new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null) {
                        //clockInfo.c_hour
                        YCBTClient.getRealTemp(new BleDataResponse() {
                            @Override
                            public void onDataResponse(int i, float v, HashMap hashMap) {
                                if (i == 0) {
                                    if (hashMap != null) {

                                        String temp = (String) hashMap.get("tempValue");
                                        Float temR= Float.parseFloat(temp);
                                        PropertyUtils.setCurrentTemp(String.valueOf(temR));
                                    }
                                }
                            }
                        });

                    }
                }
            });
        }

public static void getElectrode(){
    YCBTClient.getElectrodeLocationInfo(new BleDataResponse() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDataResponse(int code, float ratio, HashMap resultMap) {
            if (resultMap != null){

                resultMap.forEach((key, value) -> Log.d("MapKeys", String.valueOf(key)));

//                    tRetMap.put("ecgLocation", tEcgLocation);  //
//                    tRetMap.put("dataType", Constants.DATATYPE.GetElectrodeLocation);
            }
        }
    });
}

        public static void setFindDeviceOff(int dataType, Activity activity) {
            YCBTClient.settingFindPhone(0x00,new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (hashMap != null){
                        // Log.d(TAG, hashMap.toString());
                    }
                }
            });

        }





        public static String getCategory(int systolic, int diastolic){
            String category = "";
            if ((systolic >= 90 && systolic <= 120) || (diastolic > 60 && diastolic <= 80)) {
                category="Normal";

            } else if ((systolic > 120 && systolic < 130) || (diastolic >= 60 && diastolic <= 80)) {
                category="Elevated";

            }
            else if ((systolic > 130 && systolic <= 140) || (diastolic >= 60 && diastolic <= 80)) {
                category="High";

            } else if (systolic < 90 && diastolic < 60) {
                category="Low";
            }else if (systolic > 180 && diastolic > 120) {
                category="Hypertensive";

            }
            return category;
        }
    }

