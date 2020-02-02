package com.example.imagemorpher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private CirclesDrawingView firstImg;
    private Bitmap firstImgBmp;
    private CirclesDrawingView secondImg;
    private Bitmap secondImgBmp;
    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    private int index = 0;
    private int w;
    private int h;
    private String imageName = "morph";
    private EditText imageNameText;

    private int numberOfFrames = 10;
    int numberOfLines;
    private ArrayList<Line> dstLines;
    private ArrayList<Line> srcLines;
    private ArrayList<Frame> outputFrames;

    private ArrayList<SavedProject> projects;

    //perfomance
    long startTime;
    long endTime;
    double duration = 1.0;
    double duration2 = 1.0;

    boolean threadingTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Image Morpher");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        verifyStoragePermissions(this);

        Bitmap cat = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        Bitmap dog = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
        firstImg = findViewById(R.id.firstImage);
        firstImg.setmBitmap(cat);
        firstImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                index = 0;
                selectImage(MainActivity.this);
                v.setOnClickListener(null);
            }
        });
        secondImg = findViewById(R.id.secondImage);
        secondImg.setmBitmap(dog);
        secondImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                index = 1;
                selectImage(MainActivity.this);
                v.setOnClickListener(null);
            }
        });
        imageNameText = (EditText)findViewById(R.id.imageName);
        Switch s = (Switch) findViewById(R.id.switch1);

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    threadingTest = true;
                } else {
                    threadingTest = false;
                }
            }
        });
        loadProjects();
        Button openBtn = findViewById(R.id.openProject);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                for(int i = 0; i < projects.size();++i){
                    popup.getMenu().add(projects.get(i).getImageName());
                }
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.menu_example);
                popup.show();
            }
        });
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        for(SavedProject proj: projects){
            if(proj.getImageName().equalsIgnoreCase(item.getTitle().toString())){
                showSlides(proj.getImageName(),proj.getFrames());
                return  true;
            }
        }
        return false;
    }



    private class MorpherThread implements  Runnable {

        private int i;

        public MorpherThread(int index){
            this.i = index;
        }

        @Override
        public void run() {
            createFrame(i);
        }
    }

    public void beginMorph(android.view.View view){
        //Toast toast = Toast. makeText(getApplicationContext(),"Starting Morph...", Toast.LENGTH_LONG);
        //toast. setMargin(50,50);
        //toast.show();

        EditText numberOfFramesText   = (EditText)findViewById(R.id.editText);
        if (!"".equals(numberOfFramesText)){
            numberOfFrames = Integer.parseInt(numberOfFramesText.getText().toString());
        }

        imageName = imageNameText.getText().toString();
        w = secondImg.getmBitmap().getWidth();
        h = secondImg.getmBitmap().getHeight();
        srcLines = firstImg.getlines();
        dstLines = secondImg.getlines();
        numberOfLines = srcLines.size();
        outputFrames = new ArrayList<Frame>(numberOfFrames);
        firstImgBmp = firstImg.getmBitmap();
        secondImgBmp = secondImg.getmBitmap();

        ArrayList<Thread> morphThreads = new ArrayList<>();

        startTime = System.nanoTime();
        for(int i = 0; i < numberOfFrames;++i){
            Thread frameThread = new Thread(new MorpherThread(i));
            frameThread.start();
            morphThreads.add(frameThread);
        }
        for (Thread thread : morphThreads) {
            try {
                thread.join();
            } catch (InterruptedException inter){
                Log.e("Error", "Can't join thread!");
            }
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime)/1000000000;
        Log.i("TIME",duration+"");
        if(threadingTest){
            startTime = System.nanoTime();
            for(int i = 0; i < numberOfFrames;++i){
                createFrame(i);
            }
            endTime = System.nanoTime();
            duration2 = (endTime - startTime)/1000000000;
        }
        Log.i("TIME",duration2+"");
        try {
            String path =  Environment.getExternalStorageDirectory().getAbsolutePath();
            OutputStream fOut = null;
            Log.i("OUTPUT", path);
            File file = new File(path, imageName+(0)+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            File file2 = new File(path, imageName+(numberOfFrames+1)+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            fOut = new FileOutputStream(file);
            firstImgBmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut = new FileOutputStream(file2);
            secondImgBmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        showSlides(imageName,numberOfFrames);

    }

    public void showSlides(String name, int num){
        Intent i = new Intent(MainActivity.this,SlideshowActivity.class);
        i.putExtra("imageName",name.trim());
        i.putExtra("numberOfFrames",num);
        i.putExtra("time",threadingTest?(duration2/duration):duration);
        Log.i("name",name.trim().length()+"");
        Log.i("num",num+"");
        startActivity(i);
    }


    public void createFrame(int i){
        Log.i("frame",i+"");
        Frame newFrame = new Frame(firstImgBmp,numberOfLines, firstImg.getlines());

        Bitmap newBmp = Bitmap.createBitmap(w, h, conf);
        int [] pixels = new int[w * h];
        firstImgBmp.getPixels(pixels, 0, w, 0, 0, w, h);

        //calc each line by interpolation
        newFrame.genLines(firstImg, secondImg, numberOfLines, numberOfFrames, i);
        Vector2d src = new Vector2d(), dest = new Vector2d();

        //calc position of each new pixel
        for(int x = 0; x < w; ++x){
            for(int y = 0; y < h;++y){

                double p = 0,a = 0.01f,b = 2.0f;
                src = warp(x,y, srcLines, newFrame.getLines(), p, a, b, src);
                dest = warp(x,y, dstLines, newFrame.getLines(), p, a, b, dest);

                if (src.getX() < 0)
                    src.setX(0);
                if (src.getX() > w-1)
                    src.setX(w - 1);
                if (src.getY() < 0)
                    src.setY(0);
                if (src.getY() > h-1)
                    src.setY(h - 1);
                if (dest.getX() < 0)
                    dest.setX(0);
                if (dest.getX() > w-1)
                    dest.setX(w - 1);
                if (dest.getY() < 0)
                    dest.setY(0);
                if (dest.getY() > h-1)
                    dest.setY(h-1);

                pixels[(w*y)+x] = newFrame.interpolateColour(src, dest, (i+1.0)/(numberOfFrames+1.0), firstImgBmp, secondImgBmp);

            }
        }
        newBmp.setPixels(pixels, 0, w, 0, 0, w, h);

        try {
            String path =  Environment.getExternalStorageDirectory().getAbsolutePath();
            OutputStream fOut = null;
            Log.i("OUTPUT", path);
            File file = new File(path, imageName+(i+1)+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            fOut = new FileOutputStream(file);

            newBmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            pd.setX(srcLines.get(k).getStart().centerX - x);
            pd.setY(srcLines.get(k).getStart().centerY - y);

            dp.setX(x - srcLines.get(k).getStart().centerX);
            dp.setY(y - srcLines.get(k).getStart().centerY);

            pq.setX(srcLines.get(k).getEnd().centerX - srcLines.get(k).getStart().centerX);
            pq.setY(srcLines.get(k).getEnd().centerY - srcLines.get(k).getStart().centerY);

            interLen = pq.getX() * pq.getX() + pq.getY() * pq.getY();
            interLenAbs = Math.sqrt(interLen);

            norm.setX(pq.getY()*-1);
            norm.setY(pq.getX());
            normAbs = Math.sqrt(norm.getX()*norm.getX()+norm.getY()*norm.getY());

            d = dotProd(pd,norm)/normAbs;
            flen = (dp.getX() * pq.getX() + dp.getY() * pq.getY()) / interLenAbs;

            f = (flen/interLenAbs);

            pq.setX(interLines.get(k).getEnd().centerX - interLines.get(k).getStart().centerX);
            pq.setY(interLines.get(k).getEnd().centerY - interLines.get(k).getStart().centerY);

            norm = calcNorm(pq);

            nrmonrm = calcNrmONrm(norm, calcVectorLen(norm));

            X = interLines.get(k).getStart().centerX + (pq.getX()*f);
            Y = interLines.get(k).getStart().centerY + (pq.getY()*f);

            X = X - (d*nrmonrm.getX());
            Y = Y - (d*nrmonrm.getY());

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
        }
        src.setX(x + (sum_x / weightSum));
        src.setY(y + (sum_y / weightSum));

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


    private class SavedProject {
        int frames;
        String imageName;

        public int getFrames() {
            return frames;
        }

        public void setFrames(int frames) {
            this.frames = frames;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public SavedProject(String imageName, int frames){
            this.imageName = imageName;
            this.frames = frames;
        }

    }


    private void loadProjects(){

        try {
            FileInputStream fin = openFileInput("savedProjects.txt");
            InputStreamReader inputRead = new InputStreamReader(fin);

            BufferedReader buffRead = new BufferedReader(inputRead);
            StringBuffer stringBuff = new StringBuffer();
            String lines;
            projects = new ArrayList<>();
            while((lines = buffRead.readLine()) != null){
                stringBuff.append(lines+"\n");
                String[] part = lines.split("(?<=\\D)(?=\\d)");
                Log.i("name",part[0]);
                Log.i("frames",part[1]);
                SavedProject proj = new SavedProject(part[0],Integer.parseInt(part[1]));
                projects.add(proj);
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
//                deleteProjects();
    }

    private void deleteProjects(){
        File dir = getFilesDir();
        File file = new File(dir, "savedProjects.txt");
        boolean deleted = file.delete();
    }


    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        if(index == 0){
                            firstImg.setmBitmap(selectedImage);
                            firstImg.setImageLoaded(true);
                        } else {
                            secondImg.setmBitmap(selectedImage);
                            secondImg.setImageLoaded(true);
                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                if(index == 0){
                                    firstImg.setmBitmap(BitmapFactory.decodeFile(picturePath));
                                    firstImg.setImageLoaded(true);
                                } else {
                                    secondImg.setmBitmap(BitmapFactory.decodeFile(picturePath));
                                    secondImg.setImageLoaded(true);
                                }
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
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
