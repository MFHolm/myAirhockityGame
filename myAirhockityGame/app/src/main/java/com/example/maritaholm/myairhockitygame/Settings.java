package com.example.maritaholm.myairhockitygame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;


public class Settings extends Activity {


    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final RadioGroup pointsGroup  = (RadioGroup) findViewById(R.id.pointsGroup);
        final RadioGroup frictionGroup = (RadioGroup) findViewById(R.id.frictionGroup);
        int set = prefs.getInt("points",0);
        String friction = prefs.getString("friction", null);
        final MediaPlayer playSoundButtonTouch = MediaPlayer.create(getApplicationContext(), R.raw.menutouch);


        setButtons(pointsGroup,frictionGroup,set,friction);

        Button defButton = (Button) findViewById(R.id.default_button);
        Button retButton = (Button) findViewById(R.id.return_button);

        defButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
                pointsGroup.check(R.id.radio_three);
                frictionGroup.check(R.id.radio_some);
            }
        });

        pointsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.radio_three == checkedId) {
                    prefs.edit().putInt("points", 3).commit();
                } else if (checkedId == R.id.radio_five) {
                    prefs.edit().putInt("points", 5).commit();
                } else if (checkedId == R.id.radio_ten) {
                    prefs.edit().putInt("points", 10).commit();
                }
            }
        });

        frictionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.radio_none == checkedId){
                    prefs.edit().putString("friction", "none").commit();
                } else if (checkedId == R.id.radio_five){
                    prefs.edit().putString("friction","some").commit();
                } else if (checkedId == R.id.radio_ten){
                    prefs.edit().putString("friction","much").commit();
                }
            }
        });

        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playSoundButtonTouch.start();
                finish();
            }
        });



    }
    public void setButtons(RadioGroup pointsGroup,RadioGroup frictionGroup, int set,String friction){
        if(set == 3){
            pointsGroup.check(R.id.radio_three);
        } else if (set == 5){
            pointsGroup.check(R.id.radio_five);
        } else {
            pointsGroup.check(R.id.radio_ten);
        }

        if(friction.equals("none")){
            frictionGroup.check(R.id.radio_none);
        } else if (friction.equals("some")){
            frictionGroup.check(R.id.radio_some);
        } else {
            frictionGroup.check(R.id.radio_much);
        }
    }

}
