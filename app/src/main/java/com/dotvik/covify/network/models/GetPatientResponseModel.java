package com.dotvik.covify.network.models;

public class GetPatientResponseModel {

    private String message;
    private String status;
    private CreatePatientRequestModel data;

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

    public CreatePatientRequestModel getData() {
        return data;
    }

    public void setData(CreatePatientRequestModel data) {
        this.data = data;
    }
}
