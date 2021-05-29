package com.sdbiosensor.covicatch.customcomoponents;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.events.CloseAllScreens;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

public class BaseActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setScreenLanguage();
    }

    private void setScreenLanguage() {
        setLanguage(new Locale(SharedPrefUtils.getInstance(this).getString(Constants.PREF_LANG, Constants.LANGUAGES.en.name())));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CloseAllScreens event) {
        finish();
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

}
