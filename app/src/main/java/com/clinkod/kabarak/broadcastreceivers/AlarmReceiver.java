package com.clinkod.kabarak.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.clinkod.kabarak.Registration;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.utils.BackgroundDataReadTrigger;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    public static final String ALARM_TYPE_MEASURE = "measure";
    public static final String ALARM_TYPE_ACTIVITY_MOOD = "activity_mood";
    public static final String ALARM_TYPE_DAILY_DATA = "daily_data";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.i(TAG, "Data read alarm received");

        String alarmType  = intent.getStringExtra("type");

        if(alarmType == null)
            return;
        if(alarmType.equals(ALARM_TYPE_MEASURE)){
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BPAnalytics::WakelockTag");
            wakeLock.acquire();

            // Lets do the actual work
            BackgroundDataReadTrigger.trigger(context, () -> wakeLock.release());
        }else if(alarmType.equals(ALARM_TYPE_ACTIVITY_MOOD)){
            PropertyUtils.putProperty(Registration.PROPERTY_ACTIVITY_AND_MOOD, true);
        }else if(alarmType.equals(ALARM_TYPE_DAILY_DATA)){
            PropertyUtils.putProperty(Registration.PROPERTY_DAILY_DATA, true);
        }

    }
}
