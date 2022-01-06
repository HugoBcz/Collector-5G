package com.example.collector5g;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.hardware.Sensor;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;

import android.os.Build;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.Executor;


public class CollectingService extends Service {
    public CollectingService() {
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private SensorManager mySensorManager;
    private Sensor myAccelerometer;
    private Sensor myGyroscope;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);  // excludes gravity, good for motion detection
        myGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("STATUS", "service started");
        getLocation();
        getMobility();
        getBatteryLevel();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void getMobility() {

            mySensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event != null) {
                        MainActivity.accel_x.setText(String.format("%.2f", event.values[0]));
                        MainActivity.accel_y.setText(String.format("%.2f", event.values[1]));
                        MainActivity.accel_z.setText(String.format("%.2f", event.values[2]));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            }, myAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);



/*
        mySensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event != null) {
                    MainActivity.gyros_x.setText(String.format("%.2f", event.values[0]));
                    MainActivity.gyros_y.setText(String.format("%.2f", event.values[1]));
                    MainActivity.gyros_z.setText(String.format("%.2f", event.values[2]));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, myGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
 */

    }


    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        MainActivity.latitude.setText("Latitude : " + location.getLatitude() + " °N");
                        MainActivity.longitude.setText("Longitude : " + location.getLongitude() + " °E");
                        MainActivity.altitude.setText("Altitude : " + (int) (Math.round(location.getAltitude() * 100)) / 100.0 + " meters");

                    }
                });
    }

    public void getBatteryLevel() {
        // get battery level
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batteryPercentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        //Log.i("BATTERY LEVEL", "" + batteryPercentage + "%");
        MainActivity.battery.setText("BATTERY LEVEL: " + batteryPercentage + "%");
    }
}