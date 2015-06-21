package com.example.maritaholm.myairhockitygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

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


    public Puck(Context context, float x, float y, Bitmap bitmap, View frame, Game game) {
        super(context);
        this.xPos = x;
        this.yPos = y;
        this.xVel = 0;
        this.yVel = 0;
        this.radius = 32;
        this.mFrame = frame;
        this.game = game;
        this.mScaledBitmap = Bitmap.createScaledBitmap(bitmap,  2 * (int)radius, 2 * (int)radius, false);
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

    public void setX(float x){
        this.xPos=x;
    }

    public void setY(float y){
        this.yPos=y;
    }

    public void setVelocity(float x, float y) {
        xVel += x;
        yVel += y;
    }

    public void resetVelocity(){
        xVel = 0;
        yVel = 0;
    }

    protected void move(int rate) {
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
            if (!(-0.000001 < xVel && xVel < 0.00001 && -0.00001 < yVel && yVel < 0.00001)){
                Player p = intersectsPlayer();
                Vector newDircetion = getNewDirection(p);
                double speed = getSpeed();
                xVel = (float) newDircetion.getX() * (float) speed;
                yVel = (float) newDircetion.getY() * (float) speed;
            }
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
        return (xPos + 2 * radius >= mFrame.getRight());
    }

    private boolean intersectsTop() {
        return (yPos <= mFrame.getTop() &&!((xPos>=(mFrame.getRight()/2)-100)&&(xPos+2*radius<=(mFrame.getRight()/2)+100)));
    }

    private boolean intersectsBottom() {
        return (yPos + 2 * radius >= mFrame.getBottom())&&!((xPos>=(mFrame.getRight()/2)-100)&&(xPos+2*radius<=(mFrame.getRight()/2)+100));
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

    public Vector getNewDirection(Player p) {

        double playerCentrumX = p.getX()+p.getRadius();
        double playerCentrumY = p.getY()+p.getRadius();
        double centrumX = xPos + radius;
        double centrumY = yPos + radius;

        Vector radiusVector = new Vector((( p.getRadius()/(p.getRadius()+radius)) * (playerCentrumX - centrumX)),
                (( p.getRadius()/(p.getRadius()+radius)) * (playerCentrumY - centrumY)));
        Log.d(TAG, "radiusVector: " + radiusVector);
        Vector velVector = new Vector(xVel,yVel);
        Log.d(TAG, "xVel: " +xVel + " yVel: " + yVel);
        Log.d(TAG, "velVector: " + velVector);


        Vector radiusVectorNormed = new Vector(radiusVector.getX()/radiusVector.length(),
                radiusVector.getY()/radiusVector.length());
        Log.d(TAG, "radiusVectorNormed: " + radiusVectorNormed);

        double dotProductVelocityRadius = 2 * dotProduct(velVector,radiusVectorNormed);
        Log.d(TAG, "dotProductVelocityRadius: " + dotProductVelocityRadius);
        Vector radiusNormScaled = new Vector(radiusVectorNormed.getX()*dotProductVelocityRadius,
                radiusVectorNormed.getY()*dotProductVelocityRadius);
        Log.d(TAG, "radiusNormScaled: " + radiusNormScaled);


        Vector newVelocity = new Vector(velVector.getX()-radiusNormScaled.getX(),
                velVector.getY()-radiusNormScaled.getY());
        Log.d(TAG, "newVelocity: " + newVelocity);

        newVelocity.norm();

        return newVelocity;
    }
    public double getSpeed(){
        Log.d(TAG, "Speed: " + (new Vector(xVel,yVel)).length());
        return (new Vector(xVel,yVel)).length();
    }
    public float getXVel() {
        return xVel;
    }

    public float getYVel() {
        return yVel;
    }

}
