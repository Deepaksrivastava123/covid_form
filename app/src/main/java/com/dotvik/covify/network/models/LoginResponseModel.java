package com.dotvik.covify.network.models;

import java.io.Serializable;

public class LoginResponseModel {

    private String message;
    private String status;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {
        private String token;
        private String perUserProfile;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPerUserProfile() {
            return perUserProfile;
        }

        public void setPerUserProfile(String perUserProfile) {
            this.perUserProfile = perUserProfile;
        }
    }
}
