package com.example.smartify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sensorManager;
    Sensor accelerometerSensor;
    Sensor proximitySensor;
    static boolean accelerometerPresent;
    static NotificationManager mNotificationManager;

    public void Flip(View view){
        Intent flipIntent=new Intent(MainActivity.this,Flip.class);
        startActivity(flipIntent);
    }
    public void location(View view){
        Intent locationIntent=new Intent(MainActivity.this,MapsActivity.class);
        startActivity(locationIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //show start activity
            startActivity(new Intent(MainActivity.this, OnBoarding.class));
            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();
        }


        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission")
                    .setIcon(android.R.drawable.ic_btn_speak_now)
                    .setMessage("Please grant DND permission")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

        }
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        }
        else{
            accelerometerPresent = false;
            Toast.makeText(this, "Oops! No accelerometer present", Toast.LENGTH_SHORT).show();
        }
        Intent serviceIntent = new Intent(this , ExampleService.class);
        startService(serviceIntent);
    }

    public void startService(View view){


    }
    public  void stopService(View view){
        Intent serviceIntent = new Intent(this , ExampleService.class);
        stopService(serviceIntent);
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(accelerometerPresent){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(accelerometerListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if(accelerometerPresent){
            sensorManager.unregisterListener(accelerometerListener);


        }
    }
    static SensorEventListener accelerometerListener;

    }

