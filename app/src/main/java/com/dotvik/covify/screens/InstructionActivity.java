package com.dotvik.covify.screens;

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
import com.dotvik.covify.R;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;
import com.dotvik.covify.network.models.LocalDataModel;
import com.dotvik.covify.screens.instructionpager.PagerFragment;
import com.dotvik.covify.utils.SharedPrefUtils;

import net.alexandroid.utils.indicators.IndicatorsView;

import java.util.Calendar;

public class InstructionActivity extends BaseActivity {

    //    private AlarmManager alarmManager;
//    private PendingIntent alarmIntent;
    private static int NUM_ITEMS = 6;
    private ViewPager mViewPager;
    private int currentItem = 1;

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
                    mViewPager.setCurrentItem(currentItem, true);
                    currentItem ++;
                    if (currentItem != NUM_ITEMS) {
                        autoMove();
                    }
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
                    return PagerFragment.newInstance(R.layout.fragment_instruction_1);
                case 1:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_2);
                case 2:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_3);
                case 3:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_4);
                case 4:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_5);
                case 5:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_6);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}