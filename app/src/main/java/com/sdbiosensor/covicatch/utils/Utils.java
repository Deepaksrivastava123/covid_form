package com.sdbiosensor.covicatch.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

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

}
