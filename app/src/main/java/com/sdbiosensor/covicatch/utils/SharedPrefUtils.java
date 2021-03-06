package com.sdbiosensor.covicatch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sdbiosensor.covicatch.constants.Constants;

import java.util.Iterator;
import java.util.Set;

public class SharedPrefUtils {

    private SharedPreferences preferences = null;

    private static SharedPrefUtils instance = null;

    private SharedPrefUtils(Context context) {
        preferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefUtils(context);
        }
        return instance;
    }

    public void putInt(String key, int value) {
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void putLong(String key, long value) {
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, int defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public  void putString(String key, String value) {
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void putBoolean(String key, Boolean value) {
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void putStringSet(String key, Set<String> value) {
        Editor editor = preferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return preferences.getStringSet(key, defaultValue);
    }

    public void reset(String key) {
        preferences.edit().remove(key).apply();
    }

    public void resetAll() {
        preferences.edit().clear().apply();
    }

    public void resetAllWithoutLogout() {
        Iterator<String> itr = preferences.getAll().keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            if (key.equals(Constants.PREF_LOGGED_IN) ||
                    key.equals(Constants.PREF_LOGGED_IN_TOKEN)){
                //Do not do anything
            } else {
                preferences.edit().remove(key).apply();
            }
        }
    }

}