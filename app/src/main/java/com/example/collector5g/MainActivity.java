package com.example.collector5g;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        TextView networkType = findViewById(R.id.networkType);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String network = intent.getStringExtra("NETWORKTYPE");
                        String cqi = intent.getStringExtra("CQI");
                        String rsrp = intent.getStringExtra("RSRP");
                        String rsrq = intent.getStringExtra("RSRQ");

                        Log.i("HOME","CQI :" + cqi);
                        Log.i("HOME","RSRP :" + rsrp);
                        Log.i("HOME","RSRQ :" + rsrq);

                        networkType.setText(network);
                    }
                }, new IntentFilter(DataCollectionService.ACTION_NETWORK_TYPE_BROADCAST)
        );

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                //serviceIntent.setAction("start collecting service");
                //startService(serviceIntent);

                Intent dataIntent = new Intent(getApplicationContext(), DataCollectionService.class);
                dataIntent.setAction("start 5g service");
                startService(dataIntent);

                //Intent locationIntent = new Intent(getApplicationContext(), CollectingService.class);
                //locationIntent.setAction("start location service");
                //startService(locationIntent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                //serviceIntent.setAction("stop collecting service");
                //stopService(serviceIntent);

                Intent dataIntent = new Intent(getApplicationContext(), DataCollectionService.class);
                dataIntent.setAction("stop 5g service");
                stopService(dataIntent);

                //Intent locationIntent = new Intent(getApplicationContext(), CollectingService.class);
                //locationIntent.setAction("stop location service");
                //stopService(locationIntent);
            }
        });


        /*Switch switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // start our service
                Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                serviceIntent.setAction("start collecting service");
                startService(serviceIntent);

                Intent locationIntent = new Intent(getApplicationContext(), CollectingService.class);
                locationIntent.setAction("start location service");
                startService(locationIntent);
            }
        }) ;*/
    }
}