package com.clinkod.kabarak.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.clinkod.kabarak.Registration;
import com.clinkod.kabarak.models.PropertyUtils;

/**
 * Created by Shadrack Mwai on 4/7/21. Clinkod Ltd,  shadmwai@gmail.com
 */
public class DailyDataAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PropertyUtils.putProperty(Registration.PROPERTY_DAILY_DATA, true);
    }
}
