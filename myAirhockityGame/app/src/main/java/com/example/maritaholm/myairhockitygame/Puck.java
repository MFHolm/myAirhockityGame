package com.example.maritaholm.myairhockitygame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by MaritaHolm on 18/06/15.
 */
public class Puck extends View {
    private final Paint mPainter = new Paint();
    private float xPos;
    private float yPos;
    private float xVel;
    private float yVel;
    private float radius;
    private Game game;
    private Bitmap mScaledBitmap;
    private static final String TAG = "Tag-AirHockity";
    private View mFrame;
    private double DEACCELATION = 0.975;


    public Puck(Context context, float x, float y, Bitmap bitmap, View frame,Game game,String friction) {
        super(context);
        this.xPos = x;
        this.yPos = y;
        this.xVel = 0;
        this.yVel = 0;
        this.radius = 32;
        this.mFrame = frame;
        this.game = game;
        this.mScaledBitmap = Bitmap.createScaledBitmap(bitmap,  2 * (int)radius, 2 * (int)radius, false);
        if(friction.equals("none")){
            DEACCELATION = 1;
        } else if(friction.equals("some")){
            DEACCELATION = 0.97;
        } else {
            DEACCELATION = 0.875;
        }
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.drawBitmap(mScaledBitmap, xPos, yPos, mPainter);

    }
    public float getRadius() {
        return radius;
    }
    public float getX() {
        return xPos;
    }
    public float getY() {
        return yPos;
    }

    public void setX(float x){this.xPos=x;}
    public void setY(float y){this.yPos=y;}
    public float getXVel() {return xVel; }
    public float getYVel() {return yVel; }
    public void IncreaseVelocity(float x, float y) {
        xVel += x;
        yVel += y;
    }

    public void setVelocity(float x, float y){
        xVel = x;
        yVel =y;
    }

    public void resetVelocity(){
        xVel = 0;
        yVel = 0;
    }

    protected void move(int rate) {
        Log.d(TAG, "(" + xPos + ", " + yPos + ")");
        Log.d(TAG, "intersectsTop: " + intersectsTop());
        if (intersectsTop()) {
            yPos = mFrame.getTop() + 1;
            yVel = yVel * (-1);
        }

        if (intersectsBottom()) {
            yPos = mFrame.getBottom() - (2 * radius + 1);
            yVel = yVel * (-1);
        }

        if (intersectsLeft()) {
            xPos = mFrame.getLeft() + 1;
            xVel = xVel * (-1);
        }

        if (intersectsRight()) {
            xPos = mFrame.getRight()-(2 * radius + 1);
            xVel = xVel * (-1);
        }
        if (intersectsPlayer() != null) {
            //nuttin
        }

        xPos += xVel/rate;
        yPos += yVel/rate;

    }
    protected boolean topGoal(){
        return (((xPos >= ((mFrame.getRight()/2)-100)))&&
                (xPos+2*radius <= ((mFrame.getRight()/2)+100))&&(yPos <= ((mFrame.getTop()+10))));
    }

    private double dotProduct(Vector a, Vector b) {
        return a.getX()*b.getX() + a.getY() * b.getY();
    }
    private double length(Vector a) {
        return Math.sqrt(Math.pow(a.getX(),2)+Math.pow(a.getY(),2));
    }
    protected boolean botGoal(){
        return (((xPos >= ((mFrame.getRight()/2)-100)))&&
                (xPos+2*radius <= ((mFrame.getRight()/2)+100))&&(yPos+2*radius >= ((mFrame.getBottom()-10))));
    }
    private boolean intersectsLeft() {
        return (xPos <= mFrame.getLeft());
    }
    private boolean intersectsRight() {
        if(mFrame.getRight()>0){
            return (xPos + 2 * radius >= mFrame.getRight());

        } else
            return false;
    }

    private boolean intersectsTop() {
        return (yPos <= mFrame.getTop() &&!((xPos>=(mFrame.getRight()/2)-100)&&(xPos+2*radius<=(mFrame.getRight()/2)+100)));
    }

    private boolean intersectsBottom() {
        if(mFrame.getBottom()>0){
            return (yPos + 2 * radius >= mFrame.getBottom())&&!((xPos>=(mFrame.getRight()/2)-100)&&(xPos+2*radius<=(mFrame.getRight()/2)+100));

        } else
            return false;
    }

    private Player intersectsPlayer() {
        Player[] players = game.getPlayers();
        for (Player p: players) {
            if (p.intersects(this)) {
                return p;
            }
        }
        return null;
    }
    public void deaccelerate() {
        xVel = xVel * (float) DEACCELATION;
        yVel = yVel * (float) DEACCELATION;
    }

}