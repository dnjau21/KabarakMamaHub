package com.clinkod.kabarak.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.utils.BackgroundDataReadTrigger;
import com.clinkod.kabarak.utils.BleUtils;
import com.yucheng.ycbtsdk.Constants;

/**
 * Created by Shadrack Mwai on 4/7/21. Clinkod Ltd,  shadmwai@gmail.com
 */
public class BpAndHeartRateAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = BpAndHeartRateAlarmReceiver.class.getSimpleName();

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    @Override
    public void onReceive(Context context, Intent intent) {
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "MamasHub::WakelockTag");
        wakeLock.acquire();

        // Lets do the actual work
        int bleState = BleUtils.checkConnectedStatus();
         //connected success
            BackgroundDataReadTrigger.trigger(context, () -> wakeLock.release());
            Log.i(TAG, "Alarm time triggered");
//
//            String mDeviceAddress = PropertyUtils.getDeviceAddress();
//            Log.i(TAG, mDeviceAddress);
//            BleUtils.recoonectDevice(mDeviceAddress);
//

    }
}
