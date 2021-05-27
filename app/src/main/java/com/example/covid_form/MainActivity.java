package com.example.covid_form;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logo = (ImageView) findViewById(R.id.logo);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.my_animation);
        logo.startAnimation(myanim);

        new Handler().postDelayed(() -> {

                Intent intent=new Intent(MainActivity.this,SelectLanguageActivity.class);
                startActivity(intent);
                finish();

                }, 5000);

    }
}