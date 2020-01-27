package com.example.imagemorpher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class SlideshowActivity extends AppCompatActivity {

    private int index = 0;
    private int totalFrames = 20;
    private SeekBar seekbar;
    private ArrayList<String> paths;
    private ArrayList<Bitmap> images;
    private ImageView slides;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        configureBackButton();


        images = new ArrayList<Bitmap>();
        setBitmaps("/storage/emulated/0/trump", totalFrames);
        slides = findViewById(R.id.slides);
        slides.setImageBitmap(images.get(0));

        seekbar = (SeekBar) findViewById(R.id.seekBar2);
        seekbar.setMax(totalFrames - 1);

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



    }


    private void setBitmaps(String pathname, int numberOfFrames){
        for(int i = 0; i < numberOfFrames; i++){
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
