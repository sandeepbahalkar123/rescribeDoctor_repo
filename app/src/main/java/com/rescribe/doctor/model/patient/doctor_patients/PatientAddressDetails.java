
package com.rescribe.doctor.model.patient.doctor_patients;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientAddressDetails implements Parcelable, CustomResponse {
    @SerializedName("patientAddress")
    @Expose
    private String patientAddress;
    @SerializedName("patientAddress2")
    @Expose
    private String patientAddress2;
    @SerializedName("patientArea")
    @Expose
    private String patientArea;
    @SerializedName("patientAreaName")
    @Expose
    private String patientAreaName;
    @SerializedName("patientCity")
    @Expose
    private int patientCity;
    @SerializedName("patientCityName")
    @Expose
    private String patientCityName;
    @SerializedName("patientState")
    @Expose
    private int patientState;
    @SerializedName("patientStateName")
    @Expose
    private String patientStateName;
    @SerializedName("pinCode")
    @Expose
    private String pinCode;
    public final static Parcelable.Creator<PatientAddressDetails> CREATOR = new Creator<PatientAddressDetails>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PatientAddressDetails createFromParcel(Parcel in) {
            return new PatientAddressDetails(in);
        }

        public PatientAddressDetails[] newArray(int size) {
            return (new PatientAddressDetails[size]);
        }

    }
            ;

    protected PatientAddressDetails(Parcel in) {
        this.patientAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.patientAddress2 = ((String) in.readValue((String.class.getClassLoader())));
        this.patientArea = ((String) in.readValue((int.class.getClassLoader())));
        this.patientAreaName = ((String) in.readValue((String.class.getClassLoader())));
        this.patientCity = ((int) in.readValue((int.class.getClassLoader())));
        this.patientCityName = ((String) in.readValue((String.class.getClassLoader())));
        this.patientState = ((int) in.readValue((int.class.getClassLoader())));
        this.patientStateName = ((String) in.readValue((String.class.getClassLoader())));
        this.pinCode = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PatientAddressDetails() {
    }

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

    public String getPatientAreaName() {
        return patientAreaName;
    }

    public void setPatientAreaName(String patientAreaName) {
        this.patientAreaName = patientAreaName;
    }

    public int getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(int patientCity) {
        this.patientCity = patientCity;
    }

    public String getPatientCityName() {
        return patientCityName;
    }

    public void setPatientCityName(String patientCityName) {
        this.patientCityName = patientCityName;
    }

    public int getPatientState() {
        return patientState;
    }

    public void setPatientState(int patientState) {
        this.patientState = patientState;
    }

    public String getPatientStateName() {
        return patientStateName;
    }

    public void setPatientStateName(String patientStateName) {
        this.patientStateName = patientStateName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(patientAddress);
        dest.writeValue(patientAddress2);
        dest.writeValue(patientArea);
        dest.writeValue(patientAreaName);
        dest.writeValue(patientCity);
        dest.writeValue(patientCityName);
        dest.writeValue(patientState);
        dest.writeValue(patientStateName);
        dest.writeValue(pinCode);
    }

    public int describeContents() {
        return 0;
    }

}