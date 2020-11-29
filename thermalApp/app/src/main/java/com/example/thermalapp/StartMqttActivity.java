package com.example.thermalapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class StartMqttActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_mqtt);

        ImageButton imgBtn = findViewById(R.id.btn_info);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(StartMqttActivity.this, MainActivity.class);
            startActivity(intent);
            showToast();
            //finish();
            }
        });

        Button checkTempBtn = findViewById(R.id.btn_temp_check);
        checkTempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartMqttActivity.this, StartTempCheckActivity.class);
                startActivity(intent);
            }
        });
    }


    public void showToast(){
        LayoutInflater layoutInflater = getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ipAddr = wifiIpAddress(getApplicationContext());
        String ssid = wifiInfo.getSSID();

        TextView textView = layout.findViewById(R.id.toast_txt);
        textView.setText("Ip Address: " +  ipAddr + "\n" + "Wifi SSID: " + ssid);

        textView.setTextColor(Color.WHITE);

        Toast toast = new Toast(getApplicationContext());
        textView.setGravity(Gravity.CENTER);
        toast.setDuration(500);
        toast.setView(layout);

        toast.show();
    }

    private String wifiIpAddress(Context applicationContext) {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

}
