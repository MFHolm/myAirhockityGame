package com.example.maritaholm.myairhockitygame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    //Game settings
    private int pointsToWin;
    private String friction;
    private int round = 1;

    //True for best out of 3, otherwise false
    //True for best out of 3, otherwise false.
    public boolean isSoundEnabled;
    private Boolean mode;

    private Boolean bestOutOf3, sound;
    private Player[] players;

    SharedPreferences prefs = null;

    int width;
    int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up view
        setContentView(R.layout.activity_game);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        mFrame = (ViewGroup) findViewById(R.id.frame);
        mFrame.setOnTouchListener(this);

        //Get settings
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Uses default if key is null
        pointsToWin = prefs.getInt("points", 3);
        friction = prefs.getString("friction", "some");
        bestOutOf3 = prefs.getBoolean("bestOutOf3", false);
        sound = prefs.getBoolean("sound",true);

        //Set up bitmaps to display players
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
                (float) height / 2 - 2* PUCK_RADIUS, mBitmap3, mFrame,this.friction, PUCK_RADIUS);
        mFrame.addView(puck);
        start();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void start() {
        //Set up audio
        final MediaPlayer playSoundOnGoal = MediaPlayer.create(getApplicationContext(),R.raw.ongoal);
        final MediaPlayer playSoundOnWin = MediaPlayer.create(getApplicationContext(),R.raw.cheer);

        // Creates a WorkerThread
        final ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1);

        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                //Moving the puck
                puck.move(REFRESH_RATE);
                puck.deaccelerate();
                puck.postInvalidate();


                //Logic for incrementing goal score and determining game winner
                if (puck.topGoal()) {
                    if (prefs.getBoolean("sound", true)) {
                        vibrateOnGoal();
                        playSoundOnGoal.start();
                    }
                    mField.setScoreBot(mField.getScoreBot() + 1);
                    resetPuck();
                }
                if (puck.botGoal()) {
                    if (prefs.getBoolean("sound", true)) {
                        vibrateOnGoal();
                        playSoundOnGoal.start();
                    }
                    mField.setScoreTop(mField.getScoreTop() + 1);
                    resetPuck();
                }
                if (mField.getScoreBot() == pointsToWin) {
                    if (prefs.getBoolean("sound", true)) {
                        playSoundOnWin.start();
                    }

                    mField.setBotWins(mField.getBotWins() + 1);
                    if (bestOutOf3) {
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
                    if (prefs.getBoolean("sound", true)) {
                        playSoundOnWin.start();
                    }
                    mField.setTopWins(mField.getTopWins() + 1);
                    if (bestOutOf3) {
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
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    //Check if any players intersect a given (x,y) position
    private boolean intersects(float x, float y) {
        for (Player p : players) {
            if (p.intersects(x, y)) {
                return true;
            }
        }
        return false;
    }

    //Gets the player that is that the given (x,y) coordinate (if any)
    private Player getPlayerAt(float x, float y) {
        for (Player p : players) {
            if (p.intersects(x, y)) {
                return p;
            }
        }
        return null;
    }

    //Resets puck position
    private void resetPuck() {
        puck.resetVelocity();
        puck.setX(width / 2 - 20);
        puck.setY(height / 2 - 2 * 32 - 10);
        mFrame.postInvalidate();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Cycles through all players
        for (int i = 0; i < event.getPointerCount(); i++) {
            Player p = getPlayerAt(event.getX(i), event.getY(i));

            if (p != null) {
                float x = event.getX(i);
                float y = event.getY(i);
                //The switch statement makes sure that the players can't move across the center line
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

                //Increases the velocity of the puck by the velocity of the player
                //when it touches the puck
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

    //Creates a dialog if the back button is pressed
    @Override
    public void onBackPressed() {
        createDialog(puck.getXVel(),puck.getYVel()).show();

    }

    public Dialog createDialog(final float tempXVel, final float tempYVel) {

        //Pauses the game by settings puck velocity to 0
        puck.setVelocity(0,0);

        //Sets up the two button titles
        CharSequence[] choices = new CharSequence[2];
        choices[0] = "Resume";
        choices[1] = "Main Menu";

        //Set up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
        builder.setTitle("PAUSED")
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                            finish();
                        } else if (which == 0) {
                            puck.setVelocity(tempXVel, tempYVel);
                        }
                    }
                });
        //Resumes the game if the user presses outside of dialog box
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                puck.setVelocity(tempXVel, tempYVel);
            }
        });
        return builder.create();
    }

    //Show winner dialog
    public void showWinnerDialog(String winner){
        DialogFragment mWinnerDialog = WinnerDialog.newInstance(winner);
        mWinnerDialog.show(getFragmentManager(), "dialog");
    }
}
