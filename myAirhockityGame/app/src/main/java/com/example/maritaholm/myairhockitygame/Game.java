package com.example.maritaholm.myairhockitygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.maritaholm.myairhockitygame.Player;
import com.example.maritaholm.myairhockitygame.Puck;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by MaritaHolm on 17/06/15.
 */
public class Game extends Activity implements View.OnTouchListener {

    private ViewGroup mFrame;
    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;
    private Player player1;
    private Player player2;
    private Field mField;
    private Puck puck;
    private static final int REFRESH_RATE = 40;
    private int pointsToWin;
    private String friction;
    private Boolean mode;
    private static final String TAG = "Tag-AirHockity";
    private Player[] players;
    SharedPreferences prefs = null;
    int width;
    int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        // Set up user interface

        mFrame = (ViewGroup) findViewById(R.id.frame);
        mFrame.setOnTouchListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        pointsToWin = prefs.getInt("points", 0);
        friction = prefs.getString("friction", null);
        mode = prefs.getBoolean("mode",false);

        mField = new Field(getApplicationContext(),mFrame);
        mFrame.addView(mField);

        players = new Player[2];

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;

        //Player 1
        int i = prefs.getInt("player1",R.drawable.orange_player);
        mBitmap1 = BitmapFactory.decodeResource(getResources(), i, opts);
        player1 = new Player("player1",getApplicationContext(), width/2 - 128,128, mBitmap1);
        players[0]=player1;
        mFrame.addView(player1);

        //Player 2
        mBitmap2 = BitmapFactory.decodeResource(getResources(), prefs.getInt("player2",R.drawable.blue_player), opts);
        player2 = new Player("player2", getApplicationContext(), width/2 - 128,height - 3 * 128, mBitmap2);
        players[1]=player2;
        mFrame.addView(player2);
        //The puck
        mBitmap3 = BitmapFactory.decodeResource(getResources(), prefs.getInt("puck",R.drawable.grey_puck));
        puck = new Puck(getBaseContext(), (float) width / 2 - 20, (float) height / 2 - 2 * 32 - 10, mBitmap3, mFrame, this,this.friction);
        mFrame.addView(puck);
        start();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void start() {

        // Creates a WorkerThread

        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);

        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                puck.move(REFRESH_RATE);

                puck.deaccelerate();
                puck.postInvalidate();

                if (puck.topGoal()) {
                    // vibrateOnGoal();
                    mField.setScoreBot(mField.getScoreBot() + 1);
                    resetPuck();
                }
                if (puck.botGoal()) {
                    // vibrateOnGoal();
                    mField.setScoreTop(mField.getScoreTop() + 1);
                    resetPuck();
                }
                if (mField.getScoreBot() == pointsToWin) {
                    mField.setBotWins(mField.getBotWins() + 1);
                    if (mode) {
                        if (mField.getBotWins() == 3) {
                            createWinnerDialog("Bottom").show();
                        }
                    } else {
                        createWinnerDialog("Bottom").show();
                    }
                }
                if (mField.getScoreTop() == pointsToWin) {
                    mField.setTopWins(mField.getTopWins() + 1);
                    if (mode) {
                        if (mField.getTopWins() == 3) {
                            createWinnerDialog("Top").show();
                        }
                    } else {
                        createWinnerDialog("Top").show();
                    }

                }
            }
        }, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
    }
    private void vibrateOnGoal(){
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        // Vibrate for 800 milliseconds
        v.vibrate(800);
    }

    private boolean intersects(float x, float y) {
        for (Player p : players) {
            if (p.intersects(x, y)) {
                return true;
            }
        }
        return false;
    }

    private Player getPlayerAt(float x, float y) {
        for (Player p : players) {
            if (p.intersects(x, y)) {
                return p;
            }
        }
        return null;
    }

    private void resetPuck(){
        puck.resetVelocity();
        puck.setX(width / 2 - 20);
        puck.setY(height / 2 - 2 * 32 - 10);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            Player p = getPlayerAt(event.getX(i), event.getY(i));

            if (p != null) {
                float x = event.getX(i);
                float y = event.getY(i);
                switch (p.getName()) {
                    case "player1":
                        if (y >= (mFrame.getHeight() / 2) - p.getRadius() ) {

                            p.moveTo(x, (float) ((mFrame.getHeight() / 2)-p.getRadius()));
                        }
                        else {
                            p.moveTo(x, y);
                        }
                        break;
                    case "player2":
                        if (y <= (mFrame.getHeight() / 2)+p.getRadius()) {

                            p.moveTo(x, (float) ((mFrame.getHeight() / 2)+p.getRadius()));
                        }
                        else {
                            p.moveTo(x, y);
                        }
                        break;
                    default:
                        break;
                }
                VelocityTracker tracker = VelocityTracker.obtain();
                tracker.addMovement(event);
                tracker.computeCurrentVelocity(500);
                if (p.intersects(puck)) {
                    float xVel = VelocityTrackerCompat.getXVelocity(tracker, event.getPointerId(i));
                    float yVel = VelocityTrackerCompat.getYVelocity(tracker, event.getPointerId(i));

                    puck.IncreaseVelocity(xVel, yVel);
                    return true;
                }
            }
        }
        return true;
    }

    public Player[] getPlayers() {
        return players;
    }

    @Override
    public void onBackPressed() {

        createDialog(puck.getXVel(),puck.getYVel()).show();




    }

    public Dialog createDialog(final float tempXVel, final float tempYVel) {


        puck.setVelocity(0,0);
        // FLAG_PAUSE_PUCK = true;

        CharSequence[] choices = new CharSequence[2];
        choices[0] = "Resume";
        choices[1] = "Main Menu";

        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
        builder.setTitle("PAUSED")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            finish();
                        } else if (which == 0) {
                            // FLAG_PAUSE_PUCK = false;
                            puck.setVelocity(tempXVel, tempYVel);
                        }
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                puck.setVelocity(tempXVel, tempYVel);
                //FLAG_PAUSE_PUCK = false;
            }
        });
        return builder.create();
    }

    public AlertDialog createWinnerDialog(final String winner) {
        CharSequence[] choice = new CharSequence[2];
        choice[0] = "Play Again";
        choice[1] = "Main Menu";

        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
        builder.setTitle(winner+" wins the game!")
                .setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        } else if (which == 1) {
                            finish();
                        }
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        return builder.create();
    }


}
