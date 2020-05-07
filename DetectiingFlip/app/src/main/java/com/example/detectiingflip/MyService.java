package com.example.detectiingflip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
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

            onTaskRemoved(intent);
            if(MainActivity.z_value>=-9.6) {
                if (face.getText().toString().equals("Face DOWN")) {
                    face.setText("Face UP");

                    //Toast.makeText(getApplicationContext(), "Face is up" + MainActivity.z_value, Toast.LENGTH_SHORT).show();
                    //if(mNotificationManager.isNotificationPolicyAccessGranted()) {
                    //mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                    //Log.i("value of g",Float.toString(z_value));
                    //}
                }

            }
            else {
                if (face.getText().toString().equals("Face UP")) {
                    face.setText("Face DOWN");

                    //Toast.makeText(this, "Face Down"+MainActivity.z_value, Toast.LENGTH_SHORT).show();
                    new CountDownTimer(10000, 1000) {
                        public void onTick(long milliSecondsUntilDone) {
                            Log.i("Time left", Long.toString(milliSecondsUntilDone / 1000));
                        }

                        public void onFinish() {
                            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                            Log.i("finished", "finished");

                        }
                    }.start();
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                }
            }
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
            super.onTaskRemoved(rootIntent);
        }
    }


