package com.example.circleviewdrawing;

public class Vector2d {

    private double x, y;

    public Vector2d() {
    }
    public Vector2d(double value) {
        this(value, value);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(Vector2d vector2d) {
        this.x = vector2d.x;
        this.y = vector2d.y;
    }

    public void copy(Vector2d vector2d) {
        this.x = vector2d.x;
        this.y = vector2d.y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2d add(Vector2d other) {
        double x = this.x + other.x;
        double y = this.y + other.y;
        return new Vector2d(x, y);
    }

    public Vector2d subtract(Vector2d other) {
        double x = this.x - other.x;
        double y = this.y - other.y;
        return new Vector2d(x, y);
    }

    public Vector2d multiply(double value) {
        return new Vector2d(value * x, value * y);
    }

    public double dotProduct(Vector2d other) {
        return other.x * x + other.y * y;
    }

    public double getLength() {
        return (double) Math.sqrt(dotProduct(this));
    }

    public Vector2d normalize() {
        double magnitude = getLength();
        if ( magnitude == 0 ) {
            magnitude = 1;
        }
        x = x / magnitude;
        y = y / magnitude;
        return this;
    }
}