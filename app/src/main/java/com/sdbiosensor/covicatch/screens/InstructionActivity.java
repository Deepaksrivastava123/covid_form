package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.network.models.LocalDataModel;
import com.sdbiosensor.covicatch.screens.instructionpager.PagerFragment;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

import net.alexandroid.utils.indicators.IndicatorsView;

import java.util.Calendar;

public class InstructionActivity extends BaseActivity {

    //    private AlarmManager alarmManager;
//    private PendingIntent alarmIntent;
    private static int NUM_ITEMS = 2;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        initView();
        handleClicks();
    }

    private void initView() {
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        IndicatorsView mIndicatorsView = findViewById(R.id.indicatorsView);
        mIndicatorsView.setViewPager(mViewPager);
        mIndicatorsView.setSmoothTransition(true);
        mIndicatorsView.setIndicatorsClickChangePage(true);

        try {
            String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
            LocalDataModel localDataModel = new Gson().fromJson(tempString, LocalDataModel.class);
            ((TextView) findViewById(R.id.text_serial_number)).setText(getString(R.string.serial_number) + " " + localDataModel.getQrCode());
        }catch (Exception e) {
            e.printStackTrace();
        }

        autoMove();
    }

    private void autoMove() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (InstructionActivity.this != null &&
                        mViewPager != null &&
                        mViewPager.getAdapter() != null) {
                    mViewPager.setCurrentItem(1, true);
                }
            }
        }, 1500);
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

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                    return PagerFragment.newInstance();
                case 0:
                    return PagerFragment.newInstance(R.string.instruction_1, R.drawable.img_instructions_1);
                case 1:
                    return PagerFragment.newInstance(R.string.instruction_2, R.drawable.img_instructions_2);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}