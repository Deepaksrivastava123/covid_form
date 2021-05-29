package com.sdbiosensor.covicatch.screens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;

import java.util.Calendar;

public class TimerActivity extends BaseActivity implements View.OnClickListener {

    public static final int TIMER_INTERVAL = 1;
    private TextView text_timer;
    private Button button_take_picture;
    private Calendar savedCalendar;
    private Calendar currentCalendar;
    private boolean isTimerUp = false;
    private CountDownTimer mainCountdownTimer;
    private int PERMISSION_ALL = 1, CAMERA = 1001;
    private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        initViews();
        handleClicks();
        loadTimer();
    }

    private void initViews() {
        text_timer = findViewById(R.id.text_timer);
        button_take_picture = findViewById(R.id.button_take_picture);
    }

    private void handleClicks() {
        button_take_picture.setOnClickListener(this);
    }

    private void loadTimer() {
        currentCalendar = Calendar.getInstance();
        savedCalendar = Calendar.getInstance();

        long savedTime = SharedPrefUtils.getInstance(this).getLong(Constants.PREF_TIMER_ALARM_TIME, -1);
        savedCalendar.setTimeInMillis(savedTime);

        long timePassed = savedCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        long seconds = Utils.secondsBetween(savedCalendar, currentCalendar);
        if (timePassed > 0) {
            isTimerUp = false;
            startTimer(seconds);
        } else {
            isTimerUp = true;
            text_timer.setText("00:00");
        }

        handleViewBasedOnTimer();
    }

    private void startTimer(long seconds) {
        mainCountdownTimer = new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                text_timer.setText(Utils.convertMillisToMS(millisUntilFinished));
            }

            public void onFinish() {
                text_timer.setText("00:00");
                isTimerUp = true;
                handleViewBasedOnTimer();
            }

        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mainCountdownTimer != null) {
            mainCountdownTimer.cancel();
            mainCountdownTimer = null;
        }
    }

    private void handleViewBasedOnTimer() {
        if (isTimerUp) {
            button_take_picture.setTextColor(getResources().getColor(R.color.app_blue));
        } else {
            button_take_picture.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_take_picture) {
            if (isTimerUp) {
                if (!hasPermissions(this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                } else {
                    clickPhoto();
                }
            } else {
                showErrorDialog(getString(R.string.wait_for_timer));
            }
        }
    }

    private void clickPhoto() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            Bitmap clickedPhoto = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(this, PleaseWaitActivity.class);
            intent.putExtra("photo", clickedPhoto);
            startActivity(intent);
            finish();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}