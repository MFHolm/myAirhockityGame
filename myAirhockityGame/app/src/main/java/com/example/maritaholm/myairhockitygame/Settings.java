package com.example.maritaholm.myairhockitygame;

import android.app.Activity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
        final RadioGroup soundGroup = (RadioGroup) findViewById(R.id.sound_group);
        int set = prefs.getInt("points",0);
        String friction = prefs.getString("friction", null);
        String theme = prefs.getString("theme", null);
        Boolean sound = prefs.getBoolean("sound", true);

        setButtons(soundGroup,pointsGroup,frictionGroup,themeGroup,set,friction,theme,sound);

        Button defButton = (Button) findViewById(R.id.default_button);
        Button retButton = (Button) findViewById(R.id.return_button);

        //sound and vibration
        soundGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.sound_on) {
                    prefs.edit().putBoolean("sound", true).apply();
                } else {
                    prefs.edit().putBoolean("sound", false).apply();
                }
            }
        });


        // Resets preferences
        defButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks toggle buttons
                soundGroup.check(R.id.sound_on);
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
                    prefs.edit().putInt("points", 3).apply();
                } else if (checkedId == R.id.radio_five) {
                    prefs.edit().putInt("points", 5).apply();
                } else if (checkedId == R.id.radio_ten) {
                    prefs.edit().putInt("points", 10).apply();
                }
            }
        });

        frictionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(R.id.radio_none == checkedId){
                    prefs.edit().putString("friction", "none").apply();
                } else if (checkedId == R.id.radio_some){
                    prefs.edit().putString("friction","some").apply();
                } else if (checkedId == R.id.radio_much){
                    prefs.edit().putString("friction","much").apply();
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
                    prefs.edit().putString("theme", "yellow and purple").apply();
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

        // Returns to main menu
        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Updates which radio button is checked
    // Gets which button is selected from prefs
    public void setButtons(RadioGroup soundGroup,RadioGroup pointsGroup,RadioGroup frictionGroup, RadioGroup themeGroup,
                           int set,String friction, String theme, Boolean isSoundOn){
        if (isSoundOn){
            soundGroup.check(R.id.sound_on);
        }else{
            soundGroup.check(R.id.sound_off);
        }

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
