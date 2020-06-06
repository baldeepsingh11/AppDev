package com.example.feature4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

//import static com.example.feature4.MainActivity.textView;


public class MyService extends Service {
    class HeadsetIntentReceiver extends BroadcastReceiver {
        private String TAG = "HeadSet";

        public HeadsetIntentReceiver() {
            Log.d(TAG, "Created");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch(state) {
                    case(0):
                        Log.d(TAG, "Headset unplugged");
                        break;
                    case(1):
                        Log.d(TAG, "Headset plugged");
                        //  Intent intent1 = new Intent(context,MainActivity2.class);
                        Intent intent1 = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (intent1 != null) {
                            MyService.this.startActivity(intent1);//null pointer check in case package name was not found
                        }

                        // context.startActivity(intent1);
                        break;
                    default:
                        Log.d(TAG, "Error");
                }
            }
        }


    }




     public static void start()
     {

     }

    @Override
    public void onCreate() {
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetIntentReceiver receiver = new HeadsetIntentReceiver();
        registerReceiver( receiver, receiverFilter );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();

        else
            startForeground(1, new Notification());
    }

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
           //     .setSmallIcon(R.drawable.ic_brightness_3_black)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

}
