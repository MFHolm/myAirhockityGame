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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;


public class Settings extends Activity {

    private static final String TAG = "Tag-AirHockity";
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

        // Resets preferences
        defButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks toggle buttons
                playSoundButtonTouch.start();
                pointsGroup.check(R.id.radio_three);
                frictionGroup.check(R.id.radio_some);
                themeGroup.check(R.id.radio_orange_blue);

                // Updates pref with the default values
                prefs.edit().putInt("player1", R.drawable.orange_player).apply();
                prefs.edit().putInt("player2", R.drawable.blue_player).apply();
                prefs.edit().putInt("puck", R.drawable.grey_puck).apply();
            }
        });


        // Check if user updates points needed to win, the amount of friction or chooses another theme
        // Saves chosen preferences to a PreferenceManager prefs
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
                    prefs.edit().putString("friction", "some").commit();
                } else if (checkedId == R.id.radio_much){
                    prefs.edit().putString("friction", "much").commit();
                }
            }
        });

        themeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String tempTheme = null;
                int tempPlayer1 = -1, tempPlayer2 = -1, tempPuck = -1;

                if(checkedId == R.id.radio_orange_blue) {
                    tempTheme = "orange and blue";
                    tempPlayer1 = R.drawable.orange_player;
                    tempPlayer2 = R.drawable.blue_player;
                    tempPuck = R.drawable.grey_puck;
                } else if (checkedId == R.id.radio_red_green) {
                    tempTheme = "red and green";
                    tempPlayer1 = R.drawable.red_player;
                    tempPlayer2 =  R.drawable.green_player;
                    tempPuck = R.drawable.grey_puck;
                } else if (checkedId == R.id.radio_yellow_purple){
                    tempTheme = "yellow and purple";
                    tempPlayer1 = R.drawable.yellow_player;
                    tempPlayer2 = R.drawable.purple_player;
                    tempPuck = R.drawable.grey_puck;
                } else if (checkedId == R.id.radio_kitten) {
                    tempTheme = "kitten";
                    tempPlayer1 = R.drawable.paw_down;
                    tempPlayer2 = R.drawable.paw_up;
                    tempPuck = R.drawable.mouse_puck;
                }

                prefs.edit().putString("theme", tempTheme).apply();
                prefs.edit().putInt("player1", tempPlayer1).apply();
                prefs.edit().putInt("player2", tempPlayer2).apply();
                prefs.edit().putInt("puck", tempPuck).apply();
            }
        });

        // Returns to main menu
        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playSoundButtonTouch.start();
                finish();
            }
        });
    }

    // Updates which radio button is checked
    // Gets which button is selected from prefs
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
        Log.d(TAG, "in switch: " + theme);


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
