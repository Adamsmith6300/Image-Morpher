package com.example.circleviewdrawing;

/** Stores data about single circle */
public class CircleArea {
    int radius;
    int centerX;
    int centerY;

    CircleArea(int centerX, int centerY, int radius) {
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public String toString() {
        return "Point[" + centerX + ", " + centerY + "]";
    }
}