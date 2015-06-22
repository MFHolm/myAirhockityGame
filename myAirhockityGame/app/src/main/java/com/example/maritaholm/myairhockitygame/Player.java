package com.example.maritaholm.myairhockitygame;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.RelativeLayout;

/**
 * Created by MaritaHolm on 17/06/15.
 */

public class Player extends View {
    private final Paint mPainter = new Paint();
    private float xPos;
    private float yPos;
    private float radius;
    private String name;
    Bitmap mScaledBitmap;


    public Player(String name, Context context, float x, float y, Bitmap bitmap,int radius) {
        super(context);
        this.name = name;
        this.xPos = x;
        this.yPos = y;
        this.radius = radius;
        this.mScaledBitmap = Bitmap.createScaledBitmap(bitmap,  2 * (int)radius, 2 * (int)radius, false);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //Draws the player with the given bitmap.
        canvas.drawBitmap(mScaledBitmap, xPos, yPos, mPainter);

    }

    public boolean intersects(float x, float y) {
        //Checks if the player is at the (x,y) position.
        return (Math.abs(x - (xPos + radius)) <= radius && Math.abs(y - (yPos + radius)) <= radius);
    }

    public boolean intersects(Puck puck) {
        //Checks if the player intersects with a given puck.
        return (distanceTo(puck) <= radius+puck.getRadius() );

    }
    public void moveTo(float x, float y) {
        //Moves the pucks centre to the position (x,y).
        xPos = x - radius;
        yPos = y - radius;
        postInvalidate();
    }


    public double getRadius() {
        return radius;
    }

    public double distanceTo(Puck puck) {
        //Determines distance to given puck.
        return (Math.sqrt(Math.pow(Math.abs((puck.getX()+puck.getRadius())- (xPos + radius)), 2)+
                Math.pow(Math.abs((puck.getY()+puck.getRadius())-(yPos+radius)),2)));
    }
    public float getXPos(){
        return this.xPos;
    }

    public float getYPos(){
        return this.yPos;
    }

    public String getName() {
        return this.name;
    }


}
