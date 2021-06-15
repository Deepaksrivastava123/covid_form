package com.sdbiosensor.covicatch.screens;

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
import com.sdbiosensor.covicatch.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText edit_mobile;
    private View progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        handleClicks();
    }

    private void initView() {
        edit_mobile = findViewById(R.id.edit_mobile_number);
        progress = findViewById(R.id.progress);
    }


    private void handleClicks() {
        findViewById(R.id.button_next).setOnClickListener(this);
        findViewById(R.id.text_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        if (view.getId() == R.id.button_next) {
            sendOtp();
        } else if (view.getId() == R.id.text_register) {
            moveToRegisterScreen();
        }
    }

    private void sendOtp() {
        String mobileNo = edit_mobile.getText().toString();

        if (mobileNo.isEmpty() || mobileNo.length() < 10) {
            edit_mobile.setText("");
            showErrorDialog(getString(R.string.error_valid_mobile_number));
            return;
        }
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).sendOtp(mobileNo).enqueue(new Callback<CreatePatientResponseModel>() {
                @Override
                public void onResponse(Call<CreatePatientResponseModel> call, Response<CreatePatientResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        moveToOtpScreen(response, mobileNo);
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
                            edit_mobile.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                            edit_mobile.setText("");
                            showErrorDialog(response.errorBody().toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CreatePatientResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    edit_mobile.setText("");
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void moveToOtpScreen(Response<CreatePatientResponseModel> response, String mobileNo) {
        Intent intent = new Intent(this, LoginOtpActivity.class);
        intent.putExtra("mobile", mobileNo);
        startActivity(intent);
    }

    private void moveToRegisterScreen() {
        //TODO
        showDialog("To be implemented");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CloseLoginScreens event) {
        finish();
    }

}