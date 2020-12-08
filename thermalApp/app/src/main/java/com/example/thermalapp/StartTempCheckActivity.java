package com.example.thermalapp;

import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StartTempCheckActivity extends AppCompatActivity implements MqttCallbackExtended {
    public MqttAndroidClient mqttAndroidClient;

    final String BROKER_URL = "tcp://192.168.1.113:1883";
    final String TOPIC = "tele/temp/SENSOR";
    final String USERNAME = "motri";
    final String PASSWORD = "motri";
    final String CLIENT_ID = "android " + MqttClient.generateClientId();

    TextView dataReceived;
    TextView topicSelected;
    Button connectMqttBtn;

    TextView seekBarText;
    int minimumVal = 14;

    float discrete = 0;
    float start = 0;
    float end = 100;
    float start_pos = 0;
    int start_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_temp_check);

        start = 14;      //you need to give starting value of SeekBar
        end = 30;         //you need to give end value of SeekBar
        start_pos = 16;    //you need to give starting position value of SeekBar

        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setProgress(20);
        seekBar.incrementProgressBy(1);
        seekBar.setMax(28);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        seekBarText = findViewById(R.id.text_seek_bar);

        seekBarText.setText(MessageFormat.format("Slide to set temperature degree: {0}°", progress));


        connectMqttBtn = findViewById(R.id.btn_mqtt_connect);
        connectMqttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(StartTempCheckActivity.this, StartTempCheckActivity.class);
                //startActivity(intent);
                startMqtt();
                //finish();
            }
        });

        start_position = (int) (((start_pos - start) / (end - start)) * 100);
        discrete = start_pos;
        SeekBar seek = (SeekBar) findViewById(R.id.seek_bar);
        seek.setProgress(start_position);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Toast.makeText(getBaseContext(), "discrete = " + String.valueOf(discrete), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                // To convert it as discrete value
                float temp = progress;
                float dis = end - start;
                discrete = (start + ((temp / 100) * dis));
                seekBarText.setText(MessageFormat.format("Temperature Value: {0}°", progress));
            }
        });


    }

    private void startMqtt() {
        dataReceived = (TextView) findViewById(R.id.text_temp_value);
        topicSelected = (TextView) findViewById(R.id.text_topic_value);

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), BROKER_URL, CLIENT_ID);
        byte[] payload = "Android logs out from broker...".getBytes();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setWill(TOPIC, payload, 0, false);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = mqttAndroidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    subscribe(mqttAndroidClient, TOPIC);
                    mqttAndroidClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            String payloadToJson = new JSONObject(new String(String.valueOf(message))).toString();
                            Log.d("temperature", payloadToJson);


                            JSONObject json = new JSONObject(payloadToJson);

                            JSONObject location = json.getJSONObject("DS18B20");
                            String temp = location.getString("Temperature");

                            Log.d("temperature", temp + "°");
                            dataReceived.setText(temp + "°");

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });
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

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d("MQTT", "deliveryComplete");
    }

    public void subscribe(MqttAndroidClient client, String topic) {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.w("Mqtt", "Subscribed!");
                    topicSelected.setText("Topic selected!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }
    //TODO implement publish method
    // publish to subsribe with topic tele/%topic%/STATUS to retrieve a payload with least info as json
    //TODO manually trigger value of current temperature value
}
