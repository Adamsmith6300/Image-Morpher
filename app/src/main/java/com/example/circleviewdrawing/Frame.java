package com.example.circleviewdrawing;

import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.ArrayList;

public class Frame {
    public Bitmap getBmp() {
        return bmp;
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
    private ArrayList<Line> lines;

    public Frame(Bitmap bmp, int numberOfLines) {
        this.bmp = bmp;
        lines = new ArrayList<>(numberOfLines);
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
