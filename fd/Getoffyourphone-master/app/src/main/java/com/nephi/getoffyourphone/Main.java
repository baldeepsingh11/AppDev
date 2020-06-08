package com.nephi.getoffyourphone;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.danimahardhika.cafebar.CafeBar;
import com.danimahardhika.cafebar.CafeBarTheme;
import com.facebook.stetho.Stetho;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;
import com.scottyab.rootbeer.RootBeer;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;


public class Main extends AppCompatActivity {

    static Context appContext;
    //------------Google related------------
    public String h_value = "";
    public String c_value = "";
    long millisNow;
    //------------Variables------------
    //Drawer Tabs
    private Drawer result = null;
    AccountHeader headerResult;
    SecondaryDrawerItem item1;
    SecondaryDrawerItem item2;
    SecondaryDrawerItem item3;
    SecondaryDrawerItem item4;
    SectionDrawerItem section1;
    SectionDrawerItem section2;
    GifDrawable gifFromResource;
    //Database
    DB_Helper db;
    //intents
    Intent lockIntent;
    //Array lists for Apps
    ArrayList<Integer> preselectedApps = new ArrayList<>();
    ArrayList<MultiSelectModel> listOfApps = new ArrayList<>();
    List<String> LS;
    //Multi-Choice-Selector
    MultiSelectDialog multiSelectDialog;
    //strings
    String package_name;
    //RootBeer Root Checker
    RootBeer rootbeer;
    Button Lock;
    TextView title_timer;
    Intent serviceIntent;
    //------------Service related------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DefaultSettings.getTheme(this)) {
            //Change App Theme
            setTheme(R.style.AppTheme_Light);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this , Timer_Service.class);
        startService(serviceIntent);
        init();

    }

    //All declarations and variable initializations
    public void init() {

        //------------Variables & Declarations------------
        //String
        package_name = getPackageName();

        //DataBase Handler
        db = new DB_Helper(this);
        appContext = getApplicationContext();
        //Locked_intent
        lockIntent = new Intent(this, locked.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        //Drawer Tabs
        //if you want to update the items at a later time it is recommended to keep it in a variable
        item1 = new SecondaryDrawerItem().withIdentifier(1).withName(getString(R.string.drawer_item1_text)).withDescription(getString(R.string.drawer_item1_text2)).withIcon(GoogleMaterial.Icon.gmd_star).withSelectable(false);
        item2 = new SecondaryDrawerItem().withIdentifier(2).withName(getString(R.string.drawer_item6_text)).withDescription(getString(R.string.drawer_item6_text2)).withIcon(GoogleMaterial.Icon.gmd_save).withSelectable(false);


        try {
            gifFromResource = new GifDrawable(getResources(), R.drawable.tick_tick);
        } catch (IOException e) {
            e.printStackTrace();
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(gifFromResource)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withEmail(getString(R.string.drawer_header)).withIcon(R.color.black)
                )
                .build();


        Lock = findViewById(R.id.sendButton);

        new_drawer();
        permission_check();
        isIgnoringBattery();
        first_Boot_check();
        try {
            LS = getInstalledComponentList();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        selection_all();
        PreSelect();
        //Main Title text change

        Stetho.initializeWithDefaults(this);

    }

    //New Drawer
    public void new_drawer() {
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
//                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        section1,
                        item1,
                        item2,
//                        new DividerDrawerItem(),
                        section2,
                        item3,
                        item4

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Drawer Tab Clicks
                        if (drawerItem.getIdentifier() == 1) {
                            //Select Apps to Lock
                            Drawer_App_Selector();
                        } else if (drawerItem.getIdentifier() == 2) {
                            //Open selected_apps activity
                            Intent intent = new Intent(Main.this, selected_apps.class);
                            startActivity(intent);
                        }
                        return false;
                    }
                })
                .build();
        //set the selection to the item with the identifier 1
        result.setSelection(-1);
    }

    //Drawer App Selector
    public void Drawer_App_Selector() {
//        Checking if the lock is running or not


            //Creating multi dialog
            multiSelectDialog = new MultiSelectDialog()
                    .title(R.string.app_selector_title) //setting title for dialog
                    .titleSize(20) //setting textSize
                    .positiveText(getString(R.string.app_selector_apply)) //setting Submit text
                    .negativeText(getString(R.string.app_selector_cancel)) //setting Cancel text
                    .clearText(getString(R.string.app_selector_clear))
                    .preSelectIDsList(preselectedApps) //List of ids that you need to be selected
                    .multiSelectList(listOfApps) // the multi select model list with ids and name
                    .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                        @Override
                        public void onDismiss(ArrayList<Integer> ids, String dataString) {
                            //Clear previous selections from DB and add the new ones
                            db.deleteAll();
                            if (ids.size() >= 1) {
                                //set_Selected means there is at least 1 item selected, set to true
                                db.set_Selected(1);
                            } else if (ids.size() < 1) {
                                //set to false
                                db.set_Selected(0);
                            }
                            //Adding IDs of selections and the respective PKG name
                            for (int i = 0; i < ids.size(); i++) {
                                db.add_apps(new apps(LS.get(ids.get(i) - 1)
                                        , multiSelectDialog.Get_ID(listOfApps, ids.get(i) - 1)));
                                //Toast.makeText(Main.this,"Selected Ids : " + ids.get(i),Toast.LENGTH_SHORT).show();
                            }

                            //Showing result in a cafeBar
                            CafeBar.builder(Main.this)
                                    .duration(CafeBar.Duration.SHORT)
                                    .content(getString(R.string.selected_apps) + ids.size())
                                    .maxLines(4)
                                    .theme(CafeBarTheme.Custom(Color.parseColor("#1976D2")))
                                    .show();
                        }

                        //onCancel do nothing
                        @Override
                        public void onCancel() {
                            Log.e("onCancel", "Dialog Dismissed without selection");
                        }

                        //I added this button to your library, this clears all
                        //selections, tho, it needs an app restart to see it visually
                        public void onClear() {
                            db.deleteAll();
                            db.set_Selected(0);
                            preselectedApps = new ArrayList<>();
                            CafeBar.builder(Main.this)
                                    .duration(CafeBar.Duration.SHORT)
                                    .content(getString(R.string.cafebar_error4))
                                    .maxLines(4)
                                    .theme(CafeBarTheme.Custom(Color.parseColor("#1976D2")))
                                    .show();
                            Log.e("onClear", "Cleared Selections");
                        }
                    });
            multiSelectDialog.show(getSupportFragmentManager(), "multiSelectDialog");

    }

    //Initialize all values on First Boot ever
    public void first_Boot_check() {
        //First launch and update check
        if (db.getFirstBootCount() == 0) {
            db.set_AllTimerData("", "N", 1, "", 0, "");
            db.set_defaultStateTable(0, 0, "None", 0);
            db.set_FirstBoot("N");
            db.set_defaultUsage("XXX");
        }
    }

    //Show dialog if usage access permission not given
    public void permission_check() {
        //Usage Permission
        if (!isAccessGranted()) {
            new LovelyStandardDialog(Main.this)
                    .setTopColorRes(R.color.blue)
                    .setIcon(R.drawable.ic_perm_device_information_white_48dp)
                    .setTitle(getString(R.string.permission_check_title))
                    .setMessage(getString(R.string.permission_check_message))
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    //Timer related values selected (Swipe Selectors)
    public void selection_all() {


        if (!db.get_LockTime(1).isEmpty() && !db.get_LockTime(1).equals("0")) {

        }

        if (db.get_StateTable(1) > 0) {


        }


        Lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
    }




    //Control DND Mode
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void dnd_toggle() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (DefaultSettings.getCb2(this)) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
        }

    }

    //Check if app usage access is granted
    public boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            assert appOpsManager != null;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //Check if battery permissions given
    public void isIgnoringBattery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(package_name)) {
                new LovelyStandardDialog(Main.this)
                        .setTopColorRes(R.color.blue)
                        .setIcon(R.drawable.ic_perm_device_information_white_48dp)
                        .setTitle(getString(R.string.battery_dialog_title))
                        .setMessage(getString(R.string.battery_dialog_message))
                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                                //intent.setData(Uri.parse("package:" + package_name));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();

            }

        }
    }

    //Preselect the apps that are saved in DB when opening app selector
    public void PreSelect() {
        if ((int) db.getAppsCount() != 0) {
            int count = (int) db.getAppsCount();
            for (int i = 1; i <= count; ++i) {
                preselectedApps.add(db.get_app(i).getS_id());
            }
        }
    }

    //Get list of all apps, put List<String> if u uncomment return
    private List<String> getInstalledComponentList()
            throws PackageManager.NameNotFoundException {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ril = getPackageManager().queryIntentActivities(mainIntent, 0);
        List<String> componentList = new ArrayList<String>();
        String name;
        String pkg = "";
        int i = 0;
        for (ResolveInfo ri : ril) {
            if (ri.activityInfo != null) {
                Resources res = getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
                if (ri.activityInfo.labelRes != 0) {
                    name = res.getString(ri.activityInfo.labelRes);
                } else {
                    name = ri.activityInfo.applicationInfo.loadLabel(
                            getPackageManager()).toString();
                }
                pkg = ri.activityInfo.packageName;
                componentList.add(pkg);
                i++;
                listOfApps.add(new MultiSelectModel(i, name));
            }
        }
        return componentList;
    }

    //Everything related to Notifications
    public void notification_update() {
        Intent intent = new Intent(this, Main.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt(); // just use a counter in some util class...

        NotificationCompat.Builder builder;

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

        if (Integer.valueOf(db.get_Hours(1)) > 10) {
            builder = new NotificationCompat.Builder(this, "notification_1");
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT) //HIGH, MAX, FULL_SCREEN and setDefaults(Notification.DEFAULT_ALL) will make it a Heads Up Display Style
                    //.setDefaults(Notification.) // also requires VIBRATE permission
                    .setSmallIcon(R.mipmap.ic_launcher) // Required!
                    .setContentTitle(getString(R.string.notification_title1))
                    .setContentText(getString(R.string.time_chosen) + db.get_Hours(1) + getString(R.string.minutes) + ", " + getString(R.string.app_open1) + db.get_StateTitle(1))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.time_chosen) + db.get_Hours(1) + getString(R.string.minutes) + "\n" + getString(R.string.app_open2) + db.get_StateTitle(1)))
                    .setVibrate(new long[]{0, 500})
                    //.setAutoCancel(true)
                    .setContentIntent(pIntent);
            //.setOngoing(true)
            //.addAction(R.drawable.ic_clear_black_48dp, "Dismiss", dismissIntent);
            //.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent);
        } else {
            builder = new NotificationCompat.Builder(this, "notification_1");
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT) //HIGH, MAX, FULL_SCREEN and setDefaults(Notification.DEFAULT_ALL) will make it a Heads Up Display Style
                    //.setDefaults(Notification.) // also requires VIBRATE permission
                    .setSmallIcon(R.mipmap.ic_launcher) // Required!
                    .setContentTitle(getString(R.string.notification_title1))
                    .setContentText(getString(R.string.time_chosen) + db.get_Hours(1) + getString(R.string.Hours_v2) + ", " + getString(R.string.app_open1) + db.get_StateTitle(1))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.time_chosen) + db.get_Hours(1) + getString(R.string.Hours_v2) + "\n" + getString(R.string.app_open2) + db.get_StateTitle(1)))
                    .setVibrate(new long[]{0, 500})
                    //.setAutoCancel(true)
                    .setContentIntent(pIntent);
            //.setOngoing(true)
            //.addAction(R.drawable.ic_clear_black_48dp, "Dismiss", dismissIntent);
            //.addAction(R.drawable.ic_action_boom, "Action!", someOtherPendingIntent);
        }


        // Builds the notification and issues it.
        assert notificationManager != null;
        notificationManager.notify(313, builder.build());
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Saved
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}