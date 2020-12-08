package com.example.thermalapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements ConnectivityCheck.ConnectivityReceiveListener {

    Button btnNWCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNWCheck = findViewById(R.id.btn);

        //Manually check network status
        checkConnectivityNetwork();

        btnNWCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnectivityNetwork();
            }
        });
    }

    private void checkConnectivityNetwork() {
        boolean isConnected = ConnectivityCheck.isConnected();

        showSnackBar(isConnected);
        changeActivityToMqttConnection();

        if (!isConnected) {
            changeActivity();
        }
    }

    private void changeActivityToMqttConnection() {
        Intent intent = new Intent(this, StartMqttActivity.class);
        startActivity(intent);
    }

    private void changeActivity() {
        Intent intent = new Intent(this, OfflineActivity.class);
        startActivity(intent);
    }

    private void showSnackBar(boolean isConnected) {
        String msg;
        int color;
        if (isConnected) {
            msg = "Your Phone is Connected";
            color = Color.GREEN;
        } else {
            msg = "Your Phone is NOT Connected";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), msg, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register Intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);

        ConnectivityCheck connectivityCheck = new ConnectivityCheck();
        registerReceiver(connectivityCheck, intentFilter);

        //Register connection status listener
        ConnectivityCheckApplication.getInstance().setConnectivityListener(this);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (!isConnected) {
            changeActivity();
        }
        showSnackBar(isConnected);
    }
}

