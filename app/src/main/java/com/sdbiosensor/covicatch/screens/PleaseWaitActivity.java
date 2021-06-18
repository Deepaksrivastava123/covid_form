package com.sdbiosensor.covicatch.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

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

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PleaseWaitActivity extends BaseActivity {

    private String imageToUpload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_wait);

        initViews();
        sendFormData();
    }

    private void moveToTempReport() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PleaseWaitActivity.this, PdfCreatorActivity.class);
                intent.putExtra("photo", imageToUpload);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void initViews() {
        imageToUpload = getIntent().getExtras().getString("photo");

        try {
            String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
            LocalDataModel localDataModel = new Gson().fromJson(tempString, LocalDataModel.class);
            ((TextView) findViewById(R.id.text_serial_number)).setText(getString(R.string.serial_number) + " " + localDataModel.getQrCode());
        }catch (Exception e) {
            e.printStackTrace();
        }
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
        File file = new File(imageToUpload);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imageReq = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).uploadPatientImage(imageReq, Constants.CATEGORIES.COVID_TESTING_IMAGE.name(), uniqueId).enqueue(new Callback<CreatePatientResponseModel>() {
                @Override
                public void onResponse(Call<CreatePatientResponseModel> call, Response<CreatePatientResponseModel> response) {
                    if (response.errorBody() == null) {
                        Log.v("Debug", "Image Uniqe ID: " + uniqueId);
                        Log.v("Debug", "Response: " + new Gson().toJson(response.body()));
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
            moveToTempReport();
            //TODO delete image file
            //TODO move to actual report screen
//            String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
//            SharedPrefUtils.getInstance(PleaseWaitActivity.this).resetAllWithoutLogout();
//            Intent intent = new Intent(PleaseWaitActivity.this, ReportActivity.class);
//            intent.putExtra("response", new Gson().toJson(response.body()));
//            intent.putExtra("data", tempString);
//            startActivity(intent);
//            finish();
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
        addressModel.setCity(localDataModel.getDistrict());
        addressModel.setCountry("INDIA");
        addressModel.setLocality("");
        addressModel.setPinCode(localDataModel.getPincode());
        addressModel.setState(localDataModel.getState());

        model.setAddress(addressModel);
        model.setUserIdNo(localDataModel.getId_no());
        model.setIdType(localDataModel.getId_type());
        model.setAge(0);
        model.setCity(localDataModel.getCity());
        model.setCollectedBy("");
        model.setFirstName(localDataModel.getFirstName());
        model.setGender(localDataModel.getGender());
        model.setIcmrReference("");
        model.setLastName(localDataModel.getLastName());
        model.setMailId("");
        model.setMobileNo(localDataModel.getMobile());
        model.setPinCode(localDataModel.getPincode());
        model.setRemarks("");
        model.setResult("");
        model.setId(localDataModel.getExistingId());
        model.setState(localDataModel.getState());
        model.setStateCode(localDataModel.getStateId());
        model.setKitSerialNumber(localDataModel.getQrCode());
        model.setDistrict(localDataModel.getDistrict());
        model.setDistrictCode(localDataModel.getDistrictId());
        model.setNationality(localDataModel.getNationality());
        model.setDob(localDataModel.getDob());
        model.setOccupation(localDataModel.getOccupation());
        model.setContactNumberBelongsTo(localDataModel.getContactNumberBelongsTo());
        model.setVaccineReceived(localDataModel.isVaccinated());
        if (localDataModel.isVaccinated()) {
            model.setVaccineType(localDataModel.getVaccineType());
        }

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

        return model;
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
                if (!uniqueId.isEmpty()) {
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