/*
package com.clinkod.kabarak.ui.devicescan;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.PropertyUtils;
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;
import com.yucheng.ycbtsdk.Response.BleScanResponse;
import com.yucheng.ycbtsdk.YCBTClient;

import java.util.ArrayList;
import java.util.List;

public class DeviceScanFragment extends Fragment {

    private DeviceScanViewModel mViewModel;
    private ListView deviceList;
    private List<String> listVal = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    private Button btnScan;
    private boolean scanning = false;
    private NavController controller;

    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;

    public static DeviceScanFragment newInstance() {
        return new DeviceScanFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_scan_fragment, container, false);

        controller = NavHostFragment.findNavController(this);

        deviceList = view.findViewById(R.id.device_list);
        btnScan = view.findViewById(R.id.btnScan);

        deviceListAdapter = new DeviceListAdapter(getActivity(), listVal);
        deviceList.setAdapter(deviceListAdapter);

        deviceList.setOnItemClickListener((parent, view1, position, id) -> {
            YCBTClient.stopScanBle();
            btnScan.setText("Scan");
            scanning = false;

            ScanDeviceBean device = deviceListAdapter.getItem(position);

            PropertyUtils.setDeviceName(device.getDeviceName());
            PropertyUtils.setDeviceAddress(device.getDeviceMac());

            controller.popBackStack();
        });

        btnScan.setOnClickListener(v -> {
            if(scanning){
                YCBTClient.stopScanBle();
                btnScan.setText("Scan");
                scanning = false;
            }else{
                btnScan.setText("Stop");
                scanning = true;
                scan();
            }

        });

        checkLocationPermission();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DeviceScanViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            controller.popBackStack();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void scan(){
        // Log.e("device", "scan() called");

        YCBTClient.startScanBle(new BleScanResponse() {
            @Override
            public void onScanResponse(int i, ScanDeviceBean scanDeviceBean) {
                if (scanDeviceBean != null) {
                    deviceListAdapter.addDevice(scanDeviceBean);
                    if (!listVal.contains(scanDeviceBean.getDeviceMac())) {
                        listVal.add(scanDeviceBean.getDeviceMac());
                        deviceListAdapter.addDevice(scanDeviceBean);
                    }

                    // Log.e("device", "mac=" + scanDeviceBean.getDeviceMac() + ";name=" + scanDeviceBean.getDeviceName() + "rssi=" + scanDeviceBean.getDeviceRssi());

                }
            }
        }, 30);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDia// Log.Builder(getContext())
                        .setTitle("Location Access")
                        .setMessage("Location access is required to find bluetooth devices!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                }

            } else {

                new AlertDia// Log.Builder(getContext())
                        .setTitle("Location Permissions")
                        .setMessage("Location permissions are required when searching bluetooth devices!")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dia// Log.dismiss();
                            }
                        }).show();

            }
        }
    }
}
*/
