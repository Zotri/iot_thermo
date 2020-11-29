package com.example.thermalapp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.text.MessageFormat;

public class StartTempCheckActivity extends AppCompatActivity {
    final String BROKER_URL = "tcp://192.168.1.113:1883";
    final String TOPIC = "tele/temp/SENSOR";
    final String USERNAME = "motri";
    final String PASSWORD = "motri";

    TextView dataReceived;

    TextView seekBarText;
    int minimumVal = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_temp_check);

        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setProgress(20);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(28);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        seekBarText = findViewById(R.id.text_seek_bar);

        seekBarText.setText(MessageFormat.format("Slide to set temperature degree: {0}°", progress));


        Button connectMqttBtn = findViewById(R.id.btn_mqtt_connect);
        connectMqttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(StartTempCheckActivity.this, StartTempCheckActivity.class);
                //startActivity(intent);
                startMqtt();
                //finish();
            }
        });
    }

    private void startMqtt() {
        dataReceived = (TextView) findViewById(R.id.text_temp_value);

        String clientId = "android " + MqttClient.generateClientId();
        MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), BROKER_URL, clientId);
        byte[] payload = "log into the temperature topic...".getBytes();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setWill(TOPIC, payload,0,false );
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getBaseContext(), "Client is connected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getBaseContext(), "Connection with MQTT is lost", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress >= minimumVal) {
                seekBar.setProgress(progress);
            seekBarText.setText(MessageFormat.format("Temperature Value: {0}°", progress));
            } else {
                seekBar.setProgress(minimumVal);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            Toast.makeText(StartTempCheckActivity.this,
                    "Set temperature value on", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Toast.makeText(StartTempCheckActivity.this,
                    "Temperature is set", Toast.LENGTH_SHORT).show();
        }
    };

}
