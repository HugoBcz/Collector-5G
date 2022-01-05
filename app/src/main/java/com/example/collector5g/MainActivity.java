package com.example.collector5g;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                serviceIntent.setAction("start collecting service");
                startService(serviceIntent);

                //Intent locationIntent = new Intent(getApplicationContext(), CollectingService.class);
                //locationIntent.setAction("start location service");
                //startService(locationIntent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), CollectingService.class);
                serviceIntent.setAction("stop collecting service");
                stopService(serviceIntent);

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