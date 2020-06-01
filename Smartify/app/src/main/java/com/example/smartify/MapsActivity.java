package com.example.smartify;

import  androidx.annotation.NonNull;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    ArrayList<Integer> arrayList= new ArrayList<Integer>();

    Circle circle;
    SeekBar seekBar;
    int flag=0;
    LatLng x;
    LocationManager locationManager;
    LocationListener locationListener;
    public void setOnMapLocation(Location location,String s){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(s));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
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
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.smartify", Context.MODE_PRIVATE);

        seekBar=(SeekBar) findViewById(R.id.seekBar4);
        seekBar.setProgress(30);
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

            }
        });
        fabDnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        googleMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng chandigarh = new LatLng(30.7350626,76.6934887);
        mMap.addMarker(new MarkerOptions().position(chandigarh).title("Marker in Chandigarh"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chandigarh,18));
        createCircle(chandigarh,30);


    }
    public void createCircle(LatLng latLng,float radius){
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(getColor(R.color.colorPrimary))
                .strokeWidth(5)
                .fillColor(getColor(R.color.blueTransparent)));
        seekBar.setProgress(30);
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        String Address="";
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<android.location.Address> listAdress = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if (listAdress!=null&&listAdress.size()>0){
                Log.i("Place info",listAdress.get(0).toString());
            }
            if(listAdress!=null&&listAdress.size()>0){
                Address+=listAdress.get(0).getFeatureName()+" ";
            }
            if(listAdress!=null&&listAdress.size()>0){
                Address+=listAdress.get(0).getSubLocality()+" ";
            }
            if(listAdress!=null&&listAdress.size()>0){
                Address+=listAdress.get(0).getLocality();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(Address));
        Log.i("Address",Address);
        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
        circle.remove();
        createCircle(latLng,30);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng pos=marker.getPosition();
        circle.remove();
        //Log.i("pos", "String.valueOf(pos.latitude)");
        createCircle(pos,30);
        seekBar.setProgress(30);
        return false;
    }
}
