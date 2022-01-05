package com.example.collector5g;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class DataCollectionService extends Service {

    public static final String
            ACTION_NETWORK_TYPE_BROADCAST = DataCollectionService.class.getName() + "NetworkTypeBroadcast";

    //final Handler handler = new Handler();
    //final  int delay = 5000;

    public DataCollectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TelephonyManager teleMan = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String network = null;
        int cellSig = 0;
        int cellCqi = 0;
        int cellRsrp = 0;
        int cellRsrq = 0;

        @SuppressLint("MissingPermission")
        List<CellInfo> cellInfoList = teleMan.getAllCellInfo();

        try {
            for (CellInfo cellInfo : cellInfoList) {
                if (cellInfo instanceof CellInfoLte) {
                    Log.i("NETWORK TYPE", "OK");
                    network = "Réseau 4G";
                    cellSig = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                    cellCqi = ((CellInfoLte) cellInfo).getCellSignalStrength().getCqi();
                    cellRsrp = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrp();
                    cellRsrq = ((CellInfoLte) cellInfo).getCellSignalStrength().getRsrq();
                }
            }
        } catch (Exception e) {
            Log.d("NETWORK TYPE","++++++" + e);
        }

        //Log.i("NETWORK TYPE", network);

        Intent i = new Intent(ACTION_NETWORK_TYPE_BROADCAST);
        i.putExtra("NETWORKTYPE", network);
        i.putExtra("CQI", String.valueOf(cellCqi));
        i.putExtra("RSRP", String.valueOf(cellRsrp));
        i.putExtra("RSRQ", String.valueOf(cellRsrq));
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("MissingPermission")
                List<CellInfo> cellInfoList = teleMan.getAllCellInfo();
                String network = null;
                int cellSig = 0;

                try {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoLte) {
                            network = "Réseau 4G";
                            cellSig = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();
                            Log.i("NETWORK TYPE", String.valueOf(cellSig));
                        }
                    }
                } catch (Exception e) {
                    Log.d("NETWORK TYPE","++++++" + e);
                }

                //Log.i("NETWORK TYPE", network);

                Intent i = new Intent(ACTION_NETWORK_TYPE_BROADCAST);
                i.putExtra("NETWORKTYPE", network + " with Signal Strengh : " + String.valueOf(cellSig));
                LocalBroadcastManager.getInstance(DataCollectionService.this).sendBroadcast(i);
                handler.postDelayed(this, delay);
            }
        }, delay);*/

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}