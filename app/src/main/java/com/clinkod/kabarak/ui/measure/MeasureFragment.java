package com.clinkod.kabarak.ui.measure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.services.DataReadService;
import com.clinkod.kabarak.ui.MainActivityFragment;
import com.clinkod.kabarak.ui.devicescan.DeviceScanActivity;
import com.clinkod.kabarak.ui.views.HeartBeatView;

public class MeasureFragment extends MainActivityFragment {

    //private DashboardViewModel dashboardViewModel;

    private static final String TAG = MeasureFragment.class.getSimpleName();

    private Button btnRead;
    private TextView status, result, bpResults, hrResults, sysResults, diaResults;
    private HeartBeatView heartBeatView;
    LinearLayout statuslabel;

    private String mDeviceName;
    private String mDeviceAddress;

    private boolean reading = false;

    private NavController controller;

    private int[] latestData = new int[3];

    private Handler handler;

    private DataReadService dataReadService;

    private final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            dataReadService = DataReadService.getInstance();
            if (DataReadService.ACTION_STATE_CHANGED.equals(action)) {

                updateUiState();

            } else if (DataReadService.ACTION_GENERAL_FAILURE.equals(action)) {
                String message = intent.getStringExtra(DataReadService.EXTRA_DATA);

                // Log.d(TAG, "Failure:" + message);
            } else if (DataReadService.ACTION_DATA_AVAILABLE.equals(action)) {
                int[] reading = dataReadService.getData();
                setCurrentReading(reading[0], reading[1], reading[2]);
            }
        }
    };

    private void updateUiState() {
        int state = DataReadService.STATE_DISCONNECTED;
        if (dataReadService != null) {
            state = dataReadService.getState();
        }
        switch (state) {
            case DataReadService.STATE_CONNECTING:
                status.setText("Connecting...");
                btnRead.setEnabled(false);
                btnRead.setText("Start");
                statuslabel.setBackgroundResource(R.drawable.status_connecting);
                status.setBackgroundResource(R.drawable.status_connecting);
                reading = false;
                break;
            case DataReadService.STATE_CONNECTED:
                status.setText("Initializing device...");
                btnRead.setEnabled(true);
                statuslabel.setBackgroundResource(R.drawable.status_connecting);
                status.setBackgroundResource(R.drawable.status_connecting);
                reading = false;
                break;
            case DataReadService.STATE_CONNECTION_FAILED:
                status.setText("Connection Failed");
                btnRead.setEnabled(false);
                btnRead.setText("Start");
                statuslabel.setBackgroundResource(R.drawable.status_failed);
                status.setBackgroundResource(R.drawable.status_failed);
                reading = false;
                break;
            case DataReadService.STATE_DISCONNECTED:
                status.setText("Disconnected");
                btnRead.setEnabled(false);
                btnRead.setText("Start");
                statuslabel.setBackgroundResource(R.drawable.status_failed);
                status.setBackgroundResource(R.drawable.status_failed);
                status.setTextColor(Color.WHITE);
                reading = false;
                break;
            case DataReadService.STATE_READY:
                status.setText("Ready");
                btnRead.setEnabled(true);
                btnRead.setText("Start");
                statuslabel.setBackgroundResource(R.drawable.ic_statusframe);
                status.setBackgroundResource(R.drawable.ic_statusframe);
                status.setTextColor(Color.BLACK);
                reading = false;
                break;
            case DataReadService.STATE_STARTING_MEASUREMENT:
                status.setText("Starting measurement...");
                btnRead.setEnabled(true);
                btnRead.setText("Stop");
                statuslabel.setBackgroundResource(R.drawable.status_measuring);
                status.setBackgroundResource(R.drawable.status_measuring);
                status.setTextColor(Color.WHITE);
                reading = true;
                break;
            case DataReadService.STATE_READING_DATA:
                status.setText("Measuring...");
                //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                TODO - Animate heart reading state
//                if(!heartBeatView.isHeartBeating()){
//                    heartBeatView.start();
//                }
                statuslabel.setBackgroundResource(R.drawable.status_measuring);
                status.setBackgroundResource(R.drawable.status_measuring);
                btnRead.setEnabled(true);
                btnRead.setText("Stop");
                reading = true;

                break;
            case DataReadService.STATE_DONE:
                status.setText("Done");
                btnRead.setEnabled(true);
                btnRead.setText("Start");
                reading = false;
                statuslabel.setBackgroundResource(R.drawable.status_ready);
                status.setBackgroundResource(R.drawable.status_ready);
//                if(heartBeatView.isHeartBeating()){
//                    heartBeatView.stop();
//                }
                //getActivityAndMood();

                break;
            case DataReadService.STATE_NOT_SUPPORTED:
                status.setText("Device not supported");
                btnRead.setEnabled(false);
                btnRead.setText("Start");
                reading = false;
                break;

            default:
                status.setText("An error occurred!");
//                if(heartBeatView.isHeartBeating()){
//                    heartBeatView.stop();
//                }
                btnRead.setEnabled(true);
                btnRead.setText("Start");
                reading = false;

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_measure, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);

        handler = new Handler();
        controller = NavHostFragment.findNavController(this);

        btnRead = (Button) root.findViewById(R.id.startReading);
//        result = root.findViewById(R.id.result);
        status = (TextView) root.findViewById(R.id.status);
        sysResults = root.findViewById(R.id.sysResults);
        diaResults = root.findViewById(R.id.diaResults);
        hrResults = root.findViewById(R.id.hrResults);
        statuslabel = root.findViewById(R.id.linearStatus);
        //status.setText("Initializing...");

//        heartBeatView = (HeartBeatView) root.findViewById(R.id.heartBeat);
//        heartBeatView.setDurationBasedOnBPM(50);

        btnRead.setOnClickListener(v -> {
            if (dataReadService != null && !reading) {
                dataReadService.sendReadCommands();
                //dataReadService.sendGetTempCommands();
            } else if (dataReadService != null) {
                dataReadService.stopReading();
            }
        });


        mDeviceName = PropertyUtils.getDeviceName();
        mDeviceAddress = PropertyUtils.getDeviceAddress();

        if (mDeviceAddress == null) {
            startActivity(new Intent(getActivity(), DeviceScanActivity.class));
            //connect(mDeviceAddress);
        }

        getActivity().registerReceiver(bleServiceReceiver, makeGattUpdateIntentFilter());

        /*if(!Utils.isServiceRunning(getActivity(), DataReadService.class))
            DataReadService.startActionDataReadService(getActivity());*/

        if (DataReadService.getInstance() == null) {
            DataReadService.startActionDataReadService(getActivity());
            status.setText("Initializing...");
        } else {
            dataReadService = DataReadService.getInstance();

            if (dataReadService.getState() == DataReadService.STATE_DISCONNECTED) {
                DataReadService.startActionDataReadService(getActivity());
                status.setText("Initializing...");
            }

            updateUiState();
        }

        return root;
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DataReadService.ACTION_GENERAL_FAILURE);
        intentFilter.addAction(DataReadService.ACTION_STATE_CHANGED);
        intentFilter.addAction(DataReadService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void setCurrentReading(int systole, int diastole, int heartrate) {

        latestData = new int[]{systole, diastole, heartrate};

//        result.setText(String.format("Reading: %d/%d %d", systole, diastole, heartrate) );
        sysResults.setText(String.valueOf(systole));
        diaResults.setText(String.valueOf(diastole));
//        bpResults.setText(String.format("Blood Pressure: %d/ %d", systole, diastole) );
        hrResults.setText(String.valueOf(heartrate));

    }

    @Override
    public void onPause() {
        super.onPause();
        // Log.i(TAG, "On pause called");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        requireActivity().unregisterReceiver(bleServiceReceiver);
        // Log.i(TAG, "onDetach()");
    }
}