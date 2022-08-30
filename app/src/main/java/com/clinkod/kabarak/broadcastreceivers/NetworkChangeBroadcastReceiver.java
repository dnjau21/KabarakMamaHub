package com.clinkod.kabarak.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.clinkod.kabarak.services.DataSynchronizationService;
import com.clinkod.kabarak.utils.NetworkManager;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;

    private static final String TAG = NetworkChangeBroadcastReceiver.class.getSimpleName();

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        int status = 0;
        if (conn == TYPE_WIFI) {
            status = NETWORK_STATUS_WIFI;
        } else if (conn == TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
       checkNetworkAndSync(context);
    }

    public static void checkNetworkAndSync(Context context){
        int status = getConnectivityStatusString(context);
        // Log.e("Network Change", "Received network change broadcast");
        if (status == NETWORK_STATUS_NOT_CONNECTED) {
            NetworkManager.getInstance().setConnected(false);
            // Log.e(TAG, "Network Not connected");
        } else {
            // Log.e(TAG, "Network connected");
            NetworkManager.getInstance().setConnected(true);
            DataSynchronizationService.startActionSynchronizeData(context);
        }
    }

    public NetworkChangeBroadcastReceiver(){

    }
}
