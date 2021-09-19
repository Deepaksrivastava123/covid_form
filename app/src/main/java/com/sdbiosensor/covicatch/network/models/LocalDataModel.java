package com.sdbiosensor.covicatch.network.models;

import java.io.Serializable;
import java.util.ArrayList;

public class LocalDataModel implements Serializable {

    private String existingId;
    private String firstName;
    private String lastName;
    private String gender;
    private String mobile;
    private String address;
    private String pincode;
    private String state;
    private String district;
    private String stateId;
    private String districtId;
    private String city;
    private String id_type;
    private String id_no;
    private ArrayList<String> symptoms;
    private ArrayList<String> conditions;
    private String otherSymptoms;
    private String otherConditions;
    private String nationality;
    private String dob;
    private String dose1;
    private String dose2;
    private String occupation;
    private String qrCode;
    private String contactNumberBelongsTo;
    private boolean isVaccinated;
    private boolean isArogyaSetuDownloaded;
    private String vaccineType;
    private ArrayList<String> editableProfileFields;


    public ArrayList<String> getEditableProfileFields() {
        return editableProfileFields;
    }

    public void setEditableProfileFields(ArrayList<String> editableProfileFields) {
        this.editableProfileFields = editableProfileFields;
    }

    public String getExistingId() {
        return existingId;
    }

    public void setExistingId(String existingId) {
        this.existingId = existingId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<String> symptoms) {
        this.symptoms = symptoms;
    }

    public ArrayList<String> getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList<String> conditions) {
        this.conditions = conditions;
    }

    public String getOtherSymptoms() {
        return otherSymptoms;
    }

    public void setOtherSymptoms(String otherSymptoms) {
        this.otherSymptoms = otherSymptoms;
    }

    public String getOtherConditions() {
        return otherConditions;
    }

    public void setOtherConditions(String otherConditions) {
        this.otherConditions = otherConditions;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setContactNumberBelongsTo(String contactNumberBelongsTo) {
        this.contactNumberBelongsTo = contactNumberBelongsTo;
    }

    public String getContactNumberBelongsTo() {
        return contactNumberBelongsTo;
    }

    public boolean isVaccinated() {
        return isVaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        isVaccinated = vaccinated;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    public boolean isArogyaSetuDownloaded() {
        return isArogyaSetuDownloaded;
    }

    public void setArogyaSetuDownloaded(boolean arogyaSetuDownloaded) {
        isArogyaSetuDownloaded = arogyaSetuDownloaded;
    }

    public String getDose1() {
        return dose1;
    }

    public void setDose1(String dose1) {
        this.dose1 = dose1;
    }

    public String getDose2() {
        return dose2;
    }

    public void setDose2(String dose2) {
        this.dose2 = dose2;
    }
}
