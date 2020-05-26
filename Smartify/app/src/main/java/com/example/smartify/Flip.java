package com.example.smartify;

import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Toast;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class Flip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(true)
                .enableDotAnimation(true)
                .setInfoText("Click here to enable DND when phone is flipped")
                .setShape(ShapeType.CIRCLE)
                .setTarget(fab)
                .setUsageId("intro_fab") //THIS SHOULD BE UNIQUE ID
                .setMaskColor(ContextCompat.getColor(Flip.this,R.color.whiteTransparent))
                .show();
        fab.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(Flip.this, R.color.colorPrimary)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                if(ExampleService.flip==true)
                {
                    ExampleService.flip=false;
                    Toast.makeText(Flip.this, "Switched off", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ExampleService.flip=true;
                    Toast.makeText(Flip.this, "Switched on", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
