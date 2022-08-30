package com.clinkod.kabarak;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.clinkod.kabarak.events.ConnectEvent;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.models.SkinSetting;
import com.clinkod.kabarak.services.DataReadService;
import com.clinkod.kabarak.services.DataSynchronizationService;
import com.clinkod.kabarak.ui.devicescan.DeviceScanActivity;
import com.clinkod.kabarak.utils.BleUtils;
import com.clinkod.kabarak.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.YCBTClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Snackbar snackbar;
    private Activity activity;
    private int currentApiVersion;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private final static int ID_HOME = 1;
    private final static int ID_HEALTH = 2;
    private final static int ID_NOTIFICATION = 3;
    private BleConnectResponse bleConnectResponse = new BleConnectResponse() {
        public final void onConnectResponse(int i) {
            handleConnectResponse(i);
        }
    };



    public void handleConnectResponse(int i) {
        if (i == 1 || i == 3 || i == 4) {
            // Log.e(TAG, "Device disconnected");
            showReconnect();
        } else if (i == 5) {
            // Log.e(TAG, "Connecting");
        } else if (i == 6) {
            // Log.e(TAG, "Device Connected");
            Snackbar snackbar2 = this.snackbar;
            if (snackbar2 != null) {
                snackbar2.dismiss();
            }
            EventBus.getDefault().post(new ConnectEvent());
        }
    }

    private void onConnectSetup() {

        if (PropertyUtils.getBooleanValue(SkinSetting.SKIN_COLOR_UPDATED, true)) {

            YCBTClient.settingSkin(SkinSetting.getSkinColor(), (i, v, hashMap) -> {
                // Log.d(TAG, "Skin Color set: " + SkinSetting.getSkinColor());
            });
            PropertyUtils.putProperty(SkinSetting.SKIN_COLOR_UPDATED, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        fullScreenCall();
        setContentView(R.layout.activity_main);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_measure, R.id.navigation_history, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        DataReadService.startActionDataReadService(this);
        PropertyUtils.setSynchronized(false);
        checkWatchStatus();


        //DataSynchronizationService.startActionSynchronizeData(MainActivity.this);
    }

    private void checkWatchStatus(){
        String deviceAddress = PropertyUtils.getDeviceAddress();
        if(deviceAddress == null){
            startActivity(new Intent(MainActivity.this, DeviceScanActivity.class));
            // Log.i(TAG, "Device address not set");

            return;
        }else{
            // get temprature
            getCurrentUserTemp();
            BleUtils.getElectrode();
        }
    }

    private void getCurrentUserTemp(){
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
    private void offCurrentUserTemp(){
        YCBTClient.appTemperatureMeasure(0x00, new BleDataResponse() {
            @Override
            public void onDataResponse(int i, float v, HashMap hashMap) {
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
    private void showBluetoothDisabled() {
        snackbar = Snackbar.make(findViewById(R.id.nav_view), "Bluetooth disabled!", BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.setAction("Enable", v -> {
            snackbar.dismiss();

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        });

        snackbar.show();
    }
    public void checkBluetoothStatus() {
        if (this.mBluetoothAdapter.isEnabled()) {
            //YCBTClient.registerBleStateChange(this.bleConnectResponse);
            try {
                EventBus.getDefault().register(this);
            } catch (EventBusException e) {
            }
            if (DataReadService.getInstance() == null) {
                DataReadService.startActionDataReadService(this);
            } else if (DataReadService.getInstance().getState() == 0) {
                stopService(new Intent(this, DataReadService.class));
                DataReadService.startActionDataReadService(this);
            }
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            stopService(new Intent(this, DataReadService.class));
            showBluetoothDisabled();
        }
    }
//    public void checkBluetoothStatus() {
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                MainActivity.this.stopService(new Intent(MainActivity.this, DataReadService.class));
//                showBluetoothDisabled();
//            }
//        } else {
//            YCBTClient.registerBleStateChange(bleConnectResponse);
//
//            try {
//                EventBus.getDefault().register(this);
//            } catch (EventBusException e) {
//
//            }
//
//            if (DataReadService.getInstance() == null) {
//                DataReadService.startActionDataReadService(this);
//            } else {
//                DataReadService dataReadService = DataReadService.getInstance();
//
//                if (dataReadService.getState() == DataReadService.STATE_DISCONNECTED) {
//                    MainActivity.this.stopService(new Intent(MainActivity.this, DataReadService.class));
//                    DataReadService.startActionDataReadService(MainActivity.this);
//                }
//            }
//
//        }
//    }


    private void fullScreenCall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void showReconnect() {
        snackbar = Snackbar.make(findViewById(R.id.nav_host_fragment), (CharSequence) "Unable to connect!", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction((CharSequence) "RETRY", (View.OnClickListener) new View.OnClickListener() {
            public void onClick(View v) {
                snackbar.dismiss();
                DataReadService.startActionDataReadService(MainActivity.this);
            }
        });
        snackbar.show();
    }

    public  void startBluetooth(View v) {
        this.snackbar.dismiss();
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
    }


    private void turnOnBluetooth() {
        snackbar = Snackbar.make(findViewById(R.id.nav_view), "Bluetooth disabled!", BaseTransientBottomBar.LENGTH_INDEFINITE);
        snackbar.setAction("ENABLE", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1);
                snackbar.dismiss();

            }
        });

        snackbar.show();
    }



    @Override
    protected void onResume() {
        super.onResume();

        checkBluetoothStatus();
        getCurrentUserTemp();
        BleUtils.getElectrode();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        offCurrentUserTemp();
        //YCBTClient.unRegisterBleStateChange(bleConnectResponse);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectEvent(ConnectEvent connectEvent) {
        /*// Log.i(TAG, "This is reached");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                YCBTClient.settingSkin(SkinSetting.getSkinColor(), (i, v, hashMap) -> {
                    // Log.d(TAG, "Skin Color set: " + SkinSetting.getSkinColor());
                });
            }
        }, 5000);*/
    }
    //baseOrderSet();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            DataReadService.startActionDataReadService(MainActivity.this);
        }

        if(resultCode == RESULT_OK){
            String answers_json = data.getExtras().getString("answers");
            Serializable details = data.getSerializableExtra("mother_details");
            String mothers_random_id = data.getExtras().getString("mother_random_id");
            int mother_id = data.getExtras().getInt("mother_id");
             String activity_involved="";
            String mood="";
            try {
                JSONObject answers = new JSONObject(answers_json);
                Iterator<String> questions = answers.keys();

                while (questions.hasNext()) {
                    String question = questions.next();
                    if (question.contains("Select an activity you are(were) engaged in within the last two hours")) {
                        activity_involved =answers.getString(question).trim();
                    }
                    if (question.contains("Choose what best describes how you are feeling")) {
                        mood =answers.getString(question).trim();
                    }

                }

                BpAndHeartRate updatedActivity = new BpAndHeartRate(new Date(),0, 0, 0, null, mother_id, "",mothers_random_id,activity_involved,mood);
                DataSynchronizationService.updateBpReading(updatedActivity);

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

    }
}
