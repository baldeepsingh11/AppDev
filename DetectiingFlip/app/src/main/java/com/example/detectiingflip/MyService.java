package com.example.detectiingflip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import static com.example.detectiingflip.MainActivity.face;
import static com.example.detectiingflip.MainActivity.mNotificationManager;
import static com.example.detectiingflip.MainActivity.z_value;

public class MyService extends Service {
        public MyService() {
        }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId){
           MainActivity.accelerometerListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {

                  float z = event.values[2];
                    //Log.v("z value",String.valueOf(z));
                    if(z>=-9.7){
                        if (mNotificationManager.getCurrentInterruptionFilter()==NotificationManager.INTERRUPTION_FILTER_NONE) {
                            face.setText("Face UP");
                            Log.i("msg","face up");
                            // Log.i("Z value",Float.toString(z));

                            //Toast.makeText(getApplicationContext(), "Face is up" + MainActivity.z_value, Toast.LENGTH_SHORT).show();
                            //if(mNotificationManager.isNotificationPolicyAccessGranted()) {
                            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                          //  Log.i("value of g",Float.toString(z));
                            //}
                        }

                    }
                    else {
                        if (mNotificationManager.getCurrentInterruptionFilter()==NotificationManager.INTERRUPTION_FILTER_ALL) {
                            face.setText("Face DOWN");
                            Log.i("status", "face down");
                           // Log.i("Z value",Float.toString(z_value));

                            // Vibrate for 500 milliseconds

                            //Toast.makeText(this, "Face Down"+MainActivity.z_value, Toast.LENGTH_SHORT).show();
                         //   Log.i("finished", "finished");
                            /*new CountDownTimer(3000, 1000) {
                                public void onTick(long milliSecondsUntilDone) {
                                   // Log.i("Time left", Long.toString(milliSecondsUntilDone / 1000));
                                }

                                public void onFinish() {



                                }
                            }.start();*/

                                    // Do something after 5s = 5000ms
                                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        v.vibrate(1000);
                                    }



                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };




            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }
        @Override
        public void onTaskRemoved(Intent rootIntent) {
            Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
            restartServiceIntent.setPackage(getPackageName());
            startService(restartServiceIntent);
            Log.e("error ","task removed");
            super.onTaskRemoved(rootIntent);
        }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        Log.e("error","service destroyed");
        super.onDestroy();
    }
}


