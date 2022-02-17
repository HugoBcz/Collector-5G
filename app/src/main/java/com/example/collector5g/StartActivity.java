package com.example.collector5g;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

public class StartActivity extends AppCompatActivity {

    // EditText cells that are filled in by the user
    static EditText brokerAddress;
    static EditText username;
    static EditText password;
    static EditText period;
    static EditText topic;

    // variables associated with the EditText cells
    private String address = "";
    private String user = "";
    private String passwd = "";
    static int delay = 5;
    static String tp = "";

    // Mqtt client variables
    static MqttAndroidClient client;
    static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button launchButton = findViewById(R.id.launchButton);
        initComponent();

        // ask for permissions
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },100);
            return;
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_PHONE_STATE },100);
            return;
        }

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // associate the variables to the value defined by the user
                address = brokerAddress.getText().toString();
                user = username.getText().toString();
                passwd = password.getText().toString();
                tp = topic.getText().toString();
                String pd = period.getText().toString();
                if (!pd.equals("")) {
                    delay = Integer.valueOf(pd);
                }

                Toast.makeText(getApplicationContext(),"Connect Success",Toast.LENGTH_SHORT).show();
                Intent activityIntent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(activityIntent);

                // mqtt client parameters and options before connecting to the broker
                String clientId = MqttClient.generateClientId();
                id = clientId;
                client = new MqttAndroidClient(getApplicationContext(), address, clientId);
                //client = new MqttAndroidClient(this.getApplicationContext(), "mqtt://localhost:1883", clientId);

                MqttConnectOptions options = new MqttConnectOptions();
                //options.setUserName(user);
                //options.setPassword(passwd.toCharArray());

                // connection to the broker
                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d("MQTTCONNECT", "Connect Success");
                            Toast.makeText(getApplicationContext(),"Connect Success",Toast.LENGTH_SHORT).show();
                            Intent activityIntent = new Intent(StartActivity.this, MainActivity.class);
                            startActivity(activityIntent);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d("MQTTCONNECT", "Connect Failure");
                            Toast.makeText(getApplicationContext(),"Connect Failure",Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // initialize the EditText components
    private void initComponent() {
        brokerAddress = findViewById(R.id.brokerAddress);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        period = findViewById(R.id.period);
        topic = findViewById(R.id.topic);
    }

    // publish the JSON object to the broker
    public static void pub(JSONObject message) {
        Log.d("MQTT","Publish");
        String topic = tp;
        byte[] encodedPayload = new byte[0];
        try {
            client.publish(topic, message.toString().getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}