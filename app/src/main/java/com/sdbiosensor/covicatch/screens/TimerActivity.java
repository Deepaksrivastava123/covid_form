package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;

import java.util.Calendar;

public class TimerActivity extends BaseActivity implements View.OnClickListener {

    public static final int TIMER_INTERVAL = 1;     //In minutes
    private TextView text_timer;
    private Button button_scan_qr;
    private Calendar savedCalendar;
    private Calendar currentCalendar;
    private boolean isTimerUp = false;
    private CountDownTimer mainCountdownTimer;

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
        button_scan_qr = findViewById(R.id.button_scan_qr);
    }

    private void handleClicks() {
        button_scan_qr.setOnClickListener(this);
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
            button_scan_qr.setTextColor(getResources().getColor(R.color.app_blue));
        } else {
            button_scan_qr.setTextColor(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_scan_qr) {
            if (isTimerUp) {
                scanQr();
            } else {
                showErrorDialog(getString(R.string.wait_for_timer));
            }
        }
    }

    private void scanQr() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
               showErrorDialog("Cancelled");
            } else {
                Intent intent= new Intent(TimerActivity.this, TakePhotoActivity.class);
                intent.putExtra("qr", result.getContents());
                startActivity(intent);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}