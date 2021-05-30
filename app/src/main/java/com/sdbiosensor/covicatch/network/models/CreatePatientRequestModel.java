package com.sdbiosensor.covicatch.network.models;

import java.util.ArrayList;

public class CreatePatientRequestModel {

    private String aadharNo;
    private AddressRequestModel address;
    private int age;
    private String city;
    private String collectedBy;
    private String deviceId;
    private String deviceOS;
    private String firstName;
    private String gender;
    private String icmrReference;
    private String ipAddress;
    private String lastName;
    private String mailId;
    private String mobileNo;
    private String pinCode;
    private String remarks;
    private String result;
    private String state;
    private ArrayList<String> symptoms;
    private String symtomStatus;
    private ArrayList<String> underlyingMedicalCondition;
    private String uploadedImageRef;

    public CreatePatientRequestModel() {
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public AddressRequestModel getAddress() {
        return address;
    }

    public void setAddress(AddressRequestModel address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(String collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIcmrReference() {
        return icmrReference;
    }

    public void setIcmrReference(String icmrReference) {
        this.icmrReference = icmrReference;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getSymtomStatus() {
        return symtomStatus;
    }

    public void setSymtomStatus(String symtomStatus) {
        this.symtomStatus = symtomStatus;
    }

    public ArrayList<String> getUnderlyingMedicalCondition() {
        return underlyingMedicalCondition;
    }

    public void setUnderlyingMedicalCondition(ArrayList<String> underlyingMedicalCondition) {
        this.underlyingMedicalCondition = underlyingMedicalCondition;
    }

    public String getUploadedImageRef() {
        return uploadedImageRef;
    }

    public void setUploadedImageRef(String uploadedImageRef) {
        this.uploadedImageRef = uploadedImageRef;
    }
}
