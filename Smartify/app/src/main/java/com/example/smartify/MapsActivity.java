package com.example.smartify;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final FloatingActionButton fabOpen = findViewById(R.id.fabOpen);
        fabOpen.setImageResource(R.drawable.ic_settings_black_24dp);
        fabOpen.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorPrimary)));
        final FloatingActionButton fabDnd = findViewById(R.id.fabDnd);
        fabDnd.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
        fabDnd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorPrimary)));
        final FloatingActionButton fabWifi = findViewById(R.id.fabWifi);
        fabWifi.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
        fabWifi.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MapsActivity.this, R.color.colorPrimary)));
        fabOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag==0) {
                    fabDnd.animate().translationYBy(-180).setDuration(500);
                    fabWifi.animate().translationYBy(-360).setDuration(500);
                    flag =1;
                }
                else {
                    fabDnd.animate().translationYBy(180).setDuration(500);
                    fabWifi.animate().translationYBy(360).setDuration(500);
                    flag=0;
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-33.8688, 151.20939 );
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        createCircle(sydney,3000);

    }
    public void createCircle(LatLng latLng,float radius){
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(getColor(R.color.redTransparent)));
    }
}
