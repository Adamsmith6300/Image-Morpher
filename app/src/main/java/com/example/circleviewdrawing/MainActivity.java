package com.example.circleviewdrawing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private CirclesDrawingView firstImg;
    private CirclesDrawingView secondImg;

    private int numberOfFrames = 1;


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

    public void beginMorph(android.view.View v){
        
    }

    public void clearAll(android.view.View v){
        firstImg.clearLines();
        secondImg.clearLines();
    }
}
