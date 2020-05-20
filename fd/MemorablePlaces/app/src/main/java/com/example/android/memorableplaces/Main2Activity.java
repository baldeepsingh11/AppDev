package com.example.android.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {
    WifiManager wifiManager;
    private GoogleMap mMap;
    LocationManager locmang;
    //LocationListener loclist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);




    }

    LocationListener loclist = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Intent intent = getIntent();
            final Location plae = new Location(LocationManager.GPS_PROVIDER);
            plae.setLatitude(MainActivity.array1.get(intent.getIntExtra("place",0)).latitude);
            plae.setLongitude(MainActivity.array1.get(intent.getIntExtra("place",0)).longitude);
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            ToggleButton toggle = (ToggleButton) findViewById(R.id.wifi);

            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < MainActivity.array1.size(); i++) {
                            if(location.getLatitude()< MainActivity.array1.get(i).latitude +0.5 && location.getLatitude()>MainActivity.array1.get(i).latitude - 0.5){
                                if(location.getLongitude()<MainActivity.array1.get(i).longitude +0.5 && location.getLongitude()>MainActivity.array1.get(i).longitude -0.5){
                                    wifiManager.setWifiEnabled(true);
                                }
                            }
                        }
                    } else {
                        wifiManager.setWifiEnabled(false);
                    }
                }
            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
