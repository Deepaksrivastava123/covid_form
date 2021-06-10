package com.sdbiosensor.covicatch.screens;

import android.os.Bundle;

import com.google.gson.Gson;
import com.sdbiosensor.covicatch.R;
import com.sdbiosensor.covicatch.customcomoponents.BaseActivity;
import com.sdbiosensor.covicatch.network.models.CreatePatientResponseModel;

public class ReportActivity extends BaseActivity {

    private CreatePatientResponseModel responseModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initView();
    }

    private void initView() {
        try {
            responseModel = new Gson().fromJson(getIntent().getStringExtra("response"), CreatePatientResponseModel.class);
            showDialog(responseModel.getStatus() + "\n" + responseModel.getMessage() + "\n" + responseModel.getData());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}