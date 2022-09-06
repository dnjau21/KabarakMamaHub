package com.clinkod.kabarak.ui.devicescan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.clinkod.kabarak.MainActivity;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.events.ConnectEvent;
import com.clinkod.kabarak.models.BioData;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.services.DataReadService;
import com.clinkod.kabarak.utils.BleUtils;
import com.clinkod.kabarak.utils.PermissionUtils;
import com.google.android.material.snackbar.Snackbar;
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;
import com.yucheng.ycbtsdk.Response.BleConnectResponse;
import com.yucheng.ycbtsdk.YCBTClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.S)
public class DeviceScanActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG ="DeviceScan";
    private DeviceScanViewModel mViewModel;
    private List<ScanDeviceBean> listModel = new ArrayList<>();
    private ListView deviceList;
    private List<String> listVal = new ArrayList<>();
    DeviceListAdapter deviceListAdapter = new DeviceListAdapter(DeviceScanActivity.this, listModel);
    private Button btnScan;
    private boolean scanning = false;
    ImageView imageView;
    private TextView usernameText;
    private  BioData bioData;
    private DataReadService dataReadService;
    CoordinatorLayout coordinatorLayout;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;


    private String[] permissionArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectEvent(ConnectEvent connectEvent) {
        //baseOrderSet();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.device_scan_fragment);

        updateUiState();
        YCBTClient.initClient(this,true);
        //YCBTClient.registerBleStateChange(bleConnectResponse);

        EventBus.getDefault().register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            boolean backBoolean = PermissionUtils.checkPermissionArray(DeviceScanActivity.this, permissionArray, 3);
//            checkLocationPermission();
        }



        deviceList = findViewById(R.id.device_list);
        btnScan = findViewById(R.id.btnScan);
        imageView = findViewById(R.id.bleConnect);
        usernameText = findViewById(R.id.txtUser);
        setUserName();


        deviceList.setAdapter(deviceListAdapter);

        findViewById(R.id.btnScan).setOnClickListener(this);

        deviceList.setOnItemClickListener((parent, view1, position, id) -> {
            YCBTClient.stopScanBle();
            btnScan.setText("Scan");
            scanning = false;

//            ScanDeviceBean device = deviceListAdapter.getItem(position);
            ScanDeviceBean device = (ScanDeviceBean) parent.getItemAtPosition(position);

            PropertyUtils.setDeviceName(device.getDeviceName());
            PropertyUtils.setDeviceAddress(device.getDeviceMac());
            BleUtils.setFindDeviceOn();
            // Log.d(TAG, "deivce name" + device.getDeviceMac());
            finish();
        });
    }

    private void updateUiState() {
        int state = DataReadService.STATE_DISCONNECTED;
        dataReadService = DataReadService.getInstance();
        if (dataReadService != null) {
            state = dataReadService.getState();
        }
        if (state == DataReadService.STATE_READY || state == DataReadService.STATE_CONNECTED){

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Device already connected ", Snackbar.LENGTH_SHORT);
            snackbar.setTextColor(ContextCompat.getColor(DeviceScanActivity.this,R.color.white));
            snackbar.setBackgroundTint(ContextCompat.getColor(DeviceScanActivity.this,R.color.inactive_bottom_nav));
            snackbar.show();
            // Log.d(TAG, "Connected");
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotToMeasure();
                }

            }, 3000);
        }

    }
    private void setUserName() {

        try {
            String name = PrefManager.getFirstName(DeviceScanActivity.this);
            if (name != null){
                usernameText.setText("Hi " + name);
            } else {
                usernameText.setText("Hi");
            }
        } catch (Exception e){
            Toast.makeText(this, "No user details found", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotToMeasure(){
                    Intent intent = new Intent(DeviceScanActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnScan) {
            if (scanning) {
                YCBTClient.stopScanBle();
                btnScan.setText("Scan");
                scanning = false;
            } else {
                btnScan.setText("Stop");
                scanning = true;
                scan();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUiState();
//        checkLocationPermission();

    }

    private void scan() {
        // Log.e("device", "scan() called");

        YCBTClient.startScanBle((i, scanDeviceBean) -> {

            if (scanDeviceBean != null) {
                if (!listVal.contains(scanDeviceBean.getDeviceMac())) {
                    listVal.add(scanDeviceBean.getDeviceMac());
                    deviceListAdapter.addDevice(scanDeviceBean);
                }
                // Log.e("device", "mac=" + scanDeviceBean.getDeviceMac()
               //         + ";name=" + scanDeviceBean.getDeviceName() + "rssi=" + scanDeviceBean.getDeviceRssi());

            }
        },6 );
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Access")
                        .setMessage("Location access is required to find bluetooth devices!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DeviceScanActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                }

            } else {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permissions")
                        .setMessage("Location permissions are required when searching bluetooth devices!")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        }
    }


}
