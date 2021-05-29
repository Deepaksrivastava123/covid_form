package com.sdbiosensor.covicatch.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        try {
            if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }catch (Exception e) {}
    }

    public static String getCsvFromArrayList(ArrayList<String> list) {
        String str = "";
        if (list != null &&list.size() > 0) {
            for (String item : list) {
                str += item + ", ";
            }
        }
        return str.substring(0, str.lastIndexOf(","));
    }

    public static long secondsBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toSeconds(Math.abs(end - start));
    }

    public static String convertMillisToMS(long millis) {
        try {
            return String.format("%02d:%02d",
                    //Minutes
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    //Seconds
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
