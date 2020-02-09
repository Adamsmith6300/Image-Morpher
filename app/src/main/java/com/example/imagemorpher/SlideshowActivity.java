package com.example.imagemorpher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlideshowActivity extends AppCompatActivity {

    private int index = 0;
    private int totalFrames = 10;
    private SeekBar seekbar;
    private ArrayList<String> paths;
    private ArrayList<Bitmap> images;
    private ImageView slides;
    private String imageName;
    private String time;
    private static DecimalFormat df = new DecimalFormat("#.###");
    private ScheduledExecutorService scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        configureBackButton();
        Intent i = getIntent();
        imageName = i.getStringExtra("imageName");
        totalFrames = i.getIntExtra("numberOfFrames", 5);
        time = df.format(i.getDoubleExtra("time", 0.0));

        setTitle("Image Morpher - Results: " + time + "x faster than 1 thread");
        images = new ArrayList<Bitmap>(totalFrames);
        setBitmaps("/storage/emulated/0/"+imageName, totalFrames);
        slides = findViewById(R.id.slides);
        slides.setImageBitmap(images.get(0));

        seekbar = (SeekBar) findViewById(R.id.seekBar2);
        seekbar.setMax(totalFrames + 1);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                index = progress;
                slides.setImageBitmap(images.get(index));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button saveBtn = findViewById(R.id.saveResults);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProject();
            }
        });

        Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAnimation();
            }
        });

        Button stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduler.shutdown();
            }
        });


    }

    private void playAnimation(){
        scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        index++;
                        if(index <= images.size()){
                            seekbar.setProgress(index);
                        } else {
                            index = 0;
                        }
                    }
                }, 0, 70, TimeUnit.MILLISECONDS);
    }


    private void saveProject(){
        try {
            FileOutputStream fout = openFileOutput("savedProjects.txt", MODE_APPEND);
            fout.write((imageName+" ").getBytes());
            fout.write(Integer.toString(totalFrames).getBytes());
            fout.write("\n".getBytes());
            Toast.makeText(getApplicationContext(), "Project Saved", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    private void setBitmaps(String pathname, int numberOfFrames){
        for(int i = 0; i < numberOfFrames+2; i++){
            File imgFile = new File(pathname+i+".jpg");
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                images.add(myBitmap);
            }
        }
    }

    private void configureBackButton(){
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }

}
