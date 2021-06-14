package com.sdbiosensor.covicatch.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;

import java.util.Calendar;

public class TimerActivity extends BaseActivity implements View.OnClickListener {

    public static final int TIMER_INTERVAL = 1;     //In minutes
    private TextView text_timer;
    private Button button_take_picture;
    private Calendar savedCalendar;
    private Calendar currentCalendar;
    private boolean isTimerUp = false;
    private CountDownTimer mainCountdownTimer;

    int CAMERA_PERMISSIONS_CODE  = 1001;

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
                clickPhoto();
            } else {
                showErrorDialog(getString(R.string.wait_for_timer));
            }
        }
    }

    private void clickPhoto() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // check again permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA
                                , Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSIONS_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA
                                , Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSIONS_CODE);
                // Grant Permission
            }
        } else {
            ImagePicker.with(this)
                    .compress(2048)
                    .cameraOnly()
                    .crop(1080, 1920)
                    .start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            String currentPhotoPath = data.getData().getPath();
            Intent intent = new Intent(this, PleaseWaitActivity.class);
            intent.putExtra("photo", currentPhotoPath);
            startActivity(intent);
            finish();
        }
    }

}