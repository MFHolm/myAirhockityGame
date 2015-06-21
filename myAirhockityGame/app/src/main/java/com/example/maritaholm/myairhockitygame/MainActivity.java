package com.example.maritaholm.myairhockitygame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private int points = 3;
    static final int SETTINGS_REQUEST = 1;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.edit().putInt("points", 3).commit();
        prefs.edit().putString("friction","some").commit();



        final Button startButton = (Button) findViewById(R.id.quickgamebutton);
        startButton.setBackgroundColor(Color.RED);
        startButton.setTextColor(Color.WHITE);
        final Button outof3Button = (Button) findViewById(R.id.outof3button);
        outof3Button.setBackgroundColor(Color.RED);
        outof3Button.setTextColor(Color.WHITE);
        final Button settingsButton = (Button) findViewById(R.id.settingsbutton);
        settingsButton.setBackgroundColor(Color.RED);
        settingsButton.setTextColor(Color.WHITE);
        final Button quitButton = (Button) findViewById(R.id.quitbutton);
        quitButton.setBackgroundColor(Color.RED);
        quitButton.setTextColor(Color.WHITE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("mode",false);
                Intent quickGame = new Intent(MainActivity.this,Game.class);
                startActivity(quickGame);
            }
        });

        outof3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("mode",true);
                Intent outof3Game = new Intent(MainActivity.this,Game.class);
                startActivity(outof3Game);


            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(MainActivity.this,Settings.class);
                startActivity(settings);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }





}
