package com.sdbiosensor.covicatch.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.events.CloseLoginScreens;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.LoginRequestModel;
import com.sdbiosensor.covicatch.network.models.LoginResponseModel;
import com.sdbiosensor.covicatch.network.models.RegisterRequestModel;
import com.sdbiosensor.covicatch.network.models.RegisterResponseModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;
import com.sdbiosensor.covicatch.utils.ValidationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private EditText edit_mobile, edit_first_name, edit_last_name, edit_email;
    private View progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        handleClicks();
    }

    private void initView() {
        edit_mobile = findViewById(R.id.edit_mobile_number);
        edit_first_name = findViewById(R.id.edit_first_name);
        edit_last_name = findViewById(R.id.edit_last_name);
        edit_email = findViewById(R.id.edit_email);
        progress = findViewById(R.id.progress);
    }


    private void handleClicks() {
        findViewById(R.id.button_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Utils.hideKeyboard(this);
        if (view.getId() == R.id.button_next) {
            validateInputs();
        }
    }

    private void validateInputs() {
        if (!ValidationUtils.blankValidation(edit_first_name) ||
                !ValidationUtils.blankValidation(edit_last_name) ||
                !ValidationUtils.blankValidation(edit_mobile) ||
                !ValidationUtils.emailValidation(edit_email)) {
            return;
        }
        registerUser();
    }

    private void registerUser() {
        String mobileNo = edit_mobile.getText().toString();

        if (mobileNo.isEmpty() || mobileNo.length() < 10) {
            edit_mobile.setText("");
            showErrorDialog(getString(R.string.error_valid_mobile_number));
            return;
        }

        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            String uniqueID = UUID.randomUUID().toString();

            RegisterRequestModel requestModel = new RegisterRequestModel();
            requestModel.setCountryCode("91");
            requestModel.setEmailId(edit_email.getText().toString().trim());
            requestModel.setMobileNumber(edit_mobile.getText().toString().trim());
            requestModel.setName(edit_first_name.getText().toString().trim() + " " + edit_last_name.getText().toString().trim());
            requestModel.setDeviceId(uniqueID);
            requestModel.setDeviceOS("ANDROID");

            ApiClient.getBaseInstance(this).registerUser(requestModel).enqueue(new Callback<RegisterResponseModel>() {
                @Override
                public void onResponse(Call<RegisterResponseModel> call, Response<RegisterResponseModel> response) {
                    if (response.errorBody() == null) {
                        handleRegisterResponse(response.body(), mobileNo);
                    } else {
                        progress.setVisibility(View.GONE);
                        showErrorDialog(getString(R.string.error_server_error));
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleRegisterResponse(RegisterResponseModel response, String mobileNo) {
        if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
            showOtpDialog(mobileNo);
        } else {
            progress.setVisibility(View.GONE);
            showErrorDialog(response.getMessage());
        }
    }

    private void showOtpDialog(String mobileNo) {
        LinearLayout lin = new LinearLayout(this);
        lin.setPadding(50, 0, 50, 0);
        final EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        lin.addView(editText);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.otp_sent);
        builder.setMessage(R.string.otp_sent_message);
        builder.setCancelable(false);
        builder.setView(lin);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progress.setVisibility(View.GONE);
                dialog.cancel();
            }
        });

        final androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            public void onShow(DialogInterface dialogInterface) {

                Button button = ((androidx.appcompat.app.AlertDialog) dialog).getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String otp = editText.getText().toString();
                        if (!otp.trim().isEmpty()) {
                            loginUser(mobileNo, otp);
                            dialog.cancel();
                        } else {
                            editText.setError(getString(R.string.error_text_blank));
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void loginUser(String mobileNo, String otp) {
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
                        handleLoginResponse(mobileNo, response.body());
                    } else {
                        progress.setVisibility(View.GONE);
                        showErrorDialog(getString(R.string.error_server_error));
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

    private void handleLoginResponse(String mobileNo, LoginResponseModel response) {
        try {
            Intent intent = new Intent(RegisterActivity.this, SelectLanguageActivity.class);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CloseLoginScreens event) {
        finish();
    }

}