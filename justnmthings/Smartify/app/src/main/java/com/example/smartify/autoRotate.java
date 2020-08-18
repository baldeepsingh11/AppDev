package com.example.smartify;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class autoRotate extends AppCompatActivity {
    private PackageManager packageManager = null;

    static ArrayList<String> selectedappsstring = new ArrayList<>();
    Button start_stop;
    AlertDialog alert;
    boolean started = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<AppInfo> installedApps;
    private FloatingActionButton shareButton;
    private AppsManager appManager;
    private final String baseURL = "http://192.168.50.48/post.php";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_rotate);

        if (android.os.Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {   //Android M Or Over
            Log.i("hbjbdjjx","vbdhbvhdxb");
            new AlertDialog.Builder(autoRotate.this)
                    .setTitle("Permission")
                    .setIcon(android.R.drawable.ic_btn_speak_now)
                    .setMessage("Please grant overlay permission")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(intent1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();

        }

        installedApps = new ArrayList<AppInfo>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        appManager = new AppsManager(this);
        installedApps = appManager.getApps();

        // Initialize a new adapter for RecyclerView
        mAdapter = new InstalledAppsAdapter(
                getApplicationContext(),
                installedApps
        );


        mRecyclerView.setAdapter(mAdapter);


    }

  /*  @Override
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
    }*/

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

  /*  private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new InstalledAppsAdapter(autoRotate.this,
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

    }  */
}