package com.example.feature4;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

//import static com.example.feature4.MainActivity.textView;
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
                    Intent intent1 = new Intent(context,MainActivity2.class);
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                    break;
                default:
                    Log.d(TAG, "Error");
            }
        }
    }
}

public class MyService extends Service {
     public static void start()
     {

     }

    @Override
    public void onCreate() {
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        HeadsetIntentReceiver receiver = new HeadsetIntentReceiver();
        registerReceiver( receiver, receiverFilter );
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

}
