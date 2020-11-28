package com.example.thermalapp;

import android.app.Application;

public class ConnectivityCheckApplication extends Application {

    private static ConnectivityCheckApplication connectivityCheckApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityCheckApplicationInstance = this;
    }

    public static synchronized ConnectivityCheckApplication getInstance() {
        return connectivityCheckApplicationInstance;
    }

    public void setConnectivityListener(ConnectivityCheck.ConnectivityReceiveListener listener) {
        ConnectivityCheck.connectivityReceiveListener = listener;
    }
}
