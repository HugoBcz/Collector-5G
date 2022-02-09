package com.example.collector5g;

import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;



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

import android.location.LocationListener;
import android.os.BatteryManager;


import android.os.IBinder;

import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthNr;
import android.telephony.TelephonyManager;
import android.util.Log;


import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class CollectingService extends Service {
    public CollectingService() {
    }
    /*
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    */

    private FusedLocationProviderClient mFusedLocationClient;
    private SensorManager mySensorManager;
    private Sensor myAccelerometer;
    private Sensor myGyroscope;
    private SensorEventListener myAccelerometerListener;
    private SensorEventListener myGyroscopeListener;

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
        /*
        getLocation();
        getMobility();
        getBatteryLevel();
        */
        getAllData();
        MainActivity.accel_data.setText("Accelerometer data : in progress...");
        MainActivity.Location.setText("Location : in progress...");
        MainActivity.networkData.setText("Network data : in progress...");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mySensorManager.unregisterListener(myAccelerometerListener);
        mySensorManager.unregisterListener(myGyroscopeListener);

        MainActivity.accel_data.setText("Accelerometer data : stopped");
        MainActivity.Location.setText("Location : stopped");
        MainActivity.networkData.setText("Network data : stopped");

        super.onDestroy();
    }
    public void getAllData(){

        // get battery level
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batteryPercentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        //Log.i("BATTERY LEVEL", "" + batteryPercentage + "%");
        MainActivity.battery.setText("BATTERY LEVEL: " + batteryPercentage + "%");


        // get location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            long locationTime = location.getTime();
                            MainActivity.latitude.setText("Latitude : " + location.getLatitude() + " °N");
                            MainActivity.longitude.setText("Longitude : " + location.getLongitude() + " °E");
                            MainActivity.altitude.setText("Altitude : " + (int) (Math.round(location.getAltitude() * 100)) / 100.0 + " meters");
                        }
                        else {
                            MainActivity.Location.setText("Location : no last known location found");
                        }
                    }
                });


        // get mobility
        myAccelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event != null) {
                    long eventStamp = event.timestamp;
                    MainActivity.accel_x.setText(String.format("%.2f", event.values[0]));
                    MainActivity.accel_y.setText(String.format("%.2f", event.values[1]));
                    MainActivity.accel_z.setText(String.format("%.2f", event.values[2]));
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mySensorManager.registerListener(myAccelerometerListener, myAccelerometer, 500);


        // get network data
        // put the proper function here

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
                if (cellInfo instanceof CellInfoLte) {
                    Log.i("NETWORKTYPE", "LTE");
                    network = "Réseau 4G";
                    cellSigLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                    cellCqiLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getCqi();
                    cellRsrpLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
                    cellRsrqLte = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq();

                    MainActivity.networkType.setText("4G Network");
                    //MainActivity.cqi.setText("CQI : " + cellCqiLte);
                    MainActivity.rsrp.setText("RSRP : " + cellRsrpLte);
                    MainActivity.rsrq.setText("RSRQ : " + cellRsrqLte);
                }
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
                        MainActivity.rsrp.setText("RSRP : CSI = " + csiRsrp + " ; SS = " + ssRsrp);
                        MainActivity.rsrq.setText("RSRQ : CSI = " + csiRsrq + " ; SS = " + ssRsrq);
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

    /*
    public void getMobility() {
        myAccelerometerListener = new SensorEventListener() {
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
            };
            mySensorManager.registerListener(myAccelerometerListener, myAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        myGyroscopeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event != null) {
                    // This time step's delta rotation to be multiplied by the current rotation
                    // after computing it from the gyro sample data.
                    if (timestamp != 0) {
                        final float dT = (event.timestamp - timestamp) * NS2S;
                        // Axis of the rotation sample, not normalized yet.
                        float axisX = event.values[0];
                        float axisY = event.values[1];
                        float axisZ = event.values[2];

                        // Calculate the angular speed of the sample
                        float omegaMagnitude = (float) sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                        // Normalize the rotation vector if it's big enough to get the axis
                        if (omegaMagnitude > EPSILON) {
                            axisX /= omegaMagnitude;
                            axisY /= omegaMagnitude;
                            axisZ /= omegaMagnitude;
                        }

                        // Integrate around this axis with the angular speed by the time step
                        // in order to get a delta rotation from this sample over the time step
                        // We will convert this axis-angle representation of the delta rotation
                        // into a quaternion before turning it into the rotation matrix.
                        float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                        float sinThetaOverTwo = (float) sin(thetaOverTwo);
                        float cosThetaOverTwo = (float) cos(thetaOverTwo);
                        deltaRotationVector[0] = sinThetaOverTwo * axisX;
                        deltaRotationVector[1] = sinThetaOverTwo * axisY;
                        deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                        deltaRotationVector[3] = cosThetaOverTwo;
                    }
                    timestamp = event.timestamp;
                    float[] deltaRotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
                    // User code should concatenate the delta rotation we computed with the current
                    // rotation in order to get the updated rotation.
                    //rotationCurrent = rotationCurrent * deltaRotationMatrix;
                    MainActivity.gyros_x.setText((int) deltaRotationMatrix[0]);

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    MainActivity.gyros_x.setText("X : " + (int) x + " rad/s");
                    MainActivity.gyros_y.setText("Y : " + (int) y + " rad/s");
                    MainActivity.gyros_z.setText("Z : " + (int) z + " rad/s");

                    MainActivity.gyros_x.setText(String.format("%.2f", event.values[0]));
                    MainActivity.gyros_y.setText(String.format("%.2f", event.values[1]));
                    MainActivity.gyros_z.setText(String.format("%.2f", event.values[2]));


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mySensorManager.registerListener(myGyroscopeListener, myGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

    }
    */

    /*
    @SuppressLint("MissingPermission")
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
                        if (location != null) {
                            MainActivity.latitude.setText("Latitude : " + location.getLatitude() + " °N");
                            MainActivity.longitude.setText("Longitude : " + location.getLongitude() + " °E");
                            MainActivity.altitude.setText("Altitude : " + (int) (Math.round(location.getAltitude() * 100)) / 100.0 + " meters");
                        }
                        else {
                            MainActivity.Location.setText("Location : no last known location found");
                        }
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

     */
}