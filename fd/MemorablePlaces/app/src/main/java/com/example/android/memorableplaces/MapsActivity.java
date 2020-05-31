package com.example.android.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {




    private GoogleMap mMap;
    LocationManager locmang;
    LocationListener loclist;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    locmang.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,loclist);
                }}}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        if(intent.getIntExtra("place",0) == 0){
        locmang = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener loclist = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng sydney = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in UserLocation"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                //   Toast.makeText(MapsActivity.this,location.toString(),Toast.LENGTH_SHORT).show();

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(listAddress !=null && listAddress.size() >0){
                        Log.i("Placeinfo",listAddress.get(0).toString());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }}

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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locmang.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,loclist);
            // Location lastloc = LocationManager.getLastKnowLocation(LocationManager.get)
        }
        Log.i("msg","456");
        // Add a marker in Sydney and move the camera
    }else{
             Location plae = new Location(LocationManager.GPS_PROVIDER);
             plae.setLatitude(MainActivity.array1.get(intent.getIntExtra("place",0)).latitude);
            plae.setLongitude(MainActivity.array1.get(intent.getIntExtra("place",0)).longitude);
            LatLng sydney = new LatLng(plae.getLatitude(),plae.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in UserLocation"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            Log.i("msg","123");
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {


        Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
        String ADRESSS ="";

        try{
            List<Address> listAdd = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);

        if(listAdd !=null && listAdd.size()>0){
            if(listAdd.get(0).getThoroughfare() != null){
                if(listAdd.get(0).getSubThoroughfare() !=null){
                    ADRESSS += listAdd.get(0).getSubThoroughfare();
                }
                ADRESSS += listAdd.get(0).getThoroughfare();
            }
        }

        }catch(Exception e){
            e.printStackTrace();
        }

        if(ADRESSS.equals("")){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            ADRESSS +=sdf.format(new Date());
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(ADRESSS));

        MainActivity.array.add(ADRESSS);
        MainActivity.array1.add(latLng);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.android.memorableplaces",Context.MODE_PRIVATE);
        try{
            sharedPreferences.edit().putString("places", com.example.memorableplaces.ObjectSerializer.serialize(MainActivity.array)).apply();

        }catch(Exception e){
            e.printStackTrace();
        }

        MainActivity.arrayadap.notifyDataSetChanged();
        Toast.makeText(this,"Location Saved",Toast.LENGTH_SHORT).show();
    }
}
