package com.sdbiosensor.covicatch.screens;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.receiver.AlarmReceiver;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

import java.util.Calendar;

public class InstructionActivity extends BaseActivity {

//    private AlarmManager alarmManager;
//    private PendingIntent alarmIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        handleClicks();
    }

    private void handleClicks() {
        findViewById(R.id.button_start_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimer();
                startActivity(new Intent(InstructionActivity.this, TimerActivity.class));
                finish();
            }
        });
    }

    private void setTimer() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, TimerActivity.TIMER_INTERVAL);
        SharedPrefUtils.getInstance(InstructionActivity.this).putLong(Constants.PREF_TIMER_ALARM_TIME, calendar.getTimeInMillis());

        //TODO open app from background
//        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + TimerActivity.TIMER_INTERVAL * 60 * 1000, alarmIntent);
    }
}