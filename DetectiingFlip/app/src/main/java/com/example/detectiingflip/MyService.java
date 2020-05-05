package com.example.detectiingflip;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import static com.example.detectiingflip.MainActivity.mNotificationManager;

public class MyService extends Service {
        public MyService() {
        }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId){
            onTaskRemoved(intent);
            if(MainActivity.z_value>=-9.81) {
                //Toast.makeText(getApplicationContext(), "Face is up" + MainActivity.z_value, Toast.LENGTH_SHORT).show();
                Log.i("msg", String.valueOf(MainActivity.z_value));
                //if(mNotificationManager.isNotificationPolicyAccessGranted()) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                //}

            }
            else
            {
                //Toast.makeText(this, "Face Down"+MainActivity.z_value, Toast.LENGTH_SHORT).show();
                Log.i("msg", String.valueOf(MainActivity.z_value));
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
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

