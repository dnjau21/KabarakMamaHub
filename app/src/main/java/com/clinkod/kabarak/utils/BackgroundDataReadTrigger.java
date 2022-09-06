package com.clinkod.kabarak.utils;

import static com.clinkod.kabarak.services.DataReadService.ACTION_GENERAL_FAILURE;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.clinkod.kabarak.events.ConnectEvent;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.services.DataReadService;
import com.clinkod.kabarak.ui.measure.FragmentMeasure;
import com.clinkod.kabarak.ui.measure.MeasureFragment;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.YCBTClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

public class BackgroundDataReadTrigger {

//    public static int TimeOut = 1;
//    public static int NotOpen = 2;
//    public static int Disconnect = 3;
//    public static int Disconnecting = 4;
//    public static int Connecting = 5;
//    public static int Connected = 6;
//    public static int ServicesDiscovered = 7;
//    public static int CharacteristicDiscovered = 8;
//    public static int CharacteristicNotification = 9;
//    public static int ReadWriteOK = 10;

    int timeCheck=0;
    int timeCheckTrigger=0;
    Handler handler = new Handler();
    public interface BackgroundDataReadCompleteListener{
        void onFinish();
    }

    private static final String TAG = BackgroundDataReadTrigger.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private BackgroundDataReadCompleteListener listener;

    private DataReadService dataReadService;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            dataReadService = DataReadService.getInstance();
            if (DataReadService.ACTION_STATE_CHANGED.equals(action)) {
                timeCheck++;
                // Log.d(TAG, "Failure timechec:" + timeCheck);
                if(timeCheck > 20 && dataReadService.getState() == 5 ){
                    //stopBlueReading();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataReadService.stopReading();
                        }

                    }, 2000); // 5000ms delay

                }

                switch (dataReadService.getState()){
                    case DataReadService.STATE_READY:
                        //dataReadService.sendGetTempCommands();
                        dataReadService.sendReadCommands();
                        break;

                    case DataReadService.STATE_DONE:
                        context.getApplicationContext().unregisterReceiver(mGattUpdateReceiver);
                        break;
                    case DataReadService.STATE_NOT_SUPPORTED:
                        context.getApplicationContext().unregisterReceiver(mGattUpdateReceiver);
                        break;
                }

            } else if(ACTION_GENERAL_FAILURE.equals(action)){
                String message = intent.getStringExtra(DataReadService.EXTRA_DATA);
                // Log.d(TAG, "Failure:" + message);
            }
        }
    };

    BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        @Override
        public void onConnectResponse(int var1) {

            //Toast.makeText(MyApplication.this, "i222=" + var1, Toast.LENGTH_SHORT).show();

            // Log.e("device","...connect..state....." + var1);

            if(var1 == 0){
                EventBus.getDefault().post(new ConnectEvent());
            }
        }
    };

    public static void trigger(Context context, BackgroundDataReadCompleteListener listener){
         Log.i(TAG, "Data read triggered check connection here");
        BackgroundDataReadTrigger backgroundDataReadTrigger = new BackgroundDataReadTrigger(context, listener);
        backgroundDataReadTrigger.init();
    }

    public BackgroundDataReadTrigger(Context context, BackgroundDataReadCompleteListener listener){
        this.context = context;

        this.listener = listener;
    }

    public void stopBlueReading(){
        YCBTClient.appEcgTestEnd((i, v, hashMap) -> {
            dataReadService.updateStopReading();
        });
    }

    private void init(){
        String deviceAddress = PropertyUtils.getDeviceAddress();
        if(deviceAddress == null){
            // Log.i(TAG, "Device address not set");
            Utils.show_Notification(this.context);
            return;
        }

        //check device connection status
        int dStatus= BleUtils.getDeviceStatus();
        switch (dStatus){
            //device disconnected
            case 3:
                YCBTClient.connectBle(deviceAddress, i -> {
                    if (i == Constants.CODE.Code_OK){
                        dataReadService.updateConnected();
                    }else{
                        if(DataReadService.getInstance() != null) {
                            // Log.i(TAG, "Device conenected reading now");
                            dataReadService = DataReadService.getInstance();

                            dataReadService.updateDisconnected();
                            // Log.e("Device Reconnect", "Failed");
                        }
                    }
                });
                break;
        }



        if(DataReadService.getInstance() != null){
            // Log.i(TAG, "Device conenected reading now");
            dataReadService = DataReadService.getInstance();
            // Log.i(TAG, "Device state " + dataReadService.getState());
            if(dataReadService.getState() == 5 || dataReadService.getState() == 11 ){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dataReadService.stopReading();
                    }
                }, 2000); // 5000ms delay
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //dataReadService.sendGetTempCommands();
                        dataReadService.sendReadCommands();
                    }
                }, 10000); // 5000ms delay


            }
            // Check the current state
            if(dataReadService.getState() == DataReadService.STATE_READY){
                //dataReadService.sendGetTempCommands();
                dataReadService.sendReadCommands();
                FragmentMeasure fragmentMeasure = new FragmentMeasure();
                context.getApplicationContext().registerReceiver(mGattUpdateReceiver, fragmentMeasure.makeGattUpdateIntentFilter());
            }
        }else{
            int state = DataReadService.STATE_DISCONNECTED;
            dataReadService = DataReadService.getInstance();
            if (dataReadService != null) {
                state = dataReadService.getState();
            }
            if (state == DataReadService.STATE_DISCONNECTED ){

//                YCBTClient.initClient(this.context,true);
                DataReadService.startActionDataReadService(this.context);
            }
            // Log.i(TAG, "Device instance  null");
            DataReadService.startActionDataReadService(this.context);
        }

    }

    public void checkBluetoothStatus() {
        if (this.mBluetoothAdapter.isEnabled()) {
            YCBTClient.registerBleStateChange(this.bleConnectResponse);
            try {
                EventBus.getDefault().register(this);
            } catch (EventBusException e) {
            }
            if (DataReadService.getInstance() == null) {
                DataReadService.startActionDataReadService(this.context);
            } else if (DataReadService.getInstance().getState() == 0) {
//                stopService(new Intent(this.context, DataReadService.class));
                DataReadService.startActionDataReadService(this.context);
            }
        }
    }


}
