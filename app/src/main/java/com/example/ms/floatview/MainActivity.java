package com.example.ms.floatview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private FloatView mFloatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatView = new FloatView(this);
        mFloatView.setRecommendClickListener(new FloatView.BackRecommendClickListener() {
            @Override
            public void backRecommendOnClick() {
                mFloatView.removeFloatView();
            }
        });
    }
}
