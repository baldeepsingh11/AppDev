package com.example.smartify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.smartify.MainActivity.mNotificationManager;

public class ExampleService extends Service {
    float z= -20;
    float pValue ;
    boolean proximity=false;
    boolean accelerometer=false;




    @Override
    public void onCreate() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent NotificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,
                0,NotificationIntent,0);
        Notification notification=new NotificationCompat.Builder(this,App.CHANNEL_ID)
                .setContentTitle("Flip")
                .setContentText("Flip your phone to enable DND")
                .setSmallIcon(R.drawable.ic_brightness_3_black)
                .setContentIntent(pendingIntent)
                .build();
        MainActivity.accelerometerListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {


                if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
                {
                    if(event.values[2]<=-9.5)
                    {
                        accelerometer = true;
                    }
                    else
                    {
                        accelerometer=false;
                    }
                    Log.i("z value", String.valueOf(event.values[2]));

                }
                if(event.sensor.getType()==Sensor.TYPE_PROXIMITY)
                {
                    if(event.values[0]==0.0)
                    {
                        proximity=true;
                    }
                    else
                    {
                        proximity=false;
                    }

                    Log.i("pValue", String.valueOf(event.values[0]));
                }


                if(!accelerometer||!proximity){
                    if (mNotificationManager.getCurrentInterruptionFilter()== NotificationManager.INTERRUPTION_FILTER_NONE) {
                        //    face.setText("Face UP");

                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    }
                }
                else if(accelerometer&&proximity) {
                    if (mNotificationManager.getCurrentInterruptionFilter()==NotificationManager.INTERRUPTION_FILTER_ALL) {
                        //    face.setText("Face DOWN");
                        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
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
