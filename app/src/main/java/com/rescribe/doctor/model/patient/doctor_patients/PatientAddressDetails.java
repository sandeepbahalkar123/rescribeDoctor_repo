
package com.rescribe.doctor.model.patient.doctor_patients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientAddressDetails implements CustomResponse {

    @SerializedName("patientAddress")
    @Expose
    private String patientAddress;
    @SerializedName("patientAddress2")
    @Expose
    private String patientAddress2;
    @SerializedName("patientArea")
    @Expose
    private String patientArea;
    @SerializedName("patientCity")
    @Expose
    private String patientCity;
    @SerializedName("patientState")
    @Expose
    private String patientState;
    @SerializedName("pinCode")
    @Expose
    private String pinCode;

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientAddress2() {
        return patientAddress2;
    }

    public void setPatientAddress2(String patientAddress2) {
        this.patientAddress2 = patientAddress2;
    }

    public String getPatientArea() {
        return patientArea;
    }

    public void setPatientArea(String patientArea) {
        this.patientArea = patientArea;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }

    public String getPatientState() {
        return patientState;
    }

    public void setPatientState(String patientState) {
        this.patientState = patientState;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

}