package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

public class SplashActivity extends BaseActivity {
    private ImageView img_logo, img_bottom_logo;

    private boolean IS_ALIVE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        IS_ALIVE = true;

        initView();
        startAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToNextActivity();
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IS_ALIVE = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        IS_ALIVE = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IS_ALIVE = false;
    }

    private void initView() {
        img_logo = (ImageView) findViewById(R.id.img_logo);
        img_bottom_logo = (ImageView) findViewById(R.id.img_bottom_logo);

        SharedPrefUtils.getInstance(this).putString(Constants.PREF_LANG, Constants.LANGUAGES.en.name());
    }

    private void startAnimation() {
        Animation fadeAnim = AnimationUtils.loadAnimation(this, R.anim.animation_fade);
        img_logo.startAnimation(fadeAnim);
        img_bottom_logo.startAnimation(fadeAnim);
    }

    private void moveToNextActivity() {
        if (IS_ALIVE) {
            IS_ALIVE = false;
            //TODO move to timer screen if obj exists and timer in progress
            Intent intent = new Intent(SplashActivity.this, SelectLanguageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}