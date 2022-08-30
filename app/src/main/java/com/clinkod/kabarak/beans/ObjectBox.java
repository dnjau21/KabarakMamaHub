package com.clinkod.kabarak.beans;

import android.content.Context;

import com.clinkod.kabarak.BuildConfig;
import com.clinkod.kabarak.models.MyObjectBox;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;


public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context)
                .build();
        /*boxStore.close();
        boxStore.deleteAllFiles();*/
        //Could be disabled.
        if (BuildConfig.DEBUG) {
            boolean started = new AndroidObjectBrowser(boxStore).start(context);
            // Log.i("ObjectBrowser", "Started: " + started);
        }
    }

    public static BoxStore get() { return boxStore; }
}