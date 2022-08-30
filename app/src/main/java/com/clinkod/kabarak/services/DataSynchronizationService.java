package com.clinkod.kabarak.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.clinkod.kabarak.beans.ObjectBox;
import com.clinkod.kabarak.exceptions.LocalPropertyNotFound;
import com.clinkod.kabarak.models.ActivityAndMood;
import com.clinkod.kabarak.models.ActivityAndMood_;
import com.clinkod.kabarak.models.BioData;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.models.BpAndHeartRate_;
import com.clinkod.kabarak.models.DailyData;
import com.clinkod.kabarak.models.DailyData_;
import com.clinkod.kabarak.models.NextOfKin;
import com.clinkod.kabarak.models.PeriodicData;
import com.clinkod.kabarak.models.PeriodicData_;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.utils.BleUtils;
import com.clinkod.kabarak.utils.Constants;
import com.clinkod.kabarak.utils.NetworkManager;
import com.clinkod.kabarak.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataSynchronizationService extends IntentService {
    private static final String TAG = DataSynchronizationService.class.getSimpleName();

    private static final String ACTION_SYNCHRONIZE_DATA = "com.clinkod.kabarak.services.action.SYNCHRONIZE_DATA";
    private static final String REALTIME_SYNC_DATA = "com.clinkod.kabarak.services.action.REALTIME_SYNC_DATA";

    public DataSynchronizationService() {
        super("DataSynchronizationService");
    }

    public static synchronized void startActionSynchronizeData(Context context) {
        Intent intent = new Intent(context, DataSynchronizationService.class);
        intent.setAction(ACTION_SYNCHRONIZE_DATA);
        context.startService(intent);
    }


    public static synchronized void startRealSynchronizeData(Context context, int[] data, String mother_random_id) {
        Intent intent = new Intent(context, DataSynchronizationService.class);
        intent.setAction(REALTIME_SYNC_DATA);
        intent.putExtra("readings",data);
        intent.putExtra("mother_random_id",mother_random_id);
        context.startService(intent);
        //context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNCHRONIZE_DATA.equals(action)) {
                handleActionSynchronizeData();
            }
            if (REALTIME_SYNC_DATA.equals(action)) {


                int[] reading = intent.getIntArrayExtra("readings");
                String mother_random_id = intent.getStringExtra("mother_random_id");
                try {
                    if (Utils.isConnected()){
                        handleRealSynchronizeData(reading,mother_random_id);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void handleActionSynchronizeData() {
//        // Check if the user has been registered in the backend
//        if(!PropertyUtils.hasBeenRegistered()){
//            // Perform registration
//            if(!registerUser()){
//                return;
//            }
//
//            PropertyUtils.setHasBeenRegistered(true);
//        }
        //synchronizePeriodicData();
        //synchronizeDailyData();
        //synchronizeActivityAndMood();
        //synchronizeHourlyData();

    }

    private void handleRealSynchronizeData(int[] reading, String mother_random_id) {

        int mother_id = PrefManager.getId(getApplicationContext());
        String category = BleUtils.getCategory(reading[0], reading[1]);
        BpAndHeartRate hourlyData = new BpAndHeartRate(new Date(), reading[2], reading[0], reading[1], null, mother_id, category,mother_random_id,"","");
        postBpReading(hourlyData);

    }

    private boolean registerUser(){


        BioData bioData;
        try{
            bioData = BioData.getBioData();
        }catch (LocalPropertyNotFound e){

            e.printStackTrace();
            return false;
        }

        try {
            Response response = NetworkManager.getInstance()
                    .post(Constants.BASE_SERVER_URL + "/rest/v1/register/patient",
                            null,
                            bioData.toJson(),
                            null,
                            false);

            String responseString = response.body().string();

            if(response.isSuccessful()){
                JSONObject res = new JSONObject(responseString);
                bioData.setRemoteId(res.getString("id"));

                return true;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void synchronizePeriodicData(){
        Box<PeriodicData> periodicDataBox = ObjectBox.get().boxFor(PeriodicData.class);
        List<PeriodicData> periodicDataList = periodicDataBox.query().isNull(PeriodicData_.remoteId)
                .build().find();

        for(PeriodicData periodicData : periodicDataList){
            try {
                Response response = NetworkManager.getInstance()
                        .post(Constants.BASE_SERVER_URL + "/entities/bpanalyticsbackend$PeriodicData",
                                null,
                                periodicData.toJson(),
                                null,
                                true);

                String responseString = response.body().string();

                if(response.isSuccessful()){
                    JSONObject res = new JSONObject(responseString);
                    periodicData.setRemoteId(res.getString("id"));
                    periodicDataBox.put(periodicData);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void synchronizeActivityAndMood(){


        Box<ActivityAndMood> periodicDataBox = ObjectBox.get().boxFor(ActivityAndMood.class);
        List<ActivityAndMood> activityAndMoods = periodicDataBox.query().isNull(ActivityAndMood_.remoteId)
                .build().find();

        for(ActivityAndMood activityAndMood : activityAndMoods){
            try {
                Response response = NetworkManager.getInstance()
                        .post(Constants.BASE_SERVER_URL + "/entities/bpanalyticsbackend$ActivityAndMood", // TODO: Update this
                                null,
                                activityAndMood.toJson(),
                                null,
                                true);

                String responseString = response.body().string();

                if(response.isSuccessful()){
                    JSONObject res = new JSONObject(responseString);
                    activityAndMood.setRemoteId(res.getString("id"));
                    periodicDataBox.put(activityAndMood);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void synchronizeDailyData(){


        Box<DailyData> periodicDataBox = ObjectBox.get().boxFor(DailyData.class);
        List<DailyData> dailyDataList = periodicDataBox.query().isNull(DailyData_.remoteId)
                .build().find();

        for(DailyData dailyData : dailyDataList){
            try {
                Response response = NetworkManager.getInstance()
                        .post(Constants.BASE_SERVER_URL + "/entities/bpanalyticsbackend$DailyData", // TODO: Update this
                                null,
                                dailyData.toJson(),
                                null,
                                true);

                String responseString = response.body().string();

                if(response.isSuccessful()){
                    JSONObject res = new JSONObject(responseString);
                    dailyData.setRemoteId(res.getString("id"));
                    periodicDataBox.put(dailyData);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void postBpReading(BpAndHeartRate data){
        try {
            Response response = NetworkManager.getInstance()
                    .postReadings(Constants.API_SERVER_URL + "/api/bp/new", // TODO: Update this
                            null,
                            data.toJson(),
                            null,
                            false);

            String responseString = response.body().string();

            if(response.isSuccessful()){
                JSONObject res = new JSONObject(responseString);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateBpReading(BpAndHeartRate data){
        try {
            Response response = NetworkManager.getInstance()
                    .postReadings(Constants.API_SERVER_URL + "/api/bp/update/activity", // TODO: Update this
                            null,
                            data.toJson(),
                            //        Call call = this.client.newCall(request);
                            // Get a handler that can be used to post to the main thread
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                        throw new IOException("Unexpected code " + response);
                                    } else {

                                    }
                                }
                            },
                            false);

//            String responseString = response.body().string();
//
//            if(response.isSuccessful()){
//                JSONObject res = new JSONObject(responseString);
//            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public static void postNextOfKin(NextOfKin data,Context context){
        try {
            Response response = NetworkManager.getInstance()
                    .post(Constants.API_SERVER_URL + "/api/nextofkin/new", // TODO: Update this
                            null,
                            data.toJson(context),
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    String responseString = response.body().string();

                                }
                            },
                            false);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getMothersBpReadings(Context context){
        int mother_id = PrefManager.getId(context);
        try {
            Response response = NetworkManager.getInstance()
                    .get(Constants.API_SERVER_URL + "/api/bp/list/" + mother_id, // TODO: Update this
                            null,
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    String responseString = response.body().string();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                    if(response.isSuccessful()){
                                        JSONObject res = null;
                                        try {
                                            res = new JSONObject(responseString);
                                            JSONArray dataArray = res.getJSONArray("data");

                                            for (int i = 0; i < dataArray.length(); i++) {
                                                JSONObject postObject = dataArray.getJSONObject(i);
                                                int id = postObject.getInt("id");
                                                int diastole = postObject.getInt("diastole");
                                                int heart_rate = postObject.getInt("heart_rate");
                                                int systole = postObject.getInt("systole");
                                                String random_id = postObject.getString("random_id");
                                                String activity = postObject.getString("activity");
                                                String mood = postObject.getString("mood");
                                                String category = postObject.getString("category");
                                                String created_at = postObject.getString("created_at");
                                                Date dateCreated_at = simpleDateFormat.parse(created_at.replace('T',' '));
                                                Box<BpAndHeartRate> hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);
                                                BpAndHeartRate hourlyData = new BpAndHeartRate(dateCreated_at,heart_rate,systole,diastole,null,mother_id,category, random_id,activity,mood );
                                                hourlyDataBox.put(hourlyData);
                                                //Use the title and id as per your requirement
                                            }
                                            PropertyUtils.setSynchronized(true);

                                        } catch (JSONException | ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            },
                            false);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void synchronizeHourlyData(){

        Box<BpAndHeartRate> hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);
        List<BpAndHeartRate> hourlyDataList = hourlyDataBox.query().isNull(BpAndHeartRate_.remoteId)
                .build().find();

        for(BpAndHeartRate hourlyData : hourlyDataList){
            try {
                Response response = NetworkManager.getInstance()
                        .post(Constants.API_SERVER_URL + "/api/bp/new", // TODO: Update this
                                null,
                                hourlyData.toJson(),
                                null,
                                false);

                String responseString = response.body().string();

                if(response.isSuccessful()){
                    JSONObject res = new JSONObject(responseString);
                    //hourlyData.setRemoteId(res.getString("id"));
                    //hourlyDataBox.put(hourlyData);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
