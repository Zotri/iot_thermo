package com.example.thermalapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

        if(connectivityReceiveListener!=null){
            connectivityReceiveListener.onNetworkConnectionChanged(isConnected);
        }

    }

    /**
     * interface returns boolean value onNetworkConnectionChanged
     */
    public interface ConnectivityReceiveListener{
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
