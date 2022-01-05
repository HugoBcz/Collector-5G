package com.example.collector5g;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {

    public LocationManager mLocationManager;
    public Location newLocation;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, mLocationListener);
        super.onCreate();

    }

    private final LocationListener mLocationListener = new LocationListener() {

        @SuppressLint("MissingPermission")
        @Override
        public void onLocationChanged(@NonNull Location location) {

            newLocation = mLocationManager.getLastKnownLocation(mLocationManager.GPS_PROVIDER);
            Log.i("LATITUDE", "" + newLocation.getLatitude());
            Log.i("LONGITUDE", "" + newLocation.getLongitude());
            Log.i("ALTITUDE", "" + newLocation.getAltitude());

        }
    };


}