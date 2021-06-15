package com.sdbiosensor.covicatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.events.CloseLoginScreens;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

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
            ApiClient.getBaseInstance(this).verifyOtp(mobileNo, otp).enqueue(new Callback<CreatePatientResponseModel>() {
                @Override
                public void onResponse(Call<CreatePatientResponseModel> call, Response<CreatePatientResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        handleOtpVerifyResponse(mobileNo, response.body());
                    } else {
                        try {
                            JSONObject errorJSON = new JSONObject(response.errorBody().string());
                            JSONArray errorArray = errorJSON.optJSONArray("errors");

                            StringBuffer finalMessage = new StringBuffer();
                            if (errorArray != null && errorArray.length() > 0) {
                                for (int i = 0; i < errorArray.length(); i++) {
                                    if (i == 0) {
                                        finalMessage.append(errorArray.getString(i));
                                    } else {
                                        finalMessage.append("\n" + errorArray.getString(i));
                                    }
                                }
                            }
                            showErrorDialog(finalMessage.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorDialog(response.errorBody().toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CreatePatientResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleOtpVerifyResponse(String mobileNo, CreatePatientResponseModel response) {
        if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
            Intent intent = new Intent(LoginOtpActivity.this, SelectLanguageActivity.class);
            startActivity(intent);
            SharedPrefUtils.getInstance(this).putBoolean(Constants.PREF_LOGGED_IN, true);
            //TODO SharedPrefUtils.getInstance(this).putString(Constants.PREF_LOGGED_IN_TOKEN, "asdafsfd");

            EventBus.getDefault().post(new CloseLoginScreens());
            finish();
        } else {
            showErrorDialog(response.getMessage());
        }
    }

}