package com.sdbiosensor.covicatch.network.models;

import java.util.ArrayList;

public class GetHistoryResponseModel {

    private String message;
    private String status;
    private ArrayList<DataModel> data;

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

    public ArrayList<DataModel> getData() {
        return data;
    }

    public void setData(ArrayList<DataModel> data) {
        this.data = data;
    }

    public class DataModel {
        private String kitSerialNumber;
        private String id;
        private String createdDate;
        private String resultStatus;

        public String getKitSerialNumber() {
            return kitSerialNumber;
        }

        public void setKitSerialNumber(String kitSerialNumber) {
            this.kitSerialNumber = kitSerialNumber;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getResultStatus() {
            return resultStatus;
        }

        public void setResultStatus(String resultStatus) {
            this.resultStatus = resultStatus;
        }
    }
}
