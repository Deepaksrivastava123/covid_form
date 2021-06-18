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
import com.sdbiosensor.covicatch.network.models.GenericResponseModel;
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
import org.json.JSONArray;
import org.json.JSONObject;

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

        sendOtp();
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
            ApiClient.getBaseInstance(this).sendOtp(mobileNo).enqueue(new Callback<GenericResponseModel>() {
                @Override
                public void onResponse(Call<GenericResponseModel> call, Response<GenericResponseModel> response) {
                    progress.setVisibility(View.GONE);
                    if (response.errorBody() == null) {
                        showOtpDialog(response, mobileNo);
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
                public void onFailure(Call<GenericResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    edit_mobile.setText("");
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void showOtpDialog(Response<GenericResponseModel> response, String mobileNo) {
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
                edit_mobile.setText("");
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
                            verifyOtp(otp, mobileNo);
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

    private void verifyOtp(String otp, String mobileNo) {
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).verifyOtp(mobileNo, otp).enqueue(new Callback<GenericResponseModel>() {
                @Override
                public void onResponse(Call<GenericResponseModel> call, Response<GenericResponseModel> response) {
                    if (response.errorBody() == null) {
                        handleOtpVerifyResponse(mobileNo, otp, response.body());
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
                public void onFailure(Call<GenericResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleOtpVerifyResponse(String mobileNo, String otp, GenericResponseModel response) {
        if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
            registerUser(mobileNo, otp);
        } else {
            progress.setVisibility(View.GONE);
            showErrorDialog(response.getMessage());
        }
    }

    private void registerUser(String mobileNo, String otp) {
        progress.setVisibility(View.VISIBLE);
        if (ApiClient.getBaseInstance(this) != null) {
            String uniqueID = UUID.randomUUID().toString();

            RegisterRequestModel requestModel = new RegisterRequestModel();
            requestModel.setCountryCode("91");
            requestModel.setUserId(edit_email.getText().toString().trim());
            requestModel.setEmailId(edit_email.getText().toString().trim());
            requestModel.setMobileNumber(edit_mobile.getText().toString().trim());
            requestModel.setName(edit_first_name.getText().toString().trim() + " " + edit_last_name.getText().toString().trim());
            requestModel.setDeviceId(uniqueID);
            requestModel.setDeviceOS("ANDROID");

            ApiClient.getBaseInstance(this).registerUser(requestModel).enqueue(new Callback<RegisterResponseModel>() {
                @Override
                public void onResponse(Call<RegisterResponseModel> call, Response<RegisterResponseModel> response) {
                    if (response.errorBody() == null) {
                        handleRegisterResponse(response.body(), mobileNo, otp);
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
                public void onFailure(Call<RegisterResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    edit_mobile.setText("");
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleRegisterResponse(RegisterResponseModel response, String mobileNo, String otp) {
        if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
           loginUser(mobileNo, otp);
        } else {
            progress.setVisibility(View.GONE);
            showErrorDialog(response.getMessage());
        }
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
                public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                    progress.setVisibility(View.GONE);
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialog(t.getLocalizedMessage());
                }
            });
        }
    }

    private void handleLoginResponse(String mobileNo, LoginResponseModel response) {
        if(response.getStatus().equalsIgnoreCase("SUCCESS")) {
            Intent intent = new Intent(RegisterActivity.this, SelectLanguageActivity.class);
            startActivity(intent);
            SharedPrefUtils.getInstance(this).putBoolean(Constants.PREF_LOGGED_IN, true);
            SharedPrefUtils.getInstance(this).putString(Constants.PREF_LOGGED_IN_TOKEN, response.getToken());

            EventBus.getDefault().post(new CloseLoginScreens());
            finish();
        } else {
            showErrorDialog(response.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CloseLoginScreens event) {
        finish();
    }

}