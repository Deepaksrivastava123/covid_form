package com.sdbiosensor.covicatch.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.AddressRequestModel;
import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;
import com.sdbiosensor.covicatch.network.models.LocalDataModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PleaseWaitActivity extends BaseActivity {

    private Bitmap imageToUpload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_wait);

        initViews();
        sendFormData();
    }

    private void initViews() {
        imageToUpload = (Bitmap) getIntent().getExtras().get("photo");
    }

    private void sendFormData() {
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).uploadPatientDetails(getFormRequestModel()).enqueue(new Callback<CreatePatientResponseModel>() {
                @Override
                public void onResponse(Call<CreatePatientResponseModel> call, Response<CreatePatientResponseModel> response) {
                    if (response.errorBody() == null) {
                        handleFormResponse(response);
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
                            showErrorDialogWithRetry(finalMessage.toString(), "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorDialogWithRetry(response.errorBody().toString(), "");
                        }
                    }
                }

                @Override
                public void onFailure(Call<CreatePatientResponseModel> call, Throwable t) {
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialogWithRetry(t.getLocalizedMessage(), "");
                }
            });
        }
    }

    private void handleFormResponse(Response<CreatePatientResponseModel> response) {
        if(response.body().getStatus().equalsIgnoreCase("SUCCESS")) {
            sendImageData(response.body().getData());
        } else {
            showErrorDialogWithRetry(response.body().getMessage(), "");
        }
    }

    private void sendImageData(String uniqueId) {
        File file = writeImageToCache(uniqueId);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imageReq = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).uploadPatientImage(imageReq, Constants.CATEGORIES.COVID_TESTING_IMAGE.name(), uniqueId).enqueue(new Callback<CreatePatientResponseModel>() {
                @Override
                public void onResponse(Call<CreatePatientResponseModel> call, Response<CreatePatientResponseModel> response) {
                    if (response.errorBody() == null) {
                        handleImageResponse(response, uniqueId);
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
                            showErrorDialogWithRetry(finalMessage.toString(), uniqueId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showErrorDialogWithRetry(response.errorBody().toString(), uniqueId);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CreatePatientResponseModel> call, Throwable t) {
                    Log.v("Debug", t.getLocalizedMessage());
                    showErrorDialogWithRetry(t.getLocalizedMessage(), uniqueId);
                }
            });
        }
    }

    private void handleImageResponse(Response<CreatePatientResponseModel> response, String uniqueId) {
        if(response.body().getStatus().equalsIgnoreCase("SUCCESS")) {
            SharedPrefUtils.getInstance(PleaseWaitActivity.this).resetAll();
            startActivity(new Intent(PleaseWaitActivity.this, ReportActivity.class));
            //TODO can send patient info in intent if required
            finish();
        } else {
            showErrorDialogWithRetry(response.body().getMessage(), uniqueId);
        }
    }

    private CreatePatientRequestModel getFormRequestModel() {
        String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
        LocalDataModel localDataModel = new Gson().fromJson(tempString, LocalDataModel.class);

        CreatePatientRequestModel model = new CreatePatientRequestModel();
        AddressRequestModel addressModel = new AddressRequestModel();

        addressModel.setAddress1(localDataModel.getAddress());
        addressModel.setAddress2("");
        addressModel.setAddress3("");
        addressModel.setAddressType("");
        addressModel.setCity(localDataModel.getCity());
        addressModel.setCountry("INDIA");
        addressModel.setLocality("");
        addressModel.setPinCode(localDataModel.getPincode());
        addressModel.setState(localDataModel.getState());

        //TODO change in api to send card no and card type instead of aadhaar no
        model.setAddress(addressModel);
        model.setAadharNo(localDataModel.getId_no());
        model.setAge(0);
        model.setCity(localDataModel.getCity());
        model.setCollectedBy("");
        model.setDeviceId("");
        model.setDeviceOS("ANDROID");
        model.setFirstName(localDataModel.getFirstName());
        model.setGender(localDataModel.getGender());
        model.setIcmrReference("");
        model.setLastName(localDataModel.getLastName());
        model.setMailId("");
        model.setMobileNo(localDataModel.getMobile());
        model.setPinCode(localDataModel.getPincode());
        model.setRemarks("");
        model.setResult("");
        model.setState(localDataModel.getState());

        ArrayList<String> symptomList = localDataModel.getSymptoms();
        if (symptomList.contains("Others")) {
            symptomList.add(localDataModel.getOtherSymptoms());
        }
        model.setSymptoms(symptomList);

        ArrayList<String> conditionsList = localDataModel.getConditions();
        if (conditionsList.contains("Others")) {
            conditionsList.add(localDataModel.getOtherConditions());
        }
        model.setUnderlyingMedicalCondition(conditionsList);

        model.setSymtomStatus("");
        model.setUploadedImageRef("");

        return model;
    }

    private File writeImageToCache(String uniqueId) {
        File file = new File(getCacheDir(), "IMG_" + uniqueId + ".jpeg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageToUpload.compress(Bitmap.CompressFormat.JPEG, 0, bos);
        byte[] bitmapData = bos.toByteArray();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    @Override
    public void onBackPressed() {
        showErrorDialog(getString(R.string.cannot_go_back));
    }

    public void showErrorDialogWithRetry(String message, String uniqueId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (uniqueId.isEmpty()) {
                    sendImageData(uniqueId);
                } else {
                    sendFormData();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel_and_start_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPrefUtils.getInstance(PleaseWaitActivity.this).resetAll();
                startActivity(new Intent(PleaseWaitActivity.this, SplashActivity.class));
                finish();
            }
        });
        builder.create().show();
    }
}