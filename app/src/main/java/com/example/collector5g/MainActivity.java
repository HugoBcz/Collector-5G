package com.example.collector5g;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    //device info
    TextView dn;
    TextView dm;
    TextView dv;

    //location
    static TextView Location;
    static TextView latitude;
    static TextView longitude;
    static TextView altitude;


    //mobility accelerometer
    static TextView accel_data;
    static TextView accel_x;
    static TextView accel_y;
    static TextView accel_z;

    //mobility gyroscope
    static TextView gyros_data;
    static TextView gyros_x;
    static TextView gyros_y;
    static TextView gyros_z;

    //battery display
    static TextView battery;

    //5G Collection
    static TextView networkData;
    static TextView rsrp;
    static TextView rsrq;
    static TextView networkType;

    private static MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883", clientId);
        //client = new MqttAndroidClient(this.getApplicationContext(), "mqtt://localhost:1883", clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MQTTCONNECT", "Connect Success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MQTTCONNECT", "Connect Failure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }*/

        initComponent();
        getDeviceÌnfo();

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clientId = MqttClient.generateClientId();
                client = new MqttAndroidClient(getApplicationContext(), "tcp://broker.hivemq.com:1883", clientId);
                //client = new MqttAndroidClient(this.getApplicationContext(), "mqtt://localhost:1883", clientId);
                MqttConnectOptions options = new MqttConnectOptions();

                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d("MQTTCONNECT", "Connect Success");
                }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d("MQTTCONNECT", "Connect Failure");

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                Intent serviceIntent = new Intent(getApplicationContext(), DataCollectionService.class);
                serviceIntent.setAction("start collecting service");
                startService(serviceIntent);

            }

        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), DataCollectionService.class);
                serviceIntent.setAction("stop collecting service");
                stopService(serviceIntent);

                try {
                    IMqttToken disconToken = client.disconnect();
                    disconToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d("MQTTCONNECT", "Disconnect Success");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            Log.d("MQTTCONNECT", "Disconnect Failure");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initComponent() {
        accel_data = findViewById(R.id.accel_data);
        accel_x = findViewById(R.id.accel_x);
        accel_y = findViewById(R.id.accel_y);
        accel_z = findViewById(R.id.accel_z);

        Location = findViewById(R.id.Location);
        latitude =  findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        altitude = findViewById(R.id.altitude);

        dn = (TextView) findViewById(R.id.dn);
        dm = (TextView) findViewById(R.id.dm);
        dv = (TextView) findViewById(R.id.dv);

        battery = (TextView) findViewById(R.id.battery);

        networkData = findViewById(R.id.networkData);
        networkType = findViewById(R.id.networkType);
        rsrp = findViewById(R.id.rsrp);
        rsrq = findViewById(R.id.rsrq);
    }

    private void getDeviceÌnfo() {
        dn.setText(MANUFACTURER);
        dm.setText(MODEL);
        dv.setText("Android " + Build.VERSION.RELEASE);
    }

    public static void pub(JSONObject message) {
        Log.d("MQTT","Publish");
        String topic = "5Gcollection";
        //String message = "First publish message";
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");
            //MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message.toString().getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}