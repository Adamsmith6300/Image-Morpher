package com.example.imagemorpher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class CirclesDrawingView extends View {

    private static final String TAG = "CirclesDrawingView";

    /** Main bitmap */
    private Bitmap mBitmap = null;
    private boolean imageLoaded = false;
    private Rect mMeasuredRect;

    /** Paint to draw circles */
    private Paint mCirclePaint;
    private Paint selectedMCirclePaint;

    private final Random mRadiusGenerator = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 50;

    private static final int CIRCLES_LIMIT = 10;

    /** All available circles */
    private ArrayList<Line> lines = new ArrayList<Line>();
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>();

    private CircleArea lastTouched = new CircleArea(0,0,0);

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
        setFocusableInTouchMode(true);
        mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.cat);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.GREEN);
        mCirclePaint.setStrokeWidth(2);
        mCirclePaint.setStyle(Paint.Style.FILL);

        selectedMCirclePaint = new Paint();
        selectedMCirclePaint.setColor(Color.BLUE);
        selectedMCirclePaint.setStrokeWidth(2);
        selectedMCirclePaint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void onDraw(final Canvas canv) {
        // background bitmap to cover all area
        canv.drawBitmap(mBitmap, null, mMeasuredRect, null);
        for (Line line : lines){
            CircleArea c1 = line.getStart();
            if(line.getEnd() == null){
                canv.drawCircle(c1.centerX, c1.centerY, c1.radius, c1.centerX == lastTouched.centerX ? selectedMCirclePaint : mCirclePaint);
            } else {
                CircleArea c2 = line.getEnd();
                if(c1.centerX == lastTouched.centerX || c2.centerX == lastTouched.centerX){
                    canv.drawCircle(c1.centerX, c1.centerY, c1.radius, selectedMCirclePaint);
                    canv.drawCircle(c2.centerX, c2.centerY, c2.radius, selectedMCirclePaint);
                    canv.drawLine(c1.centerX, c1.centerY, c2.centerX, c2.centerY, selectedMCirclePaint);
                } else{
                    canv.drawCircle(c1.centerX, c1.centerY, c1.radius, mCirclePaint);
                    canv.drawCircle(c2.centerX, c2.centerY, c2.radius, mCirclePaint);
                    canv.drawLine(c1.centerX, c1.centerY, c2.centerX, c2.centerY, mCirclePaint);
                }
            }
        }
    }

    private View findViewAtPosition(View parent, int x, int y) {
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)parent;
            for (int i=0; i<viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                View viewAtPosition = findViewAtPosition(child, x, y);
                if (viewAtPosition != null) {
                    return viewAtPosition;
                }
            }
            return null;
        } else {
            Rect rect = new Rect();
            parent.getGlobalVisibleRect(rect);
            if (rect.contains(x, y)) {
                return parent;
            } else {
                return null;
            }
        }
    }

    public void resetLastTouched(){
        lastTouched = new CircleArea(0,0,0);
    }

    public void deleteLastTouched(){
        Iterator<Line> i = lines.iterator();
        while (i.hasNext()) {
            Line line = i.next(); // must be called before you can call i.remove()
            CircleArea c1 = line.getStart();
            if(line.getEnd() == null && c1.centerX == lastTouched.centerX){
                i.remove();
            } else {
                CircleArea c2 = line.getEnd();
                if(c1.centerX == lastTouched.centerX || c2.centerX == lastTouched.centerX){
                    i.remove();
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if(!imageLoaded){
            return super.onTouchEvent(event);
        }
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
                touchedCircle = obtainTouchedCircle(xTouch, yTouch, event);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);
                lastTouched = touchedCircle;

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch, event);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                lastTouched = touchedCircle;

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                //Log.w(TAG, "Move");
                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                        lastTouched = touchedCircle;
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
        //Log.w(TAG, "clearCirclePointer");
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
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch, final MotionEvent event) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);
        if (null == touchedCircle) {
                //Log.i(TAG, "View under finger: " + findViewAtPosition(getRootView(), (int)event.getRawX(), (int)event.getRawY()));
                View currentCanvas = findViewAtPosition(getRootView(), (int)event.getRawX(), (int)event.getRawY());
                //Log.i("ID",getResources().getResourceEntryName(currentCanvas.getId())+"");
                String currentCanvasName = getResources().getResourceEntryName(currentCanvas.getId());
                //Log.w("equal?",currentCanvasName.equalsIgnoreCase("firstImage")+"");
                if(currentCanvasName.equalsIgnoreCase("firstImage")){
                    CirclesDrawingView secondImg = getRootView().findViewById(R.id.secondImage);
                    //Log.w("Grabbed view", secondImg.toString());
                    CircleArea circleCopy = secondImg.createCircleCopy(xTouch, yTouch);
                    secondImg.invalidate();
                } else {
                    CirclesDrawingView firstImg = getRootView().findViewById(R.id.firstImage);
                    CircleArea circleCopy = firstImg.createCircleCopy(xTouch, yTouch);
                    firstImg.invalidate();
                }
            touchedCircle = createCircleCopy(xTouch, yTouch);
        }

        return touchedCircle;
    }

    public CircleArea createCircleCopy(final int xTouch, final int yTouch){
        CircleArea touchedCircle = new CircleArea(xTouch, yTouch, RADIUS_LIMIT);
        //Log.w(TAG, "Added circle " + touchedCircle);
        if (lines.size() > 0){
            Line latestLine = lines.get(lines.size() - 1);
            if (latestLine.getEnd() == null) {
                latestLine.setEnd(touchedCircle);
            } else {
                Line newLine = new Line(touchedCircle);
                lines.add(newLine);
            }
        } else {
            Line newLine = new Line(touchedCircle);
            lines.add(newLine);
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

        for (Line line : lines) {
            CircleArea c1 = line.getStart();
            if ((c1.centerX - xTouch) * (c1.centerX - xTouch) + (c1.centerY - yTouch) * (c1.centerY - yTouch) <= c1.radius * c1.radius) {
                touched = c1;
                break;
            }
            if(line.getEnd() != null){
                CircleArea c2 = line.getEnd();
                if ((c2.centerX - xTouch) * (c2.centerX - xTouch) + (c2.centerY - yTouch) * (c2.centerY - yTouch) <= c2.radius * c2.radius) {
                    touched = c2;
                    break;
                }
            }
        }

        return touched;
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void clearLines(){
        //Log.w(TAG, "Clear all circles, size is " + lines.size());
        // remove first circle
        lines.clear();
        invalidate();
    }


    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public Rect getmMeasuredRect() {
        return mMeasuredRect;
    }

    public void setmMeasuredRect(Rect mMeasuredRect) {
        this.mMeasuredRect = mMeasuredRect;
    }

    public ArrayList<Line> getlines() {
        return lines;
    }

    public void setlines(ArrayList<Line>  lines) {
        this.lines = lines;
    }

    public SparseArray<CircleArea> getmCirclePointer() {
        return mCirclePointer;
    }

    public void setmCirclePointer(SparseArray<CircleArea> mCirclePointer) {
        this.mCirclePointer = mCirclePointer;
    }

    public CircleArea getLastTouched() {
        return lastTouched;
    }

    public void setLastTouched(CircleArea lastTouched) {
        this.lastTouched = lastTouched;
    }

}