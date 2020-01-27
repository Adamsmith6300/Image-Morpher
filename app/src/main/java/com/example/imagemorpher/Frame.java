package com.example.imagemorpher;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

public class Frame {
    public Bitmap getBmp() {
        return bmp;
    }
    public Bitmap getNewBmp() {
        return newBmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    private Bitmap bmp;
    private Bitmap newBmp;

    private ArrayList<Line> lines;


    public Frame(Bitmap bmp, int numberOfLines,ArrayList<Line> lines) {
        this.bmp = bmp;
        this.newBmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
        this.lines = new ArrayList<Line>();
    }

    public int interpolateColour(Vector2d src, Vector2d dest, double t, Bitmap srcBmp, Bitmap dstBmp){
        int srcColor = srcBmp.getPixel((int)src.getX(),(int)src.getY());
        int dstColor = dstBmp.getPixel((int)dest.getX(),(int)dest.getY());

        int srcR = Color.red(srcColor);
        int srcG = Color.green(srcColor);
        int srcB = Color.blue(srcColor);

        int dstR = Color.red(dstColor);
        int dstG = Color.green(dstColor);
        int dstB = Color.blue(dstColor);


        int interR = (int)(srcR*(1-t)+ dstR*t);
        int interG = (int)(srcG*(1-t)+ dstG*t);
        int interB = (int)(srcB*(1-t)+ dstB*t);

        int interColor = Color.rgb(interR,interG,interB);

        return interColor;

    }

    public void movePixel(int x, int y, Vector2d src){
        int c = this.bmp.getPixel(x,y);
//        Log.i("PixelVal:",c+"");
//        Log.i("X:",x+"");
//        Log.i("Y:",y+"");
//        Log.i("srcX:",src.getX()+"");
//        Log.i("srcY:",src.getY()+"");
        try {
            this.newBmp.setPixel((int) src.getX(), (int) src.getY(), c);
        } catch(IllegalStateException e){
            Log.e("illegalState",e.getMessage());
        }catch(IllegalArgumentException e){
            Log.e("illegalArgument",e.getMessage());
        }
    }

    public void genLines(CirclesDrawingView firstImg, CirclesDrawingView secondImg, int numberOfLines, int numberOfFrames,int frameIndex){

        for(int j = 0; j < numberOfLines;++j){
            Line srcLine = firstImg.getlines().get(j);
            Line dstLine = secondImg.getlines().get(j);

            //first point
            int firstDiffX = dstLine.getStart().centerX - srcLine.getStart().centerX;
            int firstDiffY = dstLine.getStart().centerY - srcLine.getStart().centerY;
            int fframeStepX = firstDiffX/(numberOfFrames+1);
            int fframeStepY = firstDiffY/(numberOfFrames+1);
            int firstPtX = srcLine.getStart().centerX + ((frameIndex+1)*fframeStepX);
            int firstPtY = srcLine.getStart().centerY + ((frameIndex+1)*fframeStepY);
            CircleArea start = new CircleArea(firstPtX, firstPtY, 1);

            //second point
            int secondDiffX = dstLine.getEnd().centerX - srcLine.getEnd().centerX;
            int secondDiffY = dstLine.getEnd().centerY - srcLine.getEnd().centerY;
            int frameStepX = secondDiffX/(numberOfFrames+1);
            int frameStepY = secondDiffY/(numberOfFrames+1);
            int secondPtX = srcLine.getEnd().centerX + ((frameIndex+1)*frameStepX);
            int secondPtY = srcLine.getEnd().centerY + ((frameIndex+1)*frameStepY);
            CircleArea end = new CircleArea(secondPtX, secondPtY, 1);

            //make vector
            Line newLine = new Line(start, end);
//                Log.i("src:","\n"+srcLine.toString());
//                Log.i("dst:",dstLine.toString());
//                Log.i("intermediate:",newLine.toString());
            //add vector to frame class lines
            this.addLine(newLine);
        }
    }



    public void addLine(Line newLine){
        lines.add(newLine);
    }

    @Override
    public String toString(){
        String frame = "";
        for(int i = 0; i < this.lines.size(); ++i){
            frame+="Line "+i+"\n";
            frame+=this.lines.get(0).toString()+"\n";
        }

        return frame;
    }


}
