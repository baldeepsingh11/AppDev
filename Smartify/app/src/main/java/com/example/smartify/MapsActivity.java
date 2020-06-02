package com.example.smartify;

import  androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.smartify.ExampleService.dndList;
import static com.example.smartify.ExampleService.latitudeList;
import static com.example.smartify.ExampleService.longitudeList;
import static com.example.smartify.ExampleService.radiusList;
import static com.example.smartify.ExampleService.wifiList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    List<Marker> markers = new ArrayList<Marker>();
    Circle circle;
    SeekBar seekBar;
    int flag=0;
    LatLng x;
    LocationManager locationManager;
    LocationListener locationListener;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    int current_Id;
    int wifiFlag,dndFlag;
    //SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.smartify", Context.MODE_PRIVATE);
    public void setOnMapLocation(Location location,String s){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mCurrLocationMarker=mMap.addMarker(new MarkerOptions().position(userLocation).title(s));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
        }
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.smartify", Context.MODE_PRIVATE);
        seekBar=(SeekBar) findViewById(R.id.seekBar4);
        //seekBar.setProgress(30);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circle.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusList.set(current_Id,seekBar.getProgress());
                Log.i("current id",Integer.toString(current_Id));
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final FloatingActionButton fabData = findViewById(R.id.fabOpen);
        fabData.setImageResource(R.drawable.ic_perm_data_setting_black_24dp);
        fabData.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        final FloatingActionButton fabDnd = findViewById(R.id.fabDnd);
        fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
        fabDnd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        final FloatingActionButton fabWifi = findViewById(R.id.fabWifi);
        fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
        fabWifi.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fabWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiFlag==0){
                    wifiFlag=1;
                    wifiList.set(current_Id,1);
                }
                if(wifiFlag==1){
                    wifiFlag=0;
                    wifiList.set(current_Id,0);
                }
            }
        });
        fabDnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dndFlag==0){
                    dndFlag=1;
                    dndList.set(current_Id,1);
                }
                if(dndFlag==1){
                    dndFlag=0;
                    dndList.set(current_Id,0);
                }
            }
        });
    }

    private void addMarker(int i) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(ExampleService.latitudeList.get(i),ExampleService.longitudeList.get(i)))
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        markers.add(marker);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        for (int i=0 ; i<ExampleService.latitudeList.size();i++){
            addMarker(i);
        }
        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                mLastLocation = location;
                if (mCurrLocationMarker != null)
                {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Your Location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locationListener);
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setOnMapLocation(lastKnownLocation,"Your Location");
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }




        // Add a marker in Sydney, Australia, and move the camera.
        /*LatLng chandigarh = new LatLng(30.7350626,76.6934887);
        mMap.addMarker(new MarkerOptions().position(chandigarh).title("Marker in Chandigarh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chandigarh,18));
        createCircle(chandigarh,30);*/


    }
    public void createCircle(LatLng latLng,float radius){
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(getColor(R.color.colorPrimary))
                .strokeWidth(5)
                .fillColor(getColor(R.color.blueTransparent)));
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        ExampleService.latitudeList.add(latLng.latitude);
        ExampleService.longitudeList.add(latLng.longitude);
        dndList.add(1);
        ExampleService.wifiList.add(1);
        ExampleService.radiusList.add(30);
        if (circle!=null){circle.remove();}
        createCircle(latLng,30);
        addMarker(markers.size());
        Log.i("as",Integer.toString(dndList.get(dndList.size()-1)));
        Log.i("asd",Integer.toString(markers.size()));
        Log.i("asdf",Integer.toString(radiusList.size()));


        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
        /*try{
            sharedPreferences.edit().putString("dndList",ObjectSerializer.serialize(dndList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("wifiList",ObjectSerializer.serialize(wifiList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(wifiList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("latitudeList",ObjectSerializer.serialize(latitudeList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("longitudeList",ObjectSerializer.serialize(longitudeList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            sharedPreferences.edit().putString("radiusList",ObjectSerializer.serialize(radiusList)).apply();
            Log.i("serialized",ObjectSerializer.serialize(dndList));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ArrayList<String> newFriends=new ArrayList<>();
        try {
            newFriends = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("friends", ObjectSerializer.serialize(new ArrayList<String>())));
            Log.i("newFriends",newFriends.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng pos=marker.getPosition();

        for (int i=0;i<markers.size();i++){
            if(latitudeList.get(i)==pos.latitude){
                current_Id=i;
                Log.i("current id", Integer.toString(i));
            }
        }
        seekBar.setProgress(radiusList.get(current_Id));
        if(circle!=null){circle.remove();}
        createCircle(marker.getPosition(),radiusList.get(current_Id));
        //Log.i("pos", "String.valueOf(pos.latitude)");
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.settings);
        if (flag==0) {
            linearLayout.animate().translationYBy(-505).setDuration(100);
            flag=1;
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.settings);
        if (flag==1) {
            linearLayout.animate().translationYBy(505).setDuration(100);
            flag=0;
        }
    }
}
