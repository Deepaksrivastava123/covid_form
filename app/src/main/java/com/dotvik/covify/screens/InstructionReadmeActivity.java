package com.dotvik.covify.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dotvik.covify.R;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;
import com.dotvik.covify.network.models.LocalDataModel;
import com.dotvik.covify.screens.instructionpager.PagerFragment;
import com.dotvik.covify.utils.SharedPrefUtils;
import com.google.gson.Gson;

import net.alexandroid.utils.indicators.IndicatorsView;

import java.util.Calendar;

public class InstructionReadmeActivity extends BaseActivity {

    private static int NUM_ITEMS = 6;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_readme);

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
    }

    private void handleClicks() {
        findViewById(R.id.text_see_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/2EgeSFbgYpQ")));
            }
        });
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
                    return PagerFragment.newInstance(R.layout.fragment_instruction_readme_1);
                case 1:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_readme_2);
                case 2:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_readme_3);
                case 3:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_readme_4);
                case 4:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_readme_5);
                case 5:
                    return PagerFragment.newInstance(R.layout.fragment_instruction_readme_6);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}