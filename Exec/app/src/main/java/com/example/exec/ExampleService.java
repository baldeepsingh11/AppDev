package com.example.exec;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

import static com.example.exec.App.CHANNEL_ID;
import static com.example.exec.MainActivity.face;
import static com.example.exec.MainActivity.mNotificationManager;

public class ExampleService extends Service {




    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent NotificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,
                0,NotificationIntent,0);
        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Flip")
                .setContentText("Flip your phone to enable DND")
                .setSmallIcon(R.drawable.ic_brightness_3_black)
                .setContentIntent(pendingIntent)
                .build();
        /*MainActivity.accelerometerListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {

                float z = event.values[2];
                Log.i("z value",String.valueOf(z));
                if(z>=-9.7){
                    if (mNotificationManager.getCurrentInterruptionFilter()==3) {
                        face.setText("Face UP");

                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    }
                }
                else {
                    if (mNotificationManager.getCurrentInterruptionFilter()==1) {
                        face.setText("Face DOWN");
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };*/
        startForeground(1,notification);
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
