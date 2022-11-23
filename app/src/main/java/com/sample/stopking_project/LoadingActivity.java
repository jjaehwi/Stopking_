package com.sample.stopking_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoadingActivity extends AppCompatActivity {
    private ImageView iv_drink;
    private ImageView iv_smoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        iv_drink = findViewById(R.id.drink_gif);
        iv_smoke = findViewById(R.id.smoke_gif);
        Glide.with(this).load(R.raw.no_drink).into(iv_drink);
        Glide.with(this).load(R.raw.no_smoke1).into(iv_smoke);

        //로딩화면 시작.
        loadingStart();
    }

    private void loadingStart() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}