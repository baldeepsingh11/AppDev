package com.example.smartify;

import android.app.ActivityManager;
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
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.smartify.MainActivity.accelerometerSensor;
import static com.example.smartify.MainActivity.mNotificationManager;
import static com.example.smartify.MainActivity.proximitySensor;
import static com.example.smartify.MainActivity.sensorManager;


public class ExampleService extends Service {
    public static ArrayList<Integer> dndList= new ArrayList<Integer>();
    public static ArrayList<Double> latitudeList= new ArrayList<Double>();
    public static ArrayList<Double> longitudeList= new ArrayList<Double>();
    public static ArrayList<Integer> wifiList= new ArrayList<Integer>();
    public static ArrayList<Integer> radiusList= new ArrayList<Integer>();
    float z= -20;
    float pValue ;
    boolean proximity=true;
    boolean accelerometer=false;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    static boolean flip;
    int fFlag=0;
    static int flipSettings=2;
    int innerflag =0;
    WifiManager wifiManager;
    static SensorEventListener accelerometerListener;
    class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    // Use like this:


    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Log.i("flipStatus",String.valueOf(flip));

                accelerometerListener = new SensorEventListener() {

                    @Override
                    public void onSensorChanged(SensorEvent event) {
                      //  Log.i("info","sensorchanged");
                         if(flip) {
                            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                                if (event.values[2] <= -9.5) {
                                    accelerometer = true;
                                } else {
                                    accelerometer = false;
                                }
                              //   Log.v("z value", String.valueOf(event.values[2]));

                            }
                            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                                if (event.values[0] == 0.0) {
                                    proximity = true;
                                } else {
                                    proximity = false;
                                }

                                //  Log.i("pValue", String.valueOf(event.values[0]));
                            }


                            if (!accelerometer || !proximity) {
                          //      Log.i("status","faceup");
                               if (fFlag==1) {
                                    //    face.setText("Face UP");
                                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                                    fFlag=0;

                                }

                            } else if (accelerometer && proximity) {
                               if (fFlag==0) {
                                   fFlag=1;
                                    //    face.setText("Face DOWN");
                                   new CountDownTimer(2000, 100) {

                                       public void onTick(long millisUntilFinished) {

                                           innerflag = 0;
                                           Log.d("time",String.valueOf(millisUntilFinished));
                                           if(!accelerometer || !proximity)
                                           {
                                               innerflag = 1;
                                               fFlag=0;

                                           }

                                           //here you can have your logic to set text to edittext
                                       }

                                       public void onFinish() {
                                           Log.i("finish","true");
                                           if(innerflag==0) {
                                               mNotificationManager.setInterruptionFilter(flipSettings);
                                               Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                               // Vibrate for 500 milliseconds
                                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                   v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
                                               } else {
                                                   //deprecated in API 26
                                                   v.vibrate(400);
                                               }
                                               fFlag = 1;
                                           }
                                       }

                                   }.start();

                                }
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
        Log.i("info","Service Started");

        HandlerThread thread = new HandlerThread("ServiceStartArguments",16);
        Log.i("info","registered");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();

        else
            startForeground(1, new Notification());
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            boolean foregroud = new ForegroundCheckTask().execute(this).get();
            Log.d("foregoroundCheck", String.valueOf(foregroud));
        } catch (Exception e) {
            Log.i("foregrund check","error");
            e.printStackTrace();
        }

        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        super.onCreate();




    }
    @RequiresApi(api = Build.VERSION_CODES.O)
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
        if(latitudeList.size()>0)
        Log.i("abcd", String.valueOf(latitudeList.get(latitudeList.size()-1)));

        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(accelerometerListener);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
