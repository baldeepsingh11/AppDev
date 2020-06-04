package com.example.smartify;

import  androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.smartify.ExampleService.dndList;
import static com.example.smartify.ExampleService.latitudeList;
import static com.example.smartify.ExampleService.locationListener;
import static com.example.smartify.ExampleService.locationManager;
import static com.example.smartify.ExampleService.longitudeList;
import static com.example.smartify.ExampleService.radiusList;
import static com.example.smartify.ExampleService.wifiList;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,GoogleMap.OnMapClickListener {

    public static GoogleMap mMap;
    List<Marker> markers = new ArrayList<Marker>();
    Circle circle;
    SeekBar seekBar;
    int flag=0;
    public FloatingActionButton fabDelete ;
    public FloatingActionButton fabDnd ;
    public FloatingActionButton fabWifi;

    public static int current_Id=-1;
    //SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.smartify", Context.MODE_PRIVATE);
    public void setOnMapLocation(Location location,String s){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            ExampleService.mCurrLocationMarker=mMap.addMarker(new MarkerOptions().position(userLocation).title(s));
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
                if (circle!=null) circle.setRadius(progress);
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
        fabDelete = findViewById(R.id.fabOpen);
        fabDelete.setImageResource(R.drawable.ic_delete_forever_black_24dp);
        fabDelete.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabDnd = findViewById(R.id.fabDnd);
        fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
        fabDnd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabWifi = findViewById(R.id.fabWifi);
        fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
        fabWifi.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorWhite)));
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(current_Id);
                current_Id=-1;
            }
        });
        fabWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiList.get(current_Id)==0){
                    wifiList.set(current_Id,1);
                    fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
                }
                else if(wifiList.get(current_Id)==1){
                    wifiList.set(current_Id,0);
                    fabWifi.setImageResource(R.drawable.ic_signal_wifi_off_black_24dp);
                }
            }
        });
        fabDnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dndList.get(current_Id)==0){
                    dndList.set(current_Id,1);
                    fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
                }
                else if(dndList.get(current_Id)==1){
                    dndList.set(current_Id,0);
                    fabDnd.setImageResource(R.drawable.ic_do_not_disturb_off_black_24dp);
                }
            }
        });
    }

    private void delete(int id) {
        markers.get(id).remove();
        circle.remove();
        markers.remove(id);
        dndList.remove(id);
        radiusList.remove(id);
        wifiList.remove(id);
        latitudeList.remove(id);
        longitudeList.remove(id);
        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.settings);
        if (flag==1) {
            linearLayout.animate().translationYBy(505).setDuration(100);
            flag=0;}
    }

    private void addMarker(int i) {
        markers.add(mMap.addMarker(new MarkerOptions().position(new LatLng(ExampleService.latitudeList.get(i),ExampleService.longitudeList.get(i)))
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))));
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
        int check=0;
        for (int i = 0; i <dndList.size()-1 ; i++) {
            if (isIntersecting(latLng, new LatLng(latitudeList.get(i), longitudeList.get(i)), 60)) {
                createRedCircle( new LatLng(latitudeList.get(i), longitudeList.get(i)),radiusList.get(i));
                createRedCircle(new LatLng(latitudeList.get(dndList.size()-1),longitudeList.get(dndList.size()-1)),30);
                Toast.makeText(this, "Intersecting", Toast.LENGTH_SHORT).show();
                check=1;
            }
        }
        if (check==0)  createCircle(latLng,30);
        addMarker(markers.size());
        Log.i("as",Integer.toString(dndList.get(dndList.size()-1)));
        Log.i("asd",Integer.toString(markers.size()));
        Log.i("asdf",Integer.toString(radiusList.size()));


       // Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
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

    private void createRedCircle(LatLng latLng, float radius) {
        Circle circle1 = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(getColor(R.color.red))
                .strokeWidth(5)
                .fillColor(getColor(R.color.redTransparent)));
    }

    private boolean isIntersecting(LatLng origin1, LatLng origin2, double distance) {
        Location Lorigin = new Location("");
        Lorigin.setLongitude(origin1.longitude);
        Lorigin.setLatitude(origin1.latitude);
        Location Lpoint = new Location("");
        Lpoint.setLongitude(origin2.longitude);
        Lpoint.setLatitude(origin2.latitude);
        if(Lorigin.distanceTo(Lpoint)<distance)
            return true;
        else
            return false;

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
        Log.i("dnd", String.valueOf(dndList.get(current_Id)));
        Log.i("wifi", String.valueOf(wifiList.get(current_Id)));
        if (current_Id!=-1){
            if(dndList.get(current_Id)==1) fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
            else if(dndList.get(current_Id)==0) fabDnd.setImageResource(R.drawable.ic_do_not_disturb_off_black_24dp);
            if(wifiList.get(current_Id)==1) fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
            if(wifiList.get(current_Id)==0) fabWifi.setImageResource(R.drawable.ic_signal_wifi_off_black_24dp);
        }
        if(current_Id!=-1) seekBar.setProgress(radiusList.get(current_Id));
        if(circle!=null){circle.remove();}
        if(current_Id!=-1) createCircle(marker.getPosition(),radiusList.get(current_Id));
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
