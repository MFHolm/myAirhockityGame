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
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // Set up user interface

        mFrame = (ViewGroup) findViewById(R.id.frame);
        mFrame.setOnTouchListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        pointsToWin = prefs.getInt("points", 0);
        friction = prefs.getString("friction", null);
        mode = prefs.getBoolean("mode",false);

        mField = new Field(getApplicationContext(),mFrame);
        mField.setScoreBot(0);
        mField.setScoreBot(0);
        mField.setTopWins(0);
        mField.setBotWins(0);
        mFrame.addView(mField);

        players = new Player[2];

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.player1, opts);
        player1 = new Player("player1",getBaseContext(), 400,300, mBitmap1);
        players[0]=player1;
        mFrame.addView(player1);

        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.player2, opts);
        player2 = new Player("player2",getBaseContext(), 400,800, mBitmap2);
        players[1]=player2;
        mFrame.addView(player2);

        mBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.puck);
        puck = new Puck(getBaseContext(), 350, 600, mBitmap3, mFrame, this,this.friction);
        mFrame.addView(puck);


        Log.d("Puck",puck.getX()+" and "+puck.getY());
        start(puck, mField, mFrame);


    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void start(final Puck puck, final Field mField,final ViewGroup mFrame) {

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
                    resetPlayerPuck();

                }
                if (puck.botGoal()) {
                   // vibrateOnGoal();
                    mField.setScoreTop(mField.getScoreTop() + 1);
                    resetPlayerPuck();

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

    private void resetPlayerPuck(){
        puck.resetVelocity();
        puck.setX(mFrame.getRight() / 2);
        puck.setY(mFrame.getBottom() / 2);
        player1.moveTo(400,300);
        player2.moveTo(400,800);

        /*player1.moveTo(mFrame.getRight() / 2, mFrame.getBottom() / 4);
        player2.moveTo(mFrame.getRight()/2,mFrame.getBottom()*(3/4));*/


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
                        Log.d(TAG, "player 1");
                        if (y >= (mFrame.getHeight() / 2)-p.getRadius() ) {
                            Log.d(TAG, "over the line");
                            Log.d(TAG, ""+y);

                            p.moveTo(x, (float) ((mFrame.getHeight() / 2)-p.getRadius()));
                        }
                        else {
                            p.moveTo(x, y);
                        }
                        break;
                    case "player2":
                        Log.d(TAG, "player 2");
                        if (y <= (mFrame.getHeight() / 2)+p.getRadius()) {
                            Log.d(TAG, "over the line");
                            Log.d(TAG, ""+y);

                            p.moveTo(x, (float) ((mFrame.getHeight() / 2)+p.getRadius()));
                        }
                        else {
                            p.moveTo(x, y);
                        }
                        break;
                    default:
                        break;
                }

                //p.moveTo(x, y);
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
