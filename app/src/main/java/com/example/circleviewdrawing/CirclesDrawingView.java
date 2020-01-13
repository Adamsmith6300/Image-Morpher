package com.example.circleviewdrawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class CirclesDrawingView extends View {

    private static final String TAG = "CirclesDrawingView";

    /** Main bitmap */
    private Bitmap mBitmap = null;
    private Bitmap mBitmap2 = null;

    private Rect mMeasuredRect;

    /** Stores data about single circle */
    private static class CircleArea {
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
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    /** Stores groups of circles to form lines **/
//    public class CircleGroup<CircleArea,CircleArea> {
//        private CircleArea l;
//        private R r;
//        public Pair(L l, R r){
//            this.l = l;
//            this.r = r;
//        }
//        public L getL(){ return l; }
//        public R getR(){ return r; }
//        public void setL(L l){ this.l = l; }
//        public void setR(R r){ this.r = r; }
//    }

    /** Paint to draw circles */
    private Paint mCirclePaint;

    private final Random mRadiusGenerator = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 20;

    private static final int CIRCLES_LIMIT = 10;

    /** All available circles */
    private ArrayList<ArrayList<CircleArea>> mCircles = new ArrayList<ArrayList<CircleArea>>();
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>();

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public CirclesDrawingView(final Context ct) {
        super(ct);

        init(ct);
    }

    public CirclesDrawingView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }

    public CirclesDrawingView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);

        init(ct);
    }

    private void init(final Context ct) {
        // Generate bitmap used for background
        mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.cat);
        //mBitmap2 = BitmapFactory.decodeResource(ct.getResources(), R.drawable.cat);

        mCirclePaint = new Paint();

        mCirclePaint.setColor(Color.GREEN);
        mCirclePaint.setStrokeWidth(2);
        mCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(final Canvas canv) {
        // background bitmap to cover all area
        canv.drawBitmap(mBitmap, null, mMeasuredRect, null);
        //canv.drawBitmap(mBitmap2, null, mMeasuredRect, null);

        for (List<CircleArea> circleList : mCircles){
            CircleArea c1 = circleList.get(0);
            canv.drawCircle(c1.centerX, c1.centerY, c1.radius, mCirclePaint);
            if(circleList.size() == 2){
                CircleArea c2 = circleList.get(1);
                canv.drawCircle(c2.centerX, c2.centerY, c2.radius, mCirclePaint);
                canv.drawLine(c1.centerX, c1.centerY, c2.centerX, c2.centerY, mCirclePaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;

        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer");

        mCirclePointer.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     *
     * @return obtained {@link CircleArea}
     */
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, RADIUS_LIMIT);

            Log.w(TAG, "Added circle " + touchedCircle);
            if (mCircles.size() > 0){
                ArrayList<CircleArea> latestList = mCircles.get(mCircles.size() - 1);
                if (latestList.size() < 2) {
                    latestList.add(touchedCircle);
                } else {
                    ArrayList<CircleArea> newList = new ArrayList<CircleArea>();
                    newList.add(touchedCircle);
                    mCircles.add(newList);
                }
            } else {
                ArrayList<CircleArea> newList = new ArrayList<CircleArea>();
                newList.add(touchedCircle);
                mCircles.add(newList);
            }
        }

        return touchedCircle;
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     *
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (List<CircleArea> circleList : mCircles) {
            CircleArea c1 = circleList.get(0);
            if ((c1.centerX - xTouch) * (c1.centerX - xTouch) + (c1.centerY - yTouch) * (c1.centerY - yTouch) <= c1.radius * c1.radius) {
                touched = c1;
                break;
            }
            if(circleList.size() == 2){
                CircleArea c2 = circleList.get(1);
                if ((c2.centerX - xTouch) * (c2.centerX - xTouch) + (c2.centerY - yTouch) * (c2.centerY - yTouch) <= c2.radius * c2.radius) {
                    touched = c2;
                    break;
                }
            }
        }

        return touched;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void clearLines(){
        Log.w(TAG, "Clear all circles, size is " + mCircles.size());
        // remove first circle
        mCircles.clear();
        invalidate();
    }
}