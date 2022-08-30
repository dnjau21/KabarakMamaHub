package com.clinkod.kabarak.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.services.DataReadService;
import com.clinkod.kabarak.services.DataSynchronizationService;
import com.clinkod.kabarak.utils.BleUtils;

import java.util.Date;

public class ReadingsReceiver extends BroadcastReceiver {
    private static final String TAG = ReadingsReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        // Log.i(TAG, "Intent readings r,,.....s ");
        if (DataReadService.ACTION_POST_DATA.equals(action)) {
            // Log.i(TAG, "Intent readings received updates ");
            int[] reading = intent.getIntArrayExtra("readings");
            int mother_id = PrefManager.getId(context.getApplicationContext());
            String category = BleUtils.getCategory(reading[0], reading[1]);
            BpAndHeartRate hourlyData = new BpAndHeartRate(new Date(), reading[2], reading[0], reading[1], null, mother_id, category,String.valueOf(System.currentTimeMillis()),"","");
            //DataSynchronizationService.postBpReading(hourlyData);
        }
    }
}