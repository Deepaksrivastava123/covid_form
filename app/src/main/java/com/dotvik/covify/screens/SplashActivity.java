package com.dotvik.covify.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.dotvik.covify.R;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;
import com.dotvik.covify.utils.SharedPrefUtils;

public class SplashActivity extends BaseActivity {
    static String TAG = "SplashActivity";
    private boolean IS_ALIVE = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        IS_ALIVE = true;

        initView();
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
        SharedPrefUtils.getInstance(this).putString(Constants.PREF_LANG, Constants.LANGUAGES.en.name());
    }

    private void moveToNextActivity() {
        if (IS_ALIVE) {
            IS_ALIVE = false;

            Intent intent = null;

            Log.d(TAG, "PREF_TIMER_START_TIME = " + SharedPrefUtils.getInstance(this).getLong(Constants.PREF_TIMER_ALARM_TIME, -1));

            if (SharedPrefUtils.getInstance(this).getBoolean(Constants.PREF_LOGGED_IN, false)) {
                intent = new Intent(SplashActivity.this, SelectLanguageActivity.class);
            } else {
                if (SharedPrefUtils.getInstance(this).getLong(Constants.PREF_TIMER_ALARM_TIME, -1) == -1) {
                    intent = new Intent(SplashActivity.this, StartActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, TimerActivity.class);
                }
            }
            startActivity(intent);
            finish();
        }
    }
}