package com.clinkod.kabarak.services;



import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.clinkod.kabarak.MainActivity;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.beans.ObjectBox;
import com.clinkod.kabarak.broadcastreceivers.NetworkChangeBroadcastReceiver;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.utils.BleUtils;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.YCBTClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import io.objectbox.Box;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DataReadService extends IntentService {
    private interface ConnectCallback{
        void onConnect();
    }

    private static volatile DataReadService instance;

    private static final String ACTION_TRIGGER_DATA_READ = "com.clinkod.kabarak.services.action.TRIGGER_DATA_READ";

    private static final String TAG = DataReadService.class.getSimpleName();

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTION_FAILED = 3;
    public static final int STATE_READY = 4;
    public static final int STATE_STARTING_MEASUREMENT = 11;
    public static final int STATE_READING_DATA = 5;
    public static final int STATE_DONE = 7;
    public static final int STATE_NOT_SUPPORTED = 8;
    public static final int STATE_GENERAL_FAILURE = 9;
    public static final int STATE_UNABLE_TO_READ = 10;

    private int state = STATE_DISCONNECTED;
    public boolean stopped = true;
    public boolean stoppedTemp = true;
    private boolean serviceRunning = false;
    int timeCheckRead =0;
    private static final int NOTIFICATION_ID = 234;

    public final static String ACTION_STATE_CHANGED =
            "com.clinkod.kabarak.services.action.ACTION_STATE_CHANGED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.clinkod.kabarak.services.action.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.clinkod.kabarak.services.extra.EXTRA_DATA";

    public static final String ACTION_GENERAL_FAILURE =
            "com.clinkod.kabarak.services.action.ACTION_GENERAL_FAILURE";
    public static final String ACTION_POST_DATA =
            "com.clinkod.kabarak.services.action.ACTION_POST_DATA";
    public static final String ACTION_POST_TEMP_DATA =
            "com.clinkod.kabarak.services.action.ACTION_POST_TEMP_DATA";
    private PowerManager.WakeLock wakeLock;
    private final Runnable backgroundDataRead = new Runnable() {
        public final void run() {
            handerDataReadService();
        }
    };

    private int[] latestData = new int[]{-1, -1, -1};
    private String currentTemp ="";
    private Handler handler = new Handler();

        public void handerDataReadService() {
            try {
                this.handler.removeCallbacks(getBackgroundDataRead());
                int i = this.state;
                if (i == 4) {
                    //sendReadCommands();
                } else if (i == 0 || i == 3) {
                    //sendReadCommands();

                }
            } finally {
                //this.handler.postDelayed(getBackgroundDataRead(), PropertyUtils.getMeasureFrequency() * 60 * 1000);
            }
        }

    private final BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        @Override
        public void onConnectResponse(int i) {
            checkDataReadService(i);
        }
    };
    public  void checkDataReadService(int i) {
        if (i == 1 || i == 3 || i == 4) {
            // Log.e(TAG, "Device disconnected");
            this.state = 0;
            broadcastUpdate(ACTION_STATE_CHANGED);
        } else if (i == 5) {
            // Log.e(TAG, "Connecting");
            this.state = 1;
            broadcastUpdate(ACTION_STATE_CHANGED);
        } else if (i == 6) {
            // Log.e(TAG, "Device Connected");
            this.state = 2;
            broadcastUpdate(ACTION_STATE_CHANGED);
        }
    }


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.

    public DataReadService() {
        super("DataReadService");
    }

    public int getState() {


        return state;
    }
    public Runnable getBackgroundDataRead() {
        return backgroundDataRead;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastReadingsUpdate(final String action, int[] data) {
        final Intent i = new Intent(action);
        i.putExtra("readings",data);
        // Log.i(TAG, "Intent Start updates ");
        sendBroadcast(i);
    }

    private void broadcastUpdate(String action, String extra){
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, extra);
        sendBroadcast(intent);
    }

    public static void startActionDataReadService(Context context) {
        Intent intent = new Intent(context, DataReadService.class);
        intent.setAction(ACTION_TRIGGER_DATA_READ);
        try {
            context.startService(intent);
        }catch (IllegalStateException e){

        }
    }

    public static DataReadService getInstance(){

        return instance;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        // Log.i(TAG, "onStartCommand() is reached!");
        this.onHandleIntent(intent);

        return START_STICKY;
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

        // Log.i(TAG, "Service started");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.state = 0;
        broadcastUpdate(ACTION_STATE_CHANGED);
        // Log.i(TAG,"onDestroy called...");
        stopForegroundService();
        serviceRunning = false;

        if(wakeLock != null && wakeLock.isHeld())
            wakeLock.release();
        //instance = null;
        //stopReading();
        //YCBTClient.unRegisterBleStateChange(bleConnectResponse);
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        // Log.i(TAG, "onHandleIntent() is reached!");
        if (!this.serviceRunning) {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BPAnalytics::WakelockTag");
        wakeLock.acquire();
            startForegroundService();
            if (intent != null && ACTION_TRIGGER_DATA_READ.equals(intent.getAction())) {
                try {
                    handleActionTriggerDataRead();
                    this.handler.postDelayed(getBackgroundDataRead(), PropertyUtils.getMeasureFrequency() * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.serviceRunning = true;
        }
    }

    /**
     * Handle action TRIGGER DATA READ in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTriggerDataRead() {

        //YCBTClient.registerBleStateChange(bleConnectResponse);
        if(!this.connect()) {
            broadcastUpdate(ACTION_GENERAL_FAILURE,
                    "Could not connect to the bluetooth device!");
            return;
        }

    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service");
        } else {

            // Create notification default intent.
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0 | PendingIntent.FLAG_IMMUTABLE);

            // Create notification builder.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            // Make notification show big text.
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Mamas Hub is monitoring your Blood Pressure");
//            bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
            // Set big text style.
            builder.setStyle(bigTextStyle);

            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
            builder.setLargeIcon(largeIconBitmap);
            // Make the notification max priority.
            builder.setPriority(Notification.PRIORITY_MAX);
            // Make head-up notification.
            builder.setFullScreenIntent(pendingIntent, true);

            // Build the notification.
            Notification notification = builder.build();

            // Start foreground service.
            startForeground(1, notification);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        Intent resultIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("App is measuring bp readings in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent) //intent
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());
        startForeground(1, notification);
    }

public static void updatStatusChange(Boolean status ){
    // Log.e("Device Reconnect", String.valueOf(status));

    DataReadService data = new DataReadService();
    if(status == true){
        data.state = STATE_CONNECTED;
        data.broadcastUpdate(ACTION_STATE_CHANGED);
        data.handler.postDelayed(() -> {
            data.state = STATE_READY;
            data.broadcastUpdate(ACTION_STATE_CHANGED);

        }, 2000);
    }else{
        data.state = STATE_DISCONNECTED;
        data.broadcastUpdate(ACTION_STATE_CHANGED);
    }

}
    private void stopForegroundService() {

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    private void setCurrentReading(int systole, int diastole, int heartrate){

        latestData = new int[]{systole, diastole, heartrate};
        broadcastUpdate(ACTION_DATA_AVAILABLE);
    }

    private void setCurrentReadingTempBp(String temp){
        currentTemp = temp;
        broadcastUpdate(ACTION_DATA_AVAILABLE);
    }

    public void sendGetTempCommands(){
        Log.i(TAG, "Start reading temp");

        if(state == STATE_READY || state == STATE_DONE){
            state = STATE_STARTING_MEASUREMENT;
            broadcastUpdate(ACTION_STATE_CHANGED);
            YCBTClient.getRealTemp(new BleDataResponse() {
                @Override
                public void onDataResponse(int i, float v, HashMap hashMap) {
                    if (i == 0) {
                        if (hashMap != null) {
                            state = STATE_READING_DATA;
                            broadcastUpdate(ACTION_STATE_CHANGED);
                            String temp = (String) hashMap.get("tempValue");
                            setCurrentReadingTempBp(temp);
                            Float temR= Float.parseFloat(temp);

                        }
                        stoppedTemp = true;

                    }
                }
            });

        }


    }



    public boolean sendReadCommands(){

        // Log.i(TAG, "Start reading");

        if(state == STATE_READY || state == STATE_DONE){
            this.reset();

            // Log.i(TAG, "Sending read commands");
            AtomicInteger timeMeasured= new AtomicInteger();
            state = STATE_STARTING_MEASUREMENT;
            broadcastUpdate(ACTION_STATE_CHANGED);
            YCBTClient.appEcgTestStart((i, v, hashMap) -> {
                if (hashMap != null) {
                    //clockInfo.c_hour
                    ArrayList<HashMap> lists = (ArrayList) hashMap.get("data");
                    for (HashMap map : lists) {
                        System.out.println(map.keySet());
                        System.out.println(map.values());
                    }
                }
            }, (i, hashMap) -> {

                if (hashMap != null) {
                    state = STATE_READING_DATA;
                    broadcastUpdate(ACTION_STATE_CHANGED);

                    int dataType = (int) hashMap.get("dataType");

                    if (i == Constants.DATATYPE.Real_UploadECG) {
                        final List<Integer> tData = (List<Integer>) hashMap.get("data");
                        System.out.println("chong----------ecgData==" + tData.toString());
                    } else if (i == Constants.DATATYPE.Real_UploadECGHrv) {
                        float param = (float) hashMap.get("data");
                        // Log.e("qob", "HRV: " + param);
                    } else if (i == Constants.DATATYPE.Real_UploadECGRR) {
                        float param = (float) hashMap.get("data");
                        // Log.e("qob", "RR invo " + param);
                    } else if (i == Constants.DATATYPE.Real_UploadBlood) {
                        int heart = (int) hashMap.get("heartValue");//心率
                        int tDBP = (int) hashMap.get("bloodDBP");//高压
                        int tSBP = (int) hashMap.get("bloodSBP");//低压
                        timeMeasured.getAndIncrement();
                        if(timeMeasured.get() > 7){
                            stopReading();
                        }
                        setCurrentReading(tDBP, tSBP, heart);
                    }

                    // Ensure we know that it is running
                    stopped = false;
                }
            });


            //handler.postDelayed(this::stopReading, 60000);

            return true;
        }

        return false;
    }

    public int[] getData(){
        return latestData;
    }

    public void stopReadingTemp(){
        if(stoppedTemp){
            // Log.i(TAG, "stopped");
            state = STATE_READY;
            broadcastUpdate(ACTION_STATE_CHANGED);
            if(currentTemp != ""){
                 Log.i(TAG, currentTemp);
//                Intent readingsTempIntent = new Intent();
//                readingsTempIntent.setAction(ACTION_POST_TEMP_DATA);
//                readingsTempIntent.putExtra("readingsTemp",currentTemp);
//                sendBroadcast(readingsTempIntent);
//                //DataSynchronizationService.startActionSynchronizeData(this);
//                NetworkChangeBroadcastReceiver.checkNetworkAndSync(getApplicationContext());
            }
            return;
        }

        YCBTClient.appTemperatureMeasure(0x00, new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {
                stoppedTemp = true;
                state = STATE_DONE;
                broadcastUpdate(ACTION_STATE_CHANGED);
                handler.postDelayed(()-> {
                    state = STATE_READY;
                    broadcastUpdate(ACTION_STATE_CHANGED);
                }, 3000);
                if (hashMap != null) {
                    //clockInfo.c_hour
                    ArrayList<HashMap> lists = (ArrayList) hashMap.get("data");
                    for (HashMap map : lists) {
                        System.out.println(map.keySet());
                        System.out.println(map.values());
                    }
                }
            }
        });
    }

    public void stopReading(){
        String mother_random_id = String.valueOf(System.currentTimeMillis());
//        String userTemp = PropertyUtils.getUserTemp();
//        if(userTemp == null){
//            userTemp= "35.0";
//            BleUtils.getCurrentUserTemprature();
//        }

        // Log.i(TAG, "Stop reading");

        if(stopped){
            // Log.i(TAG, "stopped");
            state = STATE_READY;
            broadcastUpdate(ACTION_STATE_CHANGED);
            if(latestData[0] != -1){
                // Log.i(TAG, "data available");
                Intent readingsIntent = new Intent();
                readingsIntent.setAction(ACTION_POST_DATA);
                readingsIntent.putExtra("mother_random_id",mother_random_id);
                readingsIntent.putExtra("readings",latestData);
                sendBroadcast(readingsIntent);
                //DataSynchronizationService.startActionSynchronizeData(this);
                NetworkChangeBroadcastReceiver.checkNetworkAndSync(getApplicationContext());
            }
            return;
        }

        YCBTClient.appEcgTestEnd((i, v, hashMap) -> {
            stopped = true;
            state = STATE_DONE;
            broadcastUpdate(ACTION_STATE_CHANGED);
            handler.postDelayed(()-> {
                state = STATE_READY;
                broadcastUpdate(ACTION_STATE_CHANGED);
            }, 3000);
            if (hashMap != null) {
                //clockInfo.c_hour
                ArrayList<HashMap> lists = (ArrayList) hashMap.get("data");
                for (HashMap map : lists) {
                    System.out.println(map.keySet());
                    System.out.println(map.values());
                }
            }

        });

        if(latestData[0] != -1){
            // Log.i(TAG, "data available old" + " Date: " + String.valueOf(new Date()));
            int mother_id = PrefManager.getId(getApplicationContext());
            String category = BleUtils.getCategory(latestData[0],latestData[1]);
            Box<BpAndHeartRate> hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);
            BpAndHeartRate hourlyData = new BpAndHeartRate(new Date(), latestData[2], latestData[0], latestData[1], null, mother_id,category,mother_random_id,"","" );
            hourlyDataBox.put(hourlyData);
            Intent readingsIntent = new Intent();
            readingsIntent.setAction(ACTION_POST_DATA);
            readingsIntent.putExtra("readings",latestData);
            readingsIntent.putExtra("mother_random_id",mother_random_id);
            sendBroadcast(readingsIntent);

            DataSynchronizationService.startRealSynchronizeData(this,latestData,mother_random_id);


        }

    }

    public void reset(){
        latestData = new int[]{-1,-1,-1};
    }

    public boolean connect(){
        return connect(null);
    }

public void updateConnected(){
    // Log.e("Device Reconnect","success agga");
    state = STATE_CONNECTED;
    broadcastUpdate(ACTION_STATE_CHANGED);
    handler.postDelayed(() -> {
        state = STATE_READY;
        broadcastUpdate(ACTION_STATE_CHANGED);

    }, 2000);
}

public void updateDisconnected(){
  state = STATE_DISCONNECTED;
  broadcastUpdate(ACTION_STATE_CHANGED);

}
public void updateStopReading(){
    if(stopped){
        state = STATE_READY;
        broadcastUpdate(ACTION_STATE_CHANGED);
        return;
    }
    stopped = true;
    state = STATE_DONE;
    broadcastUpdate(ACTION_STATE_CHANGED);
    handler.postDelayed(()-> {
        state = STATE_READY;
        broadcastUpdate(ACTION_STATE_CHANGED);
    }, 3000);
}

    public boolean connect(ConnectCallback callback){

        if(state != STATE_DISCONNECTED){
            return true;
        }

        String deviceAddress = PropertyUtils.getDeviceAddress();
        if(deviceAddress == null){
            return false;
        }

        YCBTClient.connectBle(deviceAddress, i -> {
                    if (i == Constants.CODE.Code_OK){
                        BleUtils.settingRestoreFactory();
                        state = STATE_CONNECTED;
                        broadcastUpdate(ACTION_STATE_CHANGED);

                        handler.postDelayed(() -> {
                            state = STATE_READY;
                            broadcastUpdate(ACTION_STATE_CHANGED);
                            if(callback != null){
                                callback.onConnect();
                            }
                        }, 2000);

                        //sendReadCommands();
                    }else{
                        state = STATE_DISCONNECTED;
                        broadcastUpdate(ACTION_STATE_CHANGED);
                    }
        }
        );

        return true;
    }

    public void disconnect() {
        YCBTClient.disconnectBle();
    }

}
