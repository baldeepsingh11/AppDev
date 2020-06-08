package com.nephi.getoffyourphone;

/**
 * Created by xerxes on 02.12.17.
 */

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TreeMap;


public class Timer_Service extends Service {

    public static final long NOTIFY_INTERVAL = 1000;
    public static String str_receiver = "com.nephi.getoffyourphone.receiver";




    DB_Helper db;
    Intent intent;
    Intent lockIntent;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Lock Screen launch


        db = new DB_Helper(this);
    }




    @Override
    public void onTaskRemoved(Intent rootIntent) {


    }

 /*
    public void notification_update() {
//        Intent intent = new Intent(this, Main.class);
//        // use System.currentTimeMillis() to have a unique ID for the pending intent
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt(); // just use a counter in some util class...

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("notification_1", "Timer_Notification", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 500,});
            notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification_1");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT) //HIGH, MAX, FULL_SCREEN and setDefaults(Notification.DEFAULT_ALL) will make it a Heads Up Display Style
                //.setDefaults(Notification.) // also requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher) // Required!
                .setContentTitle(getString(R.string.notification_title2))
                .setContentText(getString(R.string.notification_message))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_message)))
                .setVibrate(new long[]{0, 500});
        //.setAutoCancel(true);
        //.setOngoing(true)
        //.addAction(R.drawable.ic_clear_black_48dp, "Dismiss", dismissIntent);
        //.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent);

        // Builds the notification and issues it.
        assert notificationManager != null;
        notificationManager.notify(313, builder.build());
    }

  private void LockApps() {
        if ((int) db.getAppsCount() != 0) {
            int count = (int) db.getAppsCount();
            for (int i = 1; i <= count; ++i) {
                if (printForegroundTask().equalsIgnoreCase(db.get_app_PKG(i))) {
                   Log.i("msg","yes");
                }
            }
        }
    }*/






    private  final Handler toastHandler = new Handler()
    {
        @SuppressLint({"WrongConstant", "HandlerLeak"})
        @Override
        public void handleMessage(Message msg) {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
            if (Build.VERSION.SDK_INT <= 20) {
                if (task.size() > 0) {
                    ComponentName componentInfo = task.get(0).topActivity;
                    // for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                    //    if (componentInfo.getPackageName().equals(pakageName.get(i))) {
                    //       currentApp = pakageName.get(i);
                    //   return true;
                    Log.i("msg",componentInfo.getPackageName());
                    // MainActivity.fun();
                }
            } else {
                String mpackageName = manager.getRunningAppProcesses().get(0).processName;
                UsageStatsManager usage = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {
                        runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (runningTask.isEmpty()) {
                        Log.d("TAG", "isEmpty Yes");
                        mpackageName = "";
                    } else {
                        mpackageName = runningTask.get(runningTask.lastKey()).getPackageName();
                        Log.d("TAG", "isEmpty No : " + mpackageName);
                        //MainActivity.fun();
                    }
                }


            }
        }};

    @Override
    public void onDestroy() {

        super.onDestroy();

    }




}