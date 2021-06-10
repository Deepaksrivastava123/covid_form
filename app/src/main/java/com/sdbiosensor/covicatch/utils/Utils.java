package com.sdbiosensor.covicatch.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
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

    public static String getFormattedDate(Calendar c) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            return df.format(c.getTime());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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


    public static JSONArray stateMasterJson(Context context) {
        JSONArray json = new JSONArray();
        try {
            json = new JSONArray(readFileFromAssets(context, "state_master.json"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONArray stateDistrictJson(Context context) {
        JSONArray json = new JSONArray();
        try {
            json = new JSONArray(readFileFromAssets(context, "state_district.json"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONArray nationalityJson(Context context) {
        JSONArray json = new JSONArray();
        try {
            json = new JSONArray(readFileFromAssets(context, "nationality.json"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json;

    }

    public static String readFileFromAssets(Context context, String fileName) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        InputStream iStream = null;
        try {
            iStream = context.getAssets().open(fileName);
            br = new BufferedReader(new InputStreamReader(iStream));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if(iStream!=null) {
                    iStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
