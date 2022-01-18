package com.example.collector5g;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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

import org.w3c.dom.Text;

import java.io.IOException;

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

    //network data
    static TextView networkData;
    static TextView rsrp;
    static TextView rsrq;
    static TextView networkType;

    //battery display
    static TextView battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
        getDeviceÌnfo();

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                serviceIntent.setAction("start collecting service");
                startService(serviceIntent);

                }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                serviceIntent.setAction("stop collecting service");
                stopService(serviceIntent);
            }
        });
    }

    private void initComponent() {
        accel_data = findViewById(R.id.accel_data);
        accel_x = findViewById(R.id.accel_x);
        accel_y = findViewById(R.id.accel_y);
        accel_z = findViewById(R.id.accel_z);

        networkData = findViewById(R.id.networkData);
        networkType = findViewById(R.id.networkType);
        rsrp = findViewById(R.id.rsrp);
        rsrq = findViewById(R.id.rsrq);

        Location = findViewById(R.id.Location);
        latitude =  findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        altitude = findViewById(R.id.altitude);

        dn = (TextView) findViewById(R.id.dn);
        dm = (TextView) findViewById(R.id.dm);
        dv = (TextView) findViewById(R.id.dv);

        battery = (TextView) findViewById(R.id.battery);
    }

    private void getDeviceÌnfo() {
        dn.setText(MANUFACTURER);
        dm.setText(MODEL);
        dv.setText("Android " + Build.VERSION.RELEASE);
    }

}