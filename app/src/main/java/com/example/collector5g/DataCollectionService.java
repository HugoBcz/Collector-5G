package com.example.collector5g;

import static android.Manifest.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrength;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ServiceCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class DataCollectionService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private SensorManager mySensorManager;
    private Sensor myAccelerometer;
    private Sensor myGyroscope;

    //final Handler handler = new Handler();
    //final  int delay = 5000;

    public DataCollectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @SuppressLint("NewApi")
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
        Log.i("TAG","START");
        //getLocation();
        //getMobility();
        //getBatteryLevel();
        getCellSignalInfo();

        return super.onStartCommand(intent, flags, startId);
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
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override

                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        Log.i("LOCATION", String.valueOf(location.getLatitude()));
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

    public void getCellSignalInfo() {
        String network = null;
        int cellSigLte = 0;
        int cellCqiLte = 0;
        int cellRsrpLte = 0;
        int cellRsrqLte = 0;

        int csiRsrp = 0;
        int csiRsrq = 0;
        int ssRsrp = 0;
        int ssRsrq = 0;
        List<Integer> cqiReport = new ArrayList<Integer>();

        TelephonyManager teleMan = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") List<CellInfo> cellInfoList = teleMan.getAllCellInfo();

        try {
            Log.i("NETWORKTYPE", "NR3");
            Log.i("TAG",cellInfoList.toString());
            for (CellInfo cellInfo : cellInfoList) {
                /*if (cellInfo instanceof CellInfoLte) {
                    Log.i("NETWORKTYPE", "LTE");
                    network = "Réseau 4G";
                    cellSigLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                    cellCqiLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getCqi();
                    cellRsrpLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
                    cellRsrqLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq();

                    MainActivity.networkType.setText("4G Network");
                    MainActivity.cqiView.setText("CQI : " + cellCqiLte);
                    MainActivity.rsrpView.setText("RSRP : " + cellRsrpLte);
                    MainActivity.rsrqView.setText("RSRQ : " + cellRsrqLte);
                }*/
                Log.i("NETWORKTYPE", "NR2");
                if (cellInfo instanceof CellInfoNr) {
                    Log.i("NETWORKTYPE", "NR");
                    CellSignalStrength cellSigNr = ((CellInfoNr) cellInfo).getCellSignalStrength();
                    if (cellSigNr instanceof CellSignalStrengthNr) {
                        csiRsrp = ((CellSignalStrengthNr) cellSigNr).getCsiRsrp();
                        csiRsrq = ((CellSignalStrengthNr) cellSigNr).getCsiRsrq();
                        ssRsrp = ((CellSignalStrengthNr) cellSigNr).getSsRsrp();
                        ssRsrq = ((CellSignalStrengthNr) cellSigNr).getSsRsrq();

                        MainActivity.networkType.setText("5G Network");
                        MainActivity.rsrpView.setText("RSRP : CSI = " + csiRsrp + " ; SS = " + ssRsrp);
                        MainActivity.rsrqView.setText("RSRQ : CSI = " + csiRsrq + " ; SS = " + ssRsrq);
                    }
                }

                else {
                    MainActivity.networkType.setText("You are not connected to a 4G or 5G network");
                }
            }
        } catch (Exception e) {
            Log.d("NETWORK TYPE", "++++++" + e);
        }


        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, delay);*/
    }

    public void onDestroy() {
        Log.i("DESTROY","Destroy");
        super.onDestroy();
    }
}