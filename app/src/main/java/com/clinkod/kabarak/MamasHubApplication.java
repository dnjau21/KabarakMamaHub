package com.clinkod.kabarak;

import android.app.Application;

import com.clinkod.kabarak.beans.ObjectBox;
import com.yucheng.ycbtsdk.YCBTClient;

public class MamasHubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ObjectBox.init(getApplicationContext());

        YCBTClient.initClient(getApplicationContext(),true);
    }
}
