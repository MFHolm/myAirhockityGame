package com.example.maritaholm.myairhockitygame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        prefs.edit().putString("friction", "some").commit();


        final MediaPlayer playSoundButtonTouch = MediaPlayer.create(getApplicationContext(),R.raw.menutouch);
        final Button startButton = (Button) findViewById(R.id.quickgamebutton);
        final Button outof3Button = (Button) findViewById(R.id.outof3button);
        final Button settingsButton = (Button) findViewById(R.id.settingsbutton);
        final Button quitButton = (Button) findViewById(R.id.quitbutton);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
                prefs.edit().putBoolean("mode", false).commit();
                Intent quickGame = new Intent(MainActivity.this, Game.class);
                startActivity(quickGame);
            }
        });

        outof3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
                prefs.edit().putBoolean("mode", true).commit();
                Log.d("test",String.valueOf(prefs.getBoolean("mode",true)));
                Intent outof3Game = new Intent(MainActivity.this, Game.class);
                startActivity(outof3Game);


            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
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
