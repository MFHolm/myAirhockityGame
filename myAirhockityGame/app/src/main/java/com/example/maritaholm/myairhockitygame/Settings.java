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
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final RadioGroup pointsGroup  = (RadioGroup) findViewById(R.id.points_group);
        final RadioGroup frictionGroup = (RadioGroup) findViewById(R.id.friction_group);
        final RadioGroup themeGroup = (RadioGroup) findViewById(R.id.theme_group);
        int set = prefs.getInt("points",0);
        String friction = prefs.getString("friction", null);
        String theme = prefs.getString("theme", null);
        final MediaPlayer playSoundButtonTouch = MediaPlayer.create(getApplicationContext(), R.raw.menutouch);

        setButtons(pointsGroup,frictionGroup,themeGroup,set,friction,theme);

        Button defButton = (Button) findViewById(R.id.default_button);
        Button retButton = (Button) findViewById(R.id.return_button);

        defButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
                pointsGroup.check(R.id.radio_three);
                frictionGroup.check(R.id.radio_some);
                themeGroup.check(R.id.radio_orange_blue);

                prefs.edit().putInt("player1", R.drawable.orange_player).apply();
                prefs.edit().putInt("player2", R.drawable.blue_player).apply();
                prefs.edit().putInt("puck", R.drawable.grey_puck).apply();
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
                } else if (checkedId == R.id.radio_some){
                    prefs.edit().putString("friction","some").commit();
                } else if (checkedId == R.id.radio_much){
                    prefs.edit().putString("friction","much").commit();
                }
            }
        });

        themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_orange_blue) {
                    prefs.edit().putString("theme", "orange and blue").apply();
                    prefs.edit().putInt("player1", R.drawable.orange_player).apply();
                    prefs.edit().putInt("player2", R.drawable.blue_player).apply();
                    prefs.edit().putInt("puck", R.drawable.grey_puck).apply();
                } else if (checkedId == R.id.radio_red_green) {
                    prefs.edit().putString("theme", "red and green").apply();
                    prefs.edit().putInt("player1", R.drawable.red_player).apply();
                    prefs.edit().putInt("player2", R.drawable.green_player).apply();
                    prefs.edit().putInt("puck", R.drawable.grey_puck).apply();
                } else if (checkedId == R.id.radio_yellow_purple){
                    prefs.edit().putString("theme", "yellow and orange").apply();
                    prefs.edit().putInt("player1", R.drawable.yellow_player).apply();
                    prefs.edit().putInt("player2", R.drawable.purple_player).apply();
                    prefs.edit().putInt("puck", R.drawable.grey_puck).apply();
                } else if (checkedId == R.id.radio_kitten) {
                    prefs.edit().putString("theme", "kitten").apply();
                    prefs.edit().putInt("player1", R.drawable.paw_down).apply();
                    prefs.edit().putInt("player2", R.drawable.paw_up).apply();
                    prefs.edit().putInt("puck", R.drawable.mouse_puck).apply();
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
    public void setButtons(RadioGroup pointsGroup,RadioGroup frictionGroup, RadioGroup themeGroup,
                           int set,String friction, String theme){
        if(set == 3){
            pointsGroup.check(R.id.radio_three);
        } else if (set == 5){
            pointsGroup.check(R.id.radio_five);
        } else {
            pointsGroup.check(R.id.radio_ten);
        }

        switch (friction) {
            case "none" :  frictionGroup.check(R.id.radio_none);
                break;
            case "some" : frictionGroup.check(R.id.radio_some);
                break;
            case "much" : frictionGroup.check(R.id.radio_much);
                break;
            default : frictionGroup.check(R.id.radio_some);
                break;
        }

    /*    if(friction.equals("none")){
            frictionGroup.check(R.id.radio_none);
        } else if (friction.equals("some")){
            frictionGroup.check(R.id.radio_some);
        } else {
            frictionGroup.check(R.id.radio_much);
        } */

        switch (theme) {
            case "orange and blue" : themeGroup.check(R.id.radio_orange_blue);
                break;
            case "red and green" : themeGroup.check(R.id.radio_red_green);
                break;
            case "yellow and purple" : themeGroup.check(R.id.radio_yellow_purple);
                break;
            case "kitten" : themeGroup.check(R.id.radio_kitten);
                break;
            default : themeGroup.check(R.id.radio_orange_blue);
                break;
        }


    }

}
