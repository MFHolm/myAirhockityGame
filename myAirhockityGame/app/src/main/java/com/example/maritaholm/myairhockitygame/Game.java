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
import android.media.MediaPlayer;
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
    private int PLAYER_RADIUS;
    private int PUCK_RADIUS;
    private int pointsToWin;
    private String friction;
    private Boolean mode;
    private static final String TAG = "Tag-AirHockity";
    private Player[] players;
    private int round = 1;
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
        mFrame = (ViewGroup) findViewById(R.id.frame);
        mFrame.setOnTouchListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        pointsToWin = prefs.getInt("points", 0);
        friction = prefs.getString("friction", null);
        mode = prefs.getBoolean("mode",false);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        mBitmap1 = BitmapFactory.decodeResource(getResources(), prefs.getInt("player1",R.drawable.orange_player), opts);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), prefs.getInt("player2",R.drawable.blue_player), opts);

        mField = new Field(getApplicationContext(),mFrame,mBitmap1,mBitmap2);
        mFrame.addView(mField);

        players = new Player[2];

        PLAYER_RADIUS = width/10;
        PUCK_RADIUS = width/30;

        //Player 1
        player1 = new Player("player1",getApplicationContext(), width/2 - PLAYER_RADIUS,PLAYER_RADIUS, mBitmap1, PLAYER_RADIUS);
        players[0]=player1;
        mFrame.addView(player1);

        //Player 2
        player2 = new Player("player2", getApplicationContext(), width/2 - PLAYER_RADIUS,height - 3 * PLAYER_RADIUS, mBitmap2,PLAYER_RADIUS);
        players[1]=player2;
        mFrame.addView(player2);

        //The puck
        mBitmap3 = BitmapFactory.decodeResource(getResources(), prefs.getInt("puck",R.drawable.grey_puck));
        puck = new Puck(getBaseContext(), (float) width / 2 - PUCK_RADIUS/2,
                (float) height / 2 - 2* PUCK_RADIUS, mBitmap3, mFrame, this,this.friction, PUCK_RADIUS);
        mFrame.addView(puck);
        start();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void start() {
        final MediaPlayer playSoundOnGoal = MediaPlayer.create(getApplicationContext(),R.raw.ongoal);
        final MediaPlayer playSoundOnWin = MediaPlayer.create(getApplicationContext(),R.raw.cheer);

        // Creates a WorkerThread

        final ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);

        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                puck.move(REFRESH_RATE);
                puck.deaccelerate();
                puck.postInvalidate();

                if (puck.topGoal()) {
                    vibrateOnGoal();
                    playSoundOnGoal.start();
                    mField.setScoreBot(mField.getScoreBot() + 1);
                    resetPuck();
                }
                if (puck.botGoal()) {
                    vibrateOnGoal();
                    playSoundOnGoal.start();
                    mField.setScoreTop(mField.getScoreTop() + 1);
                    resetPuck();
                }
                if (mField.getScoreBot() == pointsToWin) {
                    playSoundOnWin.start();
                    mField.setBotWins(mField.getBotWins() + 1);
                    if (mode) {
                        mField.drawRoundWinner("bot", round);
                        round++;
                        mField.resetScore();
                        resetPuck();
                        if (mField.getBotWins() == 2) {
                            showWinnerDialog("Bottom");
                            executor.shutdown();

                        }
                    } else {
                        showWinnerDialog("Bottom");
                        executor.shutdown();
                    }
                }
                if (mField.getScoreTop() == pointsToWin) {
                    playSoundOnWin.start();
                    mField.setTopWins(mField.getTopWins() + 1);
                    if (mode) {
                        mField.drawRoundWinner("top", round);
                        round++;
                        mField.resetScore();
                        resetPuck();
                        if (mField.getTopWins() == 2) {
                            showWinnerDialog("Top");
                            executor.shutdown();
                        }
                    } else {
                        showWinnerDialog("Top");
                        executor.shutdown();
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

    private void resetPuck() {
        puck.resetVelocity();
        puck.setX(width / 2 - 20);
        puck.setY(height / 2 - 2 * 32 - 10);
        mFrame.postInvalidate();
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

    public void showWinnerDialog(String winner){
        DialogFragment mWinnerDialog = WinnerDialog.newInstance(winner);

        mWinnerDialog.show(getFragmentManager(), "dialog");
    }
}
