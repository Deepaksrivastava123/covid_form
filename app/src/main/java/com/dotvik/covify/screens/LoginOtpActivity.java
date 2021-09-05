package com.dotvik.covify.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.dotvik.covify.R;
import com.dotvik.covify.constants.Constants;
import com.dotvik.covify.customcomoponents.BaseActivity;
import com.dotvik.covify.events.CloseLoginScreens;
import com.dotvik.covify.network.ApiClient;
import com.dotvik.covify.network.models.LoginRequestModel;
import com.dotvik.covify.network.models.LoginResponseModel;
import com.dotvik.covify.utils.SharedPrefUtils;
import com.dotvik.covify.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginOtpActivity extends BaseActivity implements View.OnClickListener{

    private EditText edit_otp;
    private View progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        initView();
        handleClicks();
    }

    private void initView() {
        edit_otp = findViewById(R.id.edit_otp);
        progress = findViewById(R.id.progress);
    }


    private void handleClicks() {
        findViewById(R.id.button_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        if (view.getId() == R.id.button_next) {
            verifyOtp();
        }
    }

    private void verifyOtp() {
        String mobileNo = getIntent().getStringExtra("mobile");
        String otp = edit_otp.getText().toString();

        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {

            LoginRequestModel requestModel = new LoginRequestModel();
            requestModel.setUsername(mobileNo);
            requestModel.setPasswordAsOtp(true);
            requestModel.setPassword(otp);

            ApiClient.getBaseInstance(this).loginUser(requestModel).enqueue(new Callback<LoginResponseModel>() {
                @Override
                public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        handleOtpVerifyResponse(mobileNo, response.body());
                    } else {
                        if (response.code() == 401) {
                            showErrorDialog(getString(R.string.error_otp_incorrect));
                        }else {
                            showErrorDialog(getString(R.string.error_server_error));
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleOtpVerifyResponse(String mobileNo, LoginResponseModel responseBody) {
        try {
            if (responseBody == null ||
                    responseBody.getStatus() == null ||
                    !responseBody.getStatus().equals("SUCCESS")) {
                showFinishDialog(responseBody.getMessage());
                return;
            }

            LoginResponseModel.Data response = responseBody.getData();
            Intent intent = new Intent(LoginOtpActivity.this, SelectLanguageActivity.class);
            startActivity(intent);
            SharedPrefUtils.getInstance(this).putString(Constants.PREF_LOGGED_IN_ID, mobileNo);
            SharedPrefUtils.getInstance(this).putBoolean(Constants.PREF_LOGGED_IN, true);
            SharedPrefUtils.getInstance(this).putString(Constants.PREF_LOGGED_IN_TOKEN, response.getToken());
            SharedPrefUtils.getInstance(this).putString(Constants.PREF_PROFILE_THRESHOLD, response.getPerUserProfile());

            EventBus.getDefault().post(new CloseLoginScreens());
            finish();
        } catch (Exception e){
            showErrorDialog(getString(R.string.error_server_error));
        }
    }

    private void showFinishDialog(String message) {
        if (message == null || message.isEmpty()) {
            message = getString(R.string.error_server_error);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }

}