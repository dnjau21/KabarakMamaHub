package com.clinkod.kabarak.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.clinkod.kabarak.fhir.helper.FormatterClass;

import java.security.Provider;

public class NotificationService extends Service {

    public NotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
//        Toast.makeText(getApplicationContext(),"This is a Service running in Background",
//                Toast.LENGTH_SHORT).show();
        FormatterClass formatterClass = new FormatterClass();
//        formatterClass.generateNotification(getApplicationContext());
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }


}
