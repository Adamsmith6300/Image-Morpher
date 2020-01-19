package com.example.circleviewdrawing;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private CirclesDrawingView firstImg;
    private CirclesDrawingView secondImg;

    private int numberOfFrames = 1;
    private ArrayList<Frame> outputFrames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firstImg = findViewById(R.id.firstImage);
        secondImg = findViewById(R.id.secondImage);

//        firstImg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                    Log.i("CHANGEFOCUS",v.toString());
//                if(!hasFocus){
//
//                }
//            }
//        });
//        secondImg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                    Log.i("CHANGEFOCUS",v.toString());
//                if(!hasFocus){
//                  //  v.resetLastTouched();
//                }
//            }
//        });

    }

//    public void deleteLine(android.view.View v){
//        (CirclesDrawingView) v.deleteLastTouched();
//    }

    public void beginMorph(android.view.View view){
        int w = secondImg.getmBitmap().getWidth(), h = secondImg.getmBitmap().getHeight();
        ArrayList<Line> srcLines = secondImg.getlines();
        int numberOfLines = srcLines.size();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;


        //create list of frames
        outputFrames = new ArrayList<Frame>(numberOfFrames);
        //setup lines for each frame
        for(int i = 0; i < numberOfFrames;++i){
            Bitmap bmp = Bitmap.createBitmap(w, h, conf);
            Frame newFrame = new Frame(bmp,numberOfLines);
            //calc each line by interpolation
            for(int j = 0; j < numberOfLines;++j){
                Line srcLine = firstImg.getlines().get(j);
                Line dstLine = srcLines.get(j);

                //first point
                int firstDiffX = dstLine.getStart().centerX - srcLine.getStart().centerX;
                int firstDiffY = dstLine.getStart().centerY - srcLine.getStart().centerY;
                int fframeStepX = firstDiffX/(numberOfFrames+1);
                int fframeStepY = firstDiffY/(numberOfFrames+1);
                int firstPtX = srcLine.getStart().centerX + ((i+1)*fframeStepX);
                int firstPtY = srcLine.getStart().centerY + ((i+1)*fframeStepY);
                CircleArea start = new CircleArea(firstPtX, firstPtY, 1);

                //second point
                int secondDiffX = dstLine.getEnd().centerX - srcLine.getEnd().centerX;
                int secondDiffY = dstLine.getEnd().centerY - srcLine.getEnd().centerY;
                int frameStepX = secondDiffX/(numberOfFrames+1);
                int frameStepY = secondDiffY/(numberOfFrames+1);
                int secondPtX = srcLine.getEnd().centerX + ((i+1)*frameStepX);
                int secondPtY = srcLine.getEnd().centerY + ((i+1)*frameStepY);
                CircleArea end = new CircleArea(secondPtX, secondPtY, 1);

                //make vector
                Line newLine = new Line(start, end);
//                Log.i("src:","\n"+srcLine.toString());
//                Log.i("dst:",dstLine.toString());
//                Log.i("intermediate:",newLine.toString());
                //add vector to frame class lines
                newFrame.addLine(newLine);
            }


            Vector2d dest = new Vector2d(), src = new Vector2d();
            //calc position of each new pixel
            for(int x = 0; x < w; ++x){
                for(int y = 0; y < h;++y){
                    double interLength, srcLength;
                    double weight, weightSum, dist;
                    double sum_x, sum_y;
                    double u, v;
                    Vector2d pd = new Vector2d(), pq = new Vector2d(), qd = new Vector2d();
                    double X, Y;
                    double p = 0,a = 0.01f,b = 2.0f;
                    sum_x = 0;
                    sum_y = 0;
                    weightSum = 0;

                    for(int k = 0; k < srcLines.size(); ++k){

                        pd.setX(x - newFrame.getLines().get(k).getStart().centerX);
                        pd.setY(y - newFrame.getLines().get(k).getStart().centerY);

                        pq.setX(newFrame.getLines().get(k).getEnd().centerX - newFrame.getLines().get(k).getStart().centerX);
                        pq.setY(newFrame.getLines().get(k).getEnd().centerY - newFrame.getLines().get(k).getStart().centerY);

                        interLength = pq.getX() * pq.getX() + pq.getY() * pq.getY();
                        u = (pd.getX() * pq.getX() + pd.getY() * pq.getY()) / interLength;

                        interLength = Math.sqrt(interLength);
                        v = (pd.getX() * pq.getY() - pd.getY() * pq.getX());

                        pq.setX(srcLines.get(k).getEnd().centerX - srcLines.get(k).getStart().centerX);
                        pq.setY(srcLines.get(k).getEnd().centerY - srcLines.get(k).getStart().centerY);

                        srcLength = Math.sqrt(pq.getX() * pq.getX() + pq.getY() * pq.getY());
                        X = srcLines.get(k).getStart().centerX + u * pq.getX() + v * pq.getY() / srcLength;
                        Y = srcLines.get(k).getStart().centerY + u * pq.getY() - v * pq.getX() / srcLength;

                        if (u < 0)
                            dist = Math.sqrt(pd.getX() * pd.getX() + pd.getY() * pd.getY());
                        else if (u > 1) {
                            qd.setX(x - newFrame.getLines().get(k).getEnd().centerX);
                            qd.setY(y - newFrame.getLines().get(k).getEnd().centerY);
                            dist = Math.sqrt(qd.getX() * qd.getX() + qd.getY() * qd.getY());
                        }else{
                            dist = Math.abs(v);
                        }


                        weight = Math.pow(Math.pow(interLength, p) / (a + dist), b);
                        sum_x += X * weight;
                        sum_y += Y * weight;
                        weightSum += weight;
                    }
                    src.setX(sum_x / weightSum);
                    src.setY(sum_y / weightSum);

                }



            }



            outputFrames.add(newFrame);
        }

    }

    public double calcU(int x, int y,Frame newFrame){
        double u = 0.0f;

        return u;
    }

    public void clearAll(android.view.View v){
        firstImg.clearLines();
        secondImg.clearLines();
    }
}
