package com.example.thermalapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectivityCheck extends BroadcastReceiver {

    public static ConnectivityReceiveListener connectivityReceiveListener;

    public ConnectivityCheck() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiveListener != null) {
            connectivityReceiveListener.onNetworkConnectionChanged(isConnected);
        }

    }

    /**
     * check manually for the connection
     *
     * @return
     */
    public static boolean isConnected() {
        final ConnectivityManager cm = (ConnectivityManager) ConnectivityCheckApplication
                .getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }

        return false;
    }

    /**
     * interface returns boolean value onNetworkConnectionChanged
     */
    public interface ConnectivityReceiveListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
