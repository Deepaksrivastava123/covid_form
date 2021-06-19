package com.sdbiosensor.covicatch.network.models;

public class LoginResponseModel {

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
