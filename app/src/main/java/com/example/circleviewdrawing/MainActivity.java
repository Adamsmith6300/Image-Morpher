package com.example.circleviewdrawing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public void clearAll(android.view.View v){
        CirclesDrawingView firstImg = findViewById(R.id.firstImage);
        CirclesDrawingView secondImg = findViewById(R.id.secondImage);
        firstImg.clearLines();
        secondImg.clearLines();
    }
}
