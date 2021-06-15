package com.sdbiosensor.covicatch.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.events.CloseAllScreens;
import com.sdbiosensor.covicatch.events.CloseLoginScreens;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OptionsActivity extends BaseActivity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        handleClicks();
    }

    private void handleClicks() {
        findViewById(R.id.button_form).setOnClickListener(this);
        findViewById(R.id.button_history).setOnClickListener(this);
        findViewById(R.id.button_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_form) {
            openFormActivity();
        } else if (view.getId() == R.id.button_history) {
            openHistoryActivity();
        } else if (view.getId() == R.id.button_logout) {
            confirmLogout();
        }
    }

    private void openFormActivity() {
        startActivity(new Intent(OptionsActivity.this, AgreementActivity.class));
        finish();
    }

    private void openHistoryActivity() {
        showDialog("To be implemented");
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void logout() {
        SharedPrefUtils.getInstance(this).resetAll();
        EventBus.getDefault().post(new CloseAllScreens());
        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
    }

}