package com.example.maritaholm.myairhockitygame;

/**
 * Created by MaritaHolm on 19/06/15.
 */
public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX(){
        return x;
    }
    public double getY() {
        return y;
    }
    public double length(){
        return Math.sqrt(getX()*getX()+getY()*getY());
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public void norm() {
        if (!(-0.00001 <length() && length() < 0.00001)) {
            this.x = this.x/this.length();
            this.y = this.y/this.length();
        }
    }


}