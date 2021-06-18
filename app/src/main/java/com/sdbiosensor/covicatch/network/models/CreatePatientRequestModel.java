package com.sdbiosensor.covicatch.network.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CreatePatientRequestModel implements Serializable {

    private String id;
    private String idType;
    private String userIdNo;
    private AddressRequestModel address;
    private int age;
    private String city;
    private String collectedBy;
    private String firstName;
    private String gender;
    private String icmrReference;
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
    private String kitSerialNumber;
    private String dob;
    private String nationality;
    private String district;
    private String occupation;
    private String districtCode;
    private String stateCode;
    private String contactNumberBelongsTo;
    private boolean isVaccineReceived;
    private String vaccineType;

    public CreatePatientRequestModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getUserIdNo() {
        return userIdNo;
    }

    public void setUserIdNo(String userIdNo) {
        this.userIdNo = userIdNo;
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

    public String getKitSerialNumber() {
        return kitSerialNumber;
    }

    public void setKitSerialNumber(String kitSerialNumber) {
        this.kitSerialNumber = kitSerialNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getContactNumberBelongsTo() {
        return contactNumberBelongsTo;
    }

    public void setContactNumberBelongsTo(String contactNumberBelongsTo) {
        this.contactNumberBelongsTo = contactNumberBelongsTo;
    }

    public boolean isVaccineReceived() {
        return isVaccineReceived;
    }

    public void setVaccineReceived(boolean vaccineReceived) {
        isVaccineReceived = vaccineReceived;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }
}
