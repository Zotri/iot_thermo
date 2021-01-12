package com.example.thermalapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class StartTempCheckActivity extends AppCompatActivity implements MqttCallbackExtended, NumberPicker.OnValueChangeListener {
    public static String TEMPVALUE;
    final String BROKER_URL = "tcp://192.168.1.113:1883";
    final String SENSOR_TOPIC = "tele/temp/SENSOR";
    final String SWITCH_POWER_STAT_TOPIC = "stat/temp/POWER";
    final String USERNAME = "motri";
    final String PASSWORD = "motri";
    final String CLIENT_ID = "android " + MqttClient.generateClientId();
    final String SENSOR_KEY = "SENSOR";
    final String POWER_KEY = "POWER";
    final String POWER_OFF = "POWEROFF";
    final String POWER_ON = "POWERON";
    public MqttAndroidClient mqttAndroidClient;
    MemoryPersistence persistence = new MemoryPersistence();
    TextView sensor_dataReceived;
    TextView power_dataReceived;
    TextView topicSensorSelected;
    TextView topicPowerSelected;
    TextView tvShowNumberPicker;
    Button connectMqttBtn;
    Button switchOffPowerBtn;
    Button switchONPowerBtn;
    Button setTemperatureBtn;
    NumberPickerWithXml numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_temp_check);

        tvShowNumberPicker = findViewById(R.id.show_number_picker_text);
        numberPicker = findViewById(R.id.temp_degree_picker);
        numberPicker.setOnValueChangedListener(this);

        topicPowerSelected = findViewById(R.id.text_topic_power_value);
        topicSensorSelected = findViewById(R.id.text_topic_sensor_value);

        setTemperatureBtn = findViewById(R.id.btn_set_temp);

        switchONPowerBtn = findViewById(R.id.btn_switch_power_on);
        switchONPowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMqttCmnd("http://192.168.1.118/cm?cmnd=Power%20On", POWER_ON);
            }
        });

        switchOffPowerBtn = findViewById(R.id.btn_switch_power_off);
        switchOffPowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMqttCmnd("http://192.168.1.118/cm?cmnd=Power%20off", POWER_OFF);
            }
        });

        connectMqttBtn = findViewById(R.id.btn_mqtt_connect);
        connectMqttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMqtt();
            }
        });
    }

    private void sendMqttCmnd(String url, final String key) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        switch (key) {
                            case SENSOR_KEY:
                                try {
                                    String tempValue = response.getJSONObject("StatusSNS").toString();
                                    mqttAndroidClient.publish(SENSOR_TOPIC, tempValue.getBytes(), 0, true);
                                    Log.d("response from sensor", response.toString());
                                } catch (MqttException | JSONException e) {
                                    e.printStackTrace();
                                    Log.d("response", response.toString());
                                }
                            case POWER_KEY:
                                try {
                                    String powerValue = response.getJSONObject("POWER").toString();
                                    mqttAndroidClient.publish(SWITCH_POWER_STAT_TOPIC, powerValue.getBytes(), 0, true);
                                    Log.d("response from power switch", powerValue);
                                } catch (MqttException | JSONException e) {
                                    e.printStackTrace();
                                    Log.d("response", response.toString());
                                }
                            case POWER_OFF:
                                try {
                                    String powerOffCmnd = response.getJSONObject("POWER").toString();
                                    mqttAndroidClient.publish(SWITCH_POWER_STAT_TOPIC, powerOffCmnd.getBytes(), 0, true);
                                    Log.d("response from power switch", powerOffCmnd);
                                } catch (MqttException | JSONException e) {
                                    e.printStackTrace();
                                    Log.d("response", response.toString());
                                }
                            case POWER_ON:
                                try {
                                    String powerOnCmnd = response.getJSONObject("POWER").toString();
                                    mqttAndroidClient.publish(SWITCH_POWER_STAT_TOPIC, powerOnCmnd.getBytes(), 0, true);
                                    Log.d("response from power switch", powerOnCmnd);
                                } catch (MqttException | JSONException e) {
                                    e.printStackTrace();
                                    Log.d("response", response.toString());
                                }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    ;

    private void startMqtt() {
        sensor_dataReceived = findViewById(R.id.text_temp_value);
        power_dataReceived = findViewById(R.id.text_power_value);

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), BROKER_URL, CLIENT_ID, persistence);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = mqttAndroidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribe(mqttAndroidClient, SENSOR_TOPIC);
                    subscribe(mqttAndroidClient, SWITCH_POWER_STAT_TOPIC);

                    mqttAndroidClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {
                            Log.d("mqtt", "connectionLost");
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            if (topic.equals(SENSOR_TOPIC)) {
                                String payloadToJson = new JSONObject(String.valueOf(message)).toString();
                                JSONObject json = new JSONObject(payloadToJson);
                                JSONObject location = json.getJSONObject("DS18B20");
                                TEMPVALUE = location.getString("Temperature");

                                Log.d("temperature value", TEMPVALUE + "°");
                                sensor_dataReceived.setText(TEMPVALUE + "\u2103");

                            } else if (topic.equals(SWITCH_POWER_STAT_TOPIC)) {
                                Log.d("Power", new String(message.getPayload()));
                                power_dataReceived.setText("Thermostat is " + new String(message.getPayload()));
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                            Log.d("mqtt", "deliveryComplete");
                        }
                    });
                    // We are connected
                    Toast.makeText(getBaseContext(), "Client is connected", Toast.LENGTH_SHORT).show();
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

    public void subscribe(MqttAndroidClient client, final String topic) {
        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (topic.equals("tele/temp/SENSOR")) {
                        sendMqttCmnd("http://192.168.1.117/cm?cmnd=status%2010", SENSOR_KEY);

                        // The message was published
                        Log.w("Mqtt", "SENSOR Subscribed!");
                        topicSensorSelected.setText("Topic SENSOR selected!");
                    } else
                        sendMqttCmnd("http://192.168.1.118/cm?cmnd=Power", POWER_KEY);
                    Log.w("Mqtt", "POWER Subscribed!");
                    topicPowerSelected.setText("Topic POWER selected!");
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

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int chosenValue) {
        Log.d("old", oldValue + "°" + "new" + chosenValue);
        tvShowNumberPicker.setText(chosenValue + " \u2103 degree to set. Click on SET to proceed!");

        try {
            Log.d("TEMPVALUE", "value before conversion " + TEMPVALUE);
            float convertedTemp = Float.parseFloat(TEMPVALUE);
            Log.d("integer static converted", "temp " + convertedTemp);
            if (Math.round(convertedTemp) <= chosenValue) {
                setTemperatureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendMqttCmnd("http://192.168.1.118/cm?cmnd=Power%20On", POWER_ON);
                        Toast.makeText(getBaseContext(), "Thermostat is set", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (Math.round(convertedTemp) >= chosenValue) {
                setTemperatureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        sendMqttCmnd("http://192.168.1.118/cm?cmnd=Power%20off", POWER_OFF);
                        Toast.makeText(getBaseContext(), "Thermostat is off, temperature is satisfying", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (Math.round(convertedTemp) > 27) {
                sendMqttCmnd("http://192.168.1.118/cm?cmnd=Power%20off", POWER_OFF);
                Toast.makeText(getBaseContext(), "Thermostat is off, temperature is too high", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Log.d("Error", "temp convert error");
        }
    }
}
