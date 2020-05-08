package com.example.detectiingflip;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static float z_value;

    SensorManager sensorManager;
    Sensor accelerometerSensor;
    static boolean accelerometerPresent;
     static NotificationManager mNotificationManager;
     static TextView face;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(),MyService.class));
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

        face = (TextView)findViewById(R.id.face);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
            accelerometerPresent = true;
            accelerometerSensor = sensorList.get(0);
        }
        else{
            accelerometerPresent = false;
            face.setText("No accelerometer present!");
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(accelerometerPresent){
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

    {
        accelerometerListener = new SensorEventListener() {

            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSensorChanged(SensorEvent arg0) {
                // TODO Auto-generated method stub
                z_value = arg0.values[2];

                if (z_value >= -9.6) {
                    if (mNotificationManager.getCurrentInterruptionFilter() == 3) {
                        face.setText("Face UP");
                        Log.i("Z value", Float.toString(z_value));
                        //if(mNotificationManager.isNotificationPolicyAccessGranted()) {
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        //  }
                    }
                } else {

                    if (mNotificationManager.getCurrentInterruptionFilter() == 1) {
                        face.setText("Face DOWN");
                        Log.i("Z value", Float.toString(z_value));
                        new CountDownTimer(1500, 1000) {
                            public void onTick(long milliSecondsUntilDone) {
                                Log.i("Time left", Long.toString(milliSecondsUntilDone / 1000));
                            }

                            public void onFinish() {
                                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                                Log.i("finished", "finished");

                            }
                        }.start();
                    }

                }
            }
        };
    }

}