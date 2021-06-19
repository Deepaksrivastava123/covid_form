package com.sdbiosensor.covicatch.network.models;

import java.util.ArrayList;

public class GetProfileResponseModel {

    private String message;
    private String status;
    private ArrayList<CreatePatientRequestModel> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<CreatePatientRequestModel> getData() {
        return data;
    }

    public void setData(ArrayList<CreatePatientRequestModel> data) {
        this.data = data;
    }
}
