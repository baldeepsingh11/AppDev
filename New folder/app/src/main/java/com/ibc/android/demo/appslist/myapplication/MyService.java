package com.ibc.android.demo.appslist.myapplication;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class MyService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
     //   new CheckRunningActivity().start();
    }

    TextView textView;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isAppRunning(this, "com.example.smartify")) {
           Log.i("msg","App running");
        } else {
            Log.i("msg","App not running");
        }
        return START_STICKY;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                Log.i("packageName",processInfo.processName);
                if (processInfo.processName.equals(packageName)) {
                   // Log.i("packageRunning",packageName);
                    return true;
                }
            }
        }
        return false;
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
