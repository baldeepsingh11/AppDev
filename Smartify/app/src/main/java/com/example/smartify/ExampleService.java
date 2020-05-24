package com.example.smartify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.smartify.MainActivity.mNotificationManager;

public class ExampleService extends Service {
    float z= -20;
    float pValue ;
    boolean proximity=false;
    boolean accelerometer=false;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
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

                      //  Log.i("pValue", String.valueOf(event.values[0]));
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
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job

        }
    }





    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",16);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        Log.i("info","Service Started");


    }
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_brightness_3_black)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);



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
