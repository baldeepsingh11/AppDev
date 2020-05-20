package com.example.android.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
     static ListView list;
    static ArrayList<String> array;
    static  ArrayList<LatLng> array1;
    static ArrayAdapter arrayadap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listview);
       array = new ArrayList<String>();
        array1 = new ArrayList<LatLng>();
        array.add("Add Places..");
        array1.add(new LatLng(0,0));
       arrayadap = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
        list.setAdapter(arrayadap);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
  if(position == 0){
      Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
      intent.putExtra("place", position);

      startActivity(intent);
  }else {
      Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
      intent.putExtra("place", position);

      startActivity(intent);
  }

            }
        });
    }
}

