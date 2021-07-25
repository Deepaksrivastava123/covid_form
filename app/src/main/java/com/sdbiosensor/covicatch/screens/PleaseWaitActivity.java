package com.sdbiosensor.covicatch.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.sdbiosensor.covicatch.BuildConfig;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.constants.Constants;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.network.ApiClient;
import com.sdbiosensor.covicatch.network.models.AddressRequestModel;
import com.sdbiosensor.covicatch.network.models.CreatePatientRequestModel;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;
import com.sdbiosensor.covicatch.network.models.GetPatientResponseModel;
import com.sdbiosensor.covicatch.network.models.LocalDataModel;
import com.sdbiosensor.covicatch.utils.SharedPrefUtils;
import com.sdbiosensor.covicatch.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PleaseWaitActivity extends BaseActivity {

    private String imageToUpload;
    private int getPatientRetryCount = 0;
    private final int RESULT_RETRY_COUNT = 5, RESULT_RETRY_DURATION = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_wait);

        initViews();
        sendFormData();
    }

    private void moveToTempReport(String resultStatus) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PleaseWaitActivity.this, PdfCreatorActivity.class);
                intent.putExtra("photo", imageToUpload);
                intent.putExtra("result", resultStatus);
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
                        showErrorDialogWithRetry(getString(R.string.error_server_error), "");
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
                        showErrorDialogWithRetry(getString(R.string.error_server_error), uniqueId);
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
            getPatientResult(uniqueId);
        } else {
            showErrorDialogWithRetry(response.body().getMessage(), uniqueId);
        }
    }

    private void getPatientResult(String uniqueId) {
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).getPatientById(uniqueId).enqueue(new Callback<GetPatientResponseModel>() {
                @Override
                public void onResponse(Call<GetPatientResponseModel> call, Response<GetPatientResponseModel> response) {
                    if (response.errorBody() == null) {
                        handlePatientResponse(response, uniqueId);
                    } else {
                        if (getPatientRetryCount < RESULT_RETRY_COUNT) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getPatientRetryCount ++;
                                    getPatientResult(uniqueId);
                                }
                            }, RESULT_RETRY_DURATION);
                        } else {
                            showErrorDialogWithRetry(getString(R.string.error_server_error), uniqueId);
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetPatientResponseModel> call, Throwable t) {
                    if (getPatientRetryCount < RESULT_RETRY_COUNT) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getPatientRetryCount ++;
                                getPatientResult(uniqueId);
                            }
                        }, RESULT_RETRY_DURATION);
                    } else {
                        Log.v("Debug", t.getLocalizedMessage());
                        showErrorDialogWithRetry(t.getLocalizedMessage(), uniqueId);
                    }
                }
            });
        }
    }

    private void handlePatientResponse(Response<GetPatientResponseModel> response, String uniqueId) {
        if(response.body().getStatus().equalsIgnoreCase("SUCCESS")) {
            if (response.body().getData() == null ||
                    response.body().getData().getResultStatus() == null ||
                    response.body().getData().getResultStatus().equals("null")) {
                if (getPatientRetryCount < RESULT_RETRY_COUNT) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getPatientRetryCount ++;
                            getPatientResult(uniqueId);
                        }
                    }, RESULT_RETRY_DURATION);
                } else {
                    showErrorDialogWithRetry(response.body().getMessage(), uniqueId);
                }
                return;
            }
            openDownloadUrl(response, uniqueId);
            //moveToTempReport(response.body().getData().getResultStatus());
        } else {
            if (getPatientRetryCount < RESULT_RETRY_COUNT) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPatientRetryCount ++;
                        getPatientResult(uniqueId);
                    }
                }, RESULT_RETRY_DURATION);
            } else {
                showErrorDialogWithRetry(response.body().getMessage(), uniqueId);
            }
        }
    }

    private void openDownloadUrl(Response<GetPatientResponseModel> response, String uniqueId) {
        if (ApiClient.getBaseInstance(this) != null) {
            ApiClient.getBaseInstance(this).downloadFile(Constants.CATEGORIES.TESTING_RESULT_PDF.name(), uniqueId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.errorBody() == null) {
                        handleDownloadResponse(response);
                    } else {
                        showErrorDialogWithRetry(getString(R.string.error_server_error), uniqueId);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showErrorDialogWithRetry(response.body().getMessage(), uniqueId);
                }
            });
        }
    }

    private void handleDownloadResponse(Response<ResponseBody> response) {
        ResponseBody body = response.body();
        File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String name = "COVI-CATCH-" + Utils.getFormattedDateTime(Calendar.getInstance()) + ".pdf";
        File file = new File(downloadsPath, name);

        InputStream in = null;
        FileOutputStream out = null;
        try {
            try {
                in = body.byteStream();
                out = new FileOutputStream(file);
                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                openResultActivity(file.getAbsolutePath(), name);
            }
            catch (IOException e) {
                e.printStackTrace();
                showDialog(getString(R.string.error_server_error));
            }
            finally {
                if (in != null) {
                    in.close();

                }
                if (out != null) {
                    out.flush();
                    out.getFD().sync();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showDialog(getString(R.string.error_server_error));
        }
    }

    private void openResultActivity(String absolutePath, String name) {
        SharedPrefUtils.getInstance(PleaseWaitActivity.this).resetAllWithoutLogout();
        Intent intent = new Intent(PleaseWaitActivity.this, ReportActivity.class);
        intent.putExtra("path", absolutePath);
        intent.putExtra("name", name);
        startActivity(intent);
        finish();
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
        model.setProfileId(localDataModel.getExistingId());
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

        if (localDataModel.getEditableProfileFields() != null && !localDataModel.getEditableProfileFields().isEmpty()) {
            model.setEditableProfileFields(localDataModel.getEditableProfileFields());
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
//        builder.setNegativeButton(R.string.cancel_and_start_again, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                SharedPrefUtils.getInstance(PleaseWaitActivity.this).resetAll();
//                startActivity(new Intent(PleaseWaitActivity.this, SplashActivity.class));
//                finish();
//            }
//        });
        builder.setNegativeButton(R.string.contact_support, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String body = getEmailBody();
                SharedPrefUtils.getInstance(PleaseWaitActivity.this).resetAll();
                composeEmail(Constants.CONTACT_SUPPORT_EMAIL,
                        getString(R.string.app_name) + " : " + BuildConfig.VERSION_NAME,
                        body);
                finish();
            }
        });
        builder.create().show();
    }

    private String getEmailBody() {
        String tempString = SharedPrefUtils.getInstance(this).getString(Constants.PREF_LOCAL_MODEL, "");
        LocalDataModel localDataModel = new Gson().fromJson(tempString, LocalDataModel.class);

        StringBuffer sb = new StringBuffer();
        sb.append("Name:" + localDataModel.getFirstName() + " " + localDataModel.getLastName() + "\n");
        sb.append("Address:" + localDataModel.getAddress() + "\n");
        sb.append("ID Type:" + localDataModel.getId_type() + "\n");
        sb.append("ID Number:" + localDataModel.getId_no() + "\n");
        sb.append("Gender:" + localDataModel.getGender() + "\n");
        sb.append("Mobile:" + localDataModel.getMobile() + "\n");
        sb.append("Kit Serial Number:" + localDataModel.getQrCode() + "\n");
        sb.append("Nationality:" + localDataModel.getNationality() + "\n");
        sb.append("DOB:" + localDataModel.getDob() + "\n");
        sb.append("Occupation:" + localDataModel.getOccupation() + "\n");
        sb.append("Contact Number belongs to:" + localDataModel.getContactNumberBelongsTo() + "\n");
        sb.append("Vaccinated:" + localDataModel.isVaccinated() + "\n");
        if (localDataModel.isVaccinated()) {
            sb.append("Vaccine Received:" + localDataModel.getVaccineType());
        }
        ArrayList<String> symptomList = localDataModel.getSymptoms();
        if (symptomList.contains("Others")) {
            symptomList.add(localDataModel.getOtherSymptoms());
        }
        sb.append("Symptoms:" + symptomList);

        ArrayList<String> conditionsList = localDataModel.getConditions();
        if (conditionsList.contains("Others")) {
            conditionsList.add(localDataModel.getOtherConditions());
        }
        sb.append("Underlying Medical Conditions:" + conditionsList);
        return sb.toString();
    }

    public void composeEmail(String address, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        //TODO intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageToUpload));
        intent.setData(Uri.parse("mailto:"));
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        finish();
    }

}