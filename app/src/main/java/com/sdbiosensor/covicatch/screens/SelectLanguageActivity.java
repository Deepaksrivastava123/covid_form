package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

public class SelectLanguageActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        handleClicks();
    }

    private void handleClicks() {
        findViewById(R.id.button_english).setOnClickListener(this);
        findViewById(R.id.button_hindi).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_english) {
            SharedPrefUtils.getInstance(this).putString(Constants.PREF_LANG, Constants.LANGUAGES.en.name());
        } else {
            SharedPrefUtils.getInstance(this).putString(Constants.PREF_LANG, Constants.LANGUAGES.hi.name());
        }
        startActivity(new Intent(SelectLanguageActivity.this, OptionsActivity.class));
        finish();
    }

}