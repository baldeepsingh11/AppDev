package com.example.smartify;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class autoRotate extends ListActivity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    static ArrayList<String> selectedappsstring = new ArrayList<>();
    Button start_stop;
    AlertDialog alert;
    boolean started = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_rotate);

        packageManager = getPackageManager();

        new LoadApplications().execute();


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
 Log.i("msg","ff");
        ApplicationInfo app = applist.get(position);
        try {
            Intent intent = packageManager
                    .getLaunchIntentForPackage(app.packageName);

            if (null != intent) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(autoRotate.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(autoRotate.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static class ScreenOrientationEnforcer {

        private final View view;
        private final WindowManager windows;

        public ScreenOrientationEnforcer(Context context) {
            windows = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            view = new View(context);
        }

        public void start() {
            WindowManager.LayoutParams layout = generateLayout();
            windows.addView(view, layout);
            view.setVisibility(View.VISIBLE);
        }

        public void stop() {
            windows.removeView(view);
        }

        private WindowManager.LayoutParams generateLayout() {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            //So we don't need a permission or activity
            //Note that this won't work on some devices running MIUI
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            //Just in case the window type somehow doesn't enforce this
            layoutParams.flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

            //Prevents breaking apps that detect overlying windows enabling
            //(eg UBank app, or enabling an accessibility service)
            layoutParams.width = 0;
            layoutParams.height = 0;

            //Try to make it completely invisible
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.alpha = 0f;

            //The orientation to force
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;

            return layoutParams;
        }

    }

    public static class ScreenOrientationEnforcer1 {

        private final View view ;
        private final WindowManager windows;

        public ScreenOrientationEnforcer1(Context context) {
            windows = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            view = new View(context);
        }

        public void start() {
            WindowManager.LayoutParams layout = generateLayout();
            windows.addView(view, layout);
            view.setVisibility(View.VISIBLE);
        }

        public void stop() {
            windows.removeView(view);
        }

        private WindowManager.LayoutParams generateLayout() {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            //So we don't need a permission or activity
            //Note that this won't work on some devices running MIUI
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            //Just in case the window type somehow doesn't enforce this
            layoutParams.flags =
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

            //Prevents breaking apps that detect overlying windows enabling
            //(eg UBank app, or enabling an accessibility service)
            layoutParams.width = 0;
            layoutParams.height = 0;

            //Try to make it completely invisible
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.alpha = 0f;

            //The orientation to force
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER;

            return layoutParams;
        }

    }




    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                    selectedappsstring.add(info.packageName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for( String  strDay : selectedappsstring ){
            Log.i("msg", strDay);
        }



        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(autoRotate.this,
                    R.layout.row, applist);

            return null;
        }




        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(autoRotate.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}