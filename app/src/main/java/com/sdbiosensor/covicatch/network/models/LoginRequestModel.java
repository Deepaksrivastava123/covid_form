package com.sdbiosensor.covicatch.network.models;

public class LoginRequestModel {

    private String password;
    private boolean passwordAsOtp;
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordAsOtp() {
        return passwordAsOtp;
    }

    public void setPasswordAsOtp(boolean passwordAsOtp) {
        this.passwordAsOtp = passwordAsOtp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
