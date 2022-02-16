package com.example.collector5g;

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
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCollectionService extends Service {
    public DataCollectionService() {
    }

    private boolean isstarted;
    private MyPhoneStateListener myPhoneStateListener;
    TelephonyManager mtelephonyManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SensorManager mySensorManager;
    private Sensor myAccelerometer;
    private Sensor myGyroscope;
    private SensorEventListener myAccelerometerListener;
    //private SensorEventListener myGyroscopeListener;
    public static JSONObject json = new JSONObject();

    final Handler handler = new Handler();
    final Handler locHandler = new Handler();

    private Boolean bool = true;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);  // excludes gravity, good for motion detection
        myGyroscope = mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null) {

                    MainActivity.latitude.setText("Latitude : " + locationResult.getLastLocation().getLatitude() + " °N");
                    MainActivity.longitude.setText("Longitude : " + locationResult.getLastLocation().getLongitude() + " °E");
                    MainActivity.altitude.setText("Altitude : " + (int) (Math.round(locationResult.getLastLocation().getAltitude() * 100)) / 100.0 + " meters");
                }
            }
        };
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isstarted = true;
        Log.i("SERVICE", "START ...");

        MainActivity.accel_data.setText("Accelerometer data : in progress...");
        MainActivity.Location.setText("Location : in progress...");
        MainActivity.networkData.setText("Network data : in progress...");

        try {
            json.put("ID", StartActivity.id);
            getAllData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bool) {
                    Log.d("JSON", json.toString());
                    //StartActivity.pub(json);
                    handler.postDelayed(this, StartActivity.delay * 1000);
                }
            }
        }, StartActivity.delay * 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        bool = false;

        mySensorManager.unregisterListener(myAccelerometerListener);
        //mySensorManager.unregisterListener(myGyroscopeListener);
        mtelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);

        MainActivity.accel_data.setText("Accelerometer data : stopped");
        MainActivity.Location.setText("Location : stopped");
        MainActivity.networkData.setText("Network data : stopped");

        super.onDestroy();
    }

    public void getAllData() throws JSONException {

        // get battery level
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batteryPercentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        //Log.i("BATTERY LEVEL", "" + batteryPercentage + "%");
        MainActivity.battery.setText("BATTERY LEVEL: " + batteryPercentage + "%");

        json.put("BATTERY", batteryPercentage);

        // #########################################################################################

        // get location
        /*
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

                            try {
                                json.put("LATITUDE", location.getLatitude());
                                json.put("LONGITUDE", location.getLongitude());
                                json.put("ALTITUDE", (Math.round(location.getAltitude() * 100)) / 100.0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            MainActivity.Location.setText("Location : no last known location found");
                        }
                    }
                });
        */
        locHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isstarted) {
                    Log.d("JSON", json.toString());
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);



        // #########################################################################################

        // get mobility
        myAccelerometerListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event != null) {
                    long eventStamp = event.timestamp;
                    MainActivity.accel_x.setText(String.format("%.2f", event.values[0]));
                    MainActivity.accel_y.setText(String.format("%.2f", event.values[1]));
                    MainActivity.accel_z.setText(String.format("%.2f", event.values[2]));

                    try {
                        json.put("ACCELX", String.format("%.2f", event.values[0]));
                        json.put("ACCELY", String.format("%.2f", event.values[1]));
                        json.put("ACCELZ", String.format("%.2f", event.values[2]));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mySensorManager.registerListener(myAccelerometerListener, myAccelerometer, 500);

        // #########################################################################################

        // get network data

        myPhoneStateListener = new MyPhoneStateListener();
        mtelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mtelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
}

class MyPhoneStateListener extends PhoneStateListener {

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);

        //levelHw =5 ; primary=CellSignalStrengthNr/Lte
        // COMMENT : When Integer.valueOf is not used, it return -140 by default , NOT SURE

        String primaryCell = "";
        //int csiRsrp = 2147483647;
        //int csiRsrq = 2147483647;
        //int csiSinr = 2147483647;
        int ssRsrp = 2147483647;
        int ssRsrq = 2147483647;
        int ssSinr = 2147483647;

        String[] sigString = signalStrength.toString().split(",");
        String nrString = sigString[5];

        if (sigString[8].equals("primary=CellSignalStrengthLte}")){
            //Log.i("NETWORK", "You are connected to a 5G network");

            //Pattern pcsiRsrp = Pattern.compile("csiRsrp = ([^ ]*)");
            //Pattern pcsiRsrq = Pattern.compile("csiRsrq = ([^ ]*)");
            //Pattern pcsiSinr = Pattern.compile("csiSinr = ([^ ]*)");
            Pattern pssRsrp = Pattern.compile("ssRsrp = ([^ ]*)");
            Pattern pssRsrq = Pattern.compile("ssRsrq = ([^ ]*)");
            Pattern pssSinr = Pattern.compile("ssSinr = ([^ ]*)");

            //Matcher mcsiRsrp = pcsiRsrp.matcher(nrString);
            //Matcher mcsiRsrq = pcsiRsrq.matcher(nrString);
            //Matcher mcsiSinr = pcsiSinr.matcher(nrString);
            Matcher mssRsrp = pssRsrp.matcher(nrString);
            Matcher mssRsrq = pssRsrq.matcher(nrString);
            Matcher mssSinr = pssSinr.matcher(nrString);

            if (mssRsrp.find() && mssRsrq.find() && mssSinr.find()){

                //Log.i("NETWORK", "Input has been correctly parsed");

                //csiRsrp =Integer.valueOf(mcsiRsrp.group(1));
                //csiRsrq =Integer.valueOf(mcsiRsrq.group(1));
                //csiSinr =Integer.valueOf(mcsiSinr.group(1));
                ssRsrp =Integer.valueOf(mssRsrp.group(1));
                ssRsrq =Integer.valueOf(mssRsrq.group(1));
                ssSinr =Integer.valueOf(mssSinr.group(1));

                try {
                    //DataCollectionService.json.put("csiRsrp", csiRsrp);
                    //DataCollectionService.json.put("csiRsrq", csiRsrq);
                    //DataCollectionService.json.put("csiSinr", csiSinr);
                    DataCollectionService.json.put("ssRsrp", ssRsrp);
                    DataCollectionService.json.put("ssRsrq", ssRsrq);
                    DataCollectionService.json.put("ssSinr", ssSinr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MainActivity.networkType.setText("You are connected to a 5G network");
                MainActivity.rsrp.setText("SSRSRP : " + ssRsrp);
                MainActivity.rsrq.setText("SSRSRq : " + ssRsrq);
            }
        } else {
            Log.i("NETWORK", "You are not connected to a 5G network");
        }

    }
}