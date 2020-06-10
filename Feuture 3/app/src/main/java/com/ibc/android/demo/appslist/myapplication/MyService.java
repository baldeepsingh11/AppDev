package com.ibc.android.demo.appslist.myapplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MyService extends Service
{
    private static Timer timer = new Timer();
    public Boolean userAuth = false;
    private Context ctx;
    public String pActivity="";

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();



    }



    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 500);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
           handleMessage();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }


        public void handleMessage() {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(5);
            if (Build.VERSION.SDK_INT <= 20) {
                if (task.size() > 0) {
                    ComponentName componentInfo = task.get(0).topActivity;
                    // for (int i = 0; pakageName != null && i < pakageName.size(); i++) {
                    //    if (componentInfo.getPackageName().equals(pakageName.get(i))) {
                    //       currentApp = pakageName.get(i);
                    //     return true;
                  Log.i("msg",componentInfo.getPackageName());
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
                    }
                }


            }
        }}