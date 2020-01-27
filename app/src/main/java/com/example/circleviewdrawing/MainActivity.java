package com.example.circleviewdrawing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private CirclesDrawingView firstImg;
    private CirclesDrawingView secondImg;

    private int numberOfFrames = 5;
    private ArrayList<Frame> outputFrames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        verifyStoragePermissions(this);

        Bitmap cat = BitmapFactory.decodeResource(getResources(), R.drawable.trump);
        Bitmap dog = BitmapFactory.decodeResource(getResources(), R.drawable.monkey);
        firstImg = findViewById(R.id.firstImage);
        firstImg.setmBitmap(cat);
        secondImg = findViewById(R.id.secondImage);
        secondImg.setmBitmap(dog);
//        Line srcL = new Line(new CircleArea(1,40,1), new CircleArea(5,1,1));
//        Line dstL = new Line(new CircleArea(5,16,1), new CircleArea(1,20,1));
//        Line srcL2 = new Line(new CircleArea(8,1,1), new CircleArea(40,40,1));
//        Line dstL2 = new Line(new CircleArea(5,30,1), new CircleArea(15,35,1));
//        ArrayList<Line> srcLines = new ArrayList<>(1);
//        srcLines.add(srcL);
//        srcLines.add(srcL2);
//        ArrayList<Line> dstLines = new ArrayList<>(1);
//        dstLines.add(dstL);
//        dstLines.add(dstL2);
//
//        double p = 0,a = 0.01f,b = 1.0f;
//        Vector2d src = new Vector2d();
//        src = warp(10,10,srcLines , dstLines, p, a, b, src);

    }

    public void beginMorph(android.view.View view){
//        EditText numberOfFramesText   = (EditText)findViewById(R.id.editText);
//        if (!"".equals(numberOfFramesText)){
//            numberOfFrames=Integer.parseInt(numberOfFramesText.getText().toString());
//        }
        int w = secondImg.getmBitmap().getWidth();
        int h = secondImg.getmBitmap().getHeight();
        ArrayList<Line> srcLines = firstImg.getlines();
        ArrayList<Line> dstLines = secondImg.getlines();
        int numberOfLines = srcLines.size();
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;

        //create list of frames
        outputFrames = new ArrayList<Frame>(numberOfFrames);

        Bitmap firstImgBmp = firstImg.getmBitmap();
        Bitmap secondImgBmp = secondImg.getmBitmap();
        //setup lines for each frame
//        for(int i = 0; i < numberOfFrames;++i){
//            Log.i("frame",i+"");
//            Frame newFrame = new Frame(firstImgBmp,numberOfLines, firstImg.getlines());
//
//            Bitmap newBmp = Bitmap.createBitmap(w, h, conf);
//            int [] pixels = new int[w * h];
//            firstImgBmp.getPixels(pixels, 0, w, 0, 0, w, h);
//
////            Bitmap rightBmp = Bitmap.createBitmap(w, h, conf);
////            int [] rightPixels = new int[w * h];
////            secondImgBmp.getPixels(rightPixels, 0, w, 0, 0, w, h);
//
//            //calc each line by interpolation
//            newFrame.genLines(firstImg, secondImg, numberOfLines, numberOfFrames, i);
//            Vector2d src = new Vector2d(), dest = new Vector2d();
//
////            Log.i("srcLines",srcLines.toString());
////            Log.i("interLines",newFrame.getLines().toString());
////            Log.i("dstLines",dstLines.toString());
//
////            double t = (i+1.0)/(numberOfFrames+1.0);
////            Log.i("T:",t+"");
//
//            //calc position of each new pixel
//            for(int x = 0; x < w; ++x){
//                for(int y = 0; y < h;++y){
//
//                    double p = 0,a = 0.01f,b = 2.0f;
//                    src = warp(x,y, srcLines, newFrame.getLines(), p, a, b, src);
//                    dest = warp(x,y, dstLines, newFrame.getLines(), p, a, b, dest);
//
//                    if (src.getX() < 0)
//                        src.setX(0);
//                    if (src.getX() > w-1)
//                        src.setX(w - 1);
//                    if (src.getY() < 0)
//                        src.setY(0);
//                    if (src.getY() > h-1)
//                        src.setY(h - 1);
//                    if (dest.getX() < 0)
//                        dest.setX(0);
//                    if (dest.getX() > w-1)
//                        dest.setX(w - 1);
//                    if (dest.getY() < 0)
//                        dest.setY(0);
//                    if (dest.getY() > h-1)
//                        dest.setY(h-1);
//
////                    pixels[(w*y)+x] = firstImgBmp.getPixel((int)src.getX(),(int)src.getY());
//                    pixels[(w*y)+x] = newFrame.interpolateColour(src, dest, (i+1.0)/(numberOfFrames+1.0), firstImgBmp, secondImgBmp);
//
//                }
//            }
//            newBmp.setPixels(pixels, 0, w, 0, 0, w, h);
//            //secondImg.setmBitmap(newBmp);
//            //outputFrames.add(newFrame);
//
//            try {
//                String path =  Environment.getExternalStorageDirectory().getAbsolutePath();
//                OutputStream fOut = null;
//                Log.i("OUTPUT", path);
//                File file = new File(path, "gbch"+i+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
//                fOut = new FileOutputStream(file);
//
//                //Bitmap pictureBitmap = newFrame.getNewBmp().copy(Bitmap.Config.ARGB_8888, true); // obtaining the Bitmap
//                newBmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
//                fOut.flush(); // Not really required
//                fOut.close(); // do not forget to close the stream
//
//                MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        Intent i = new Intent(MainActivity.this,SlideshowActivity.class);
//        i.putParcelableArrayListExtra("frames", outputFrames);
        startActivity(i);

    }

    public Vector2d warp(int x, int y, ArrayList<Line> interLines, ArrayList<Line> srcLines, double p, double a, double b, Vector2d src){

        double normAbs, interLen,interLenAbs, srcLength;
        double weight, weightSum, dist, d;
        double sum_x, sum_y;
        double flen, f;
        Vector2d pd = new Vector2d(), dp = new Vector2d(), pq = new Vector2d(), qd = new Vector2d(), norm = new Vector2d(), nrmonrm = new Vector2d();
        double X, Y;
        sum_x = 0;
        sum_y = 0;
        weightSum = 0;


        for(int k = 0; k < interLines.size(); ++k){
//            Log.i("DST:","PART ONE");
            pd.setX(srcLines.get(k).getStart().centerX - x);
            pd.setY(srcLines.get(k).getStart().centerY - y);
//            Log.i("TP1", pd.getX()+","+pd.getY());

            dp.setX(x - srcLines.get(k).getStart().centerX);
            dp.setY(y - srcLines.get(k).getStart().centerY);
//            Log.i("P1T", dp.getX()+","+dp.getY());

            pq.setX(srcLines.get(k).getEnd().centerX - srcLines.get(k).getStart().centerX);
            pq.setY(srcLines.get(k).getEnd().centerY - srcLines.get(k).getStart().centerY);
//            Log.i("P1Q1", pq.getX()+","+pq.getY());

            interLen = pq.getX() * pq.getX() + pq.getY() * pq.getY();
            interLenAbs = Math.sqrt(interLen);
//            Log.i("|P1Q1|", interLenAbs+"");

            norm.setX(pq.getY()*-1);
            norm.setY(pq.getX());
            normAbs = Math.sqrt(norm.getX()*norm.getX()+norm.getY()*norm.getY());
//            Log.i("Nrm", norm.getX()+","+norm.getY());
//            Log.i("NrmAbs", normAbs+"");

//            Log.i("dotPRod", dotProd(pd,norm)+"");
            d = dotProd(pd,norm)/normAbs;
//            Log.i("d", d+"");
            flen = (dp.getX() * pq.getX() + dp.getY() * pq.getY()) / interLenAbs;
//            Log.i("flen", flen+"");

            f = (flen/interLenAbs);



//            Log.i("SRC:","PART TWO");

            pq.setX(interLines.get(k).getEnd().centerX - interLines.get(k).getStart().centerX);
            pq.setY(interLines.get(k).getEnd().centerY - interLines.get(k).getStart().centerY);
//            Log.i("P1Q1", pq.getX()+","+pq.getY());

            norm = calcNorm(pq);
//            Log.i("nrm", norm.getX()+","+norm.getY());

            nrmonrm = calcNrmONrm(norm, calcVectorLen(norm));
//            Log.i("TEST", calcVectorLen(norm)+"");
//            Log.i("nrmonrm", nrmonrm.getX()+","+nrmonrm.getY());


            X = interLines.get(k).getStart().centerX + (pq.getX()*f);
//            Log.i("fpX", X+"");
            Y = interLines.get(k).getStart().centerY + (pq.getY()*f);
//            Log.i("fpY", Y+"");


            X = X - (d*nrmonrm.getX());
//            Log.i("srcX", X+"");
            Y = Y - (d*nrmonrm.getY());
//            Log.i("srcY", Y+"");
//            src.setX(X);
//            src.setY(Y);

            //get percent along line
            //if f < 0
            //  return d = distP1
            //if f > 1
            //  return d = distP2
            //else
            //  return dist to line
            if(f < 0){
                qd.setX(srcLines.get(k).getStart().centerX);
                qd.setY(srcLines.get(k).getStart().centerY);
                d = distanceBetweenPts(new Vector2d(x,y), qd);
            }
            if(f > 1){
                qd.setX(srcLines.get(k).getEnd().centerX);
                qd.setY(srcLines.get(k).getEnd().centerY);
                d = distanceBetweenPts(new Vector2d(x,y), qd);
            }

            double wNum = Math.pow(interLen, p);
            double wDenom = a + Math.abs(d);
            weight = Math.pow((wNum / wDenom), b);
            sum_x += (X - x) * weight;
            sum_y += (Y - y) * weight;
            weightSum += weight;
//            Log.i("weight", weight+"");
//            Log.i("deltaX", sum_x+"");
//            Log.i("deltaY", sum_y+"");
//            Log.i("weightsum", weightSum+"");
        }
        src.setX(x + (sum_x / weightSum));
        src.setY(y + (sum_y / weightSum));
//        Log.i("srcX",  src.getX()+"");
//        Log.i("srcY", src.getY()+"");

        return src;
    }


    public double distanceBetweenPts(Vector2d pt, Vector2d linept){
        double x = Math.pow((pt.getX() - linept.getX()),2);
        double y = Math.pow((pt.getY() - linept.getY()),2);
        return Math.sqrt(x+y);
    }

    public double dotProd(Vector2d a, Vector2d b){
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    public Vector2d calcNorm(Vector2d slope){
        Vector2d nrm = new Vector2d(slope.getY()*-1,slope.getX());
        return nrm;
    }

    public Vector2d calcNrmONrm(Vector2d nrm, double len){
        return new Vector2d(nrm.getX()/len,nrm.getY()/len);
    }

    public double calcVectorLen(Vector2d a){
        return Math.sqrt(a.getX() * a.getX() + a.getY() * a.getY());
    }

    public void clearAll(android.view.View v){
        firstImg.clearLines();
        secondImg.clearLines();
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



}
