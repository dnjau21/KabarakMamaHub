package com.clinkod.kabarak.utils;

import static com.clinkod.kabarak.services.DataReadService.*;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.clinkod.kabarak.MainActivity;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.broadcastreceivers.AlarmReceiver;
import com.clinkod.kabarak.broadcastreceivers.BpAndHeartRateAlarmReceiver;
import com.clinkod.kabarak.broadcastreceivers.DailyDataAlarmReceiver;
import com.clinkod.kabarak.interfaces.ReconnectCallback;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.services.DataReadService;
import com.yucheng.ycbtsdk.Constants;
import com.yucheng.ycbtsdk.YCBTClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private Handler handler = new Handler();
    public static ReconnectCallback callback;
    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Date convertStringToDate(String date) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        return sdf.parse(date);
    }



    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public static boolean isTimeBetweenTwoTimes(String initialTime, String finalTime,
                                               String currentTime) throws ParseException {



        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) &&
                currentTime.matches(reg))
        {
            boolean valid = false;
            //Start Time
            //all times are from java.util.Date
            Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0)
            {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime()))
            {
                valid = true;
                return valid;
            } else {
                throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
            }
        }

        return false;
    }

    public static void setUpAlarms(Context context){
        setUpMeasureAlarm(context);
//        setUpDailyDataAlarm(context);
    }

    public static void setUpMeasureAlarm(Context context){

       /* Set up repeating alarm*/
        AlarmManager alarmManager;
        PendingIntent alarmPendingIntent;

//        long interval = 180000L;
        long interval = PropertyUtils.getMeasureFrequency() * 60L * 1000L;


        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context.getApplicationContext(), BpAndHeartRateAlarmReceiver.class);

        alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        /*if(!PropertyUtils.doesAutomaticMeasure()){
            alarmManager.cancel(alarmPendingIntent);

            return;
        }*/

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
               // SystemClock.elapsedRealtime() + 10000, // This is for testing
                SystemClock.elapsedRealtime() + interval,
                interval, alarmPendingIntent);
    }

    public static void setUpDailyDataAlarm(Context context){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20);

    // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.


        /* Set up repeating alarm*/
        AlarmManager alarmManager;
        PendingIntent alarmPendingIntent;


        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context.getApplicationContext(), DailyDataAlarmReceiver.class);
        alarmIntent.putExtra("type", AlarmReceiver.ALARM_TYPE_DAILY_DATA);

        alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmPendingIntent);
    }

    public static Boolean isActivityRunning(Context context, Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String loadSurveyJson(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager methodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert methodManager != null && view != null;
        methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        InputMethodManager methodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert methodManager != null && view != null;
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    public static void show_Notification(Context context){

        Intent intent=new Intent(context, MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,1,intent,0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Notification notification=new Notification.Builder(context,CHANNEL_ID)
                .setContentText("Please connect your watch")
                .setContentTitle(" Watch not connected")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo)
                .setPriority(Notification.PRIORITY_MAX)
                .setLights(0xff0000, 100, 100)
                .setSound(alarmSound)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .addAction(android.R.drawable.sym_action_chat,"Title",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .build();

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);

    }


    public static boolean connectDevice(Context context){
        AtomicInteger stateB = new AtomicInteger();
        ReconnectCallback callback ;
        String deviceAddress = PropertyUtils.getDeviceAddress();
        if(deviceAddress == null){
            return false;
        }

        YCBTClient.connectBle(deviceAddress, i -> {

             int state = STATE_DISCONNECTED;
                    if (i == Constants.CODE.Code_OK){
                        updateState(true);

                        stateB.set(1);
                    }else{
                        state = STATE_DISCONNECTED;
                        updateState(false);
                        stateB.set(1);
                    }
                }
        );

        return false;
    }

    private static void updateState(Boolean data){

        DataReadService.updatStatusChange(data);
    }

    public static boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
}
