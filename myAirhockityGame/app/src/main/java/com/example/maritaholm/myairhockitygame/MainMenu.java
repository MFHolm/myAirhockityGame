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


public class MainMenu extends Activity {

    private int points = 3;
    static final int SETTINGS_REQUEST = 1;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final MediaPlayer playSoundButtonTouch = MediaPlayer.create(getApplicationContext(),R.raw.menutouch);
        final Button startButton = (Button) findViewById(R.id.quickgamebutton);
        final Button outof3Button = (Button) findViewById(R.id.outof3button);
        final Button settingsButton = (Button) findViewById(R.id.settingsbutton);
        final Button quitButton = (Button) findViewById(R.id.quitbutton);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
                Intent quickGame = new Intent(MainMenu.this, Game.class);
                startActivity(quickGame);
            }
        });

        outof3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundButtonTouch.start();
                prefs.edit().putBoolean("bestOutOf3", true).commit();
                Intent outof3Game = new Intent(MainMenu.this, Game.class);
                startActivity(outof3Game);


            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(MainMenu.this,Settings.class);
                playSoundButtonTouch.start();
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
