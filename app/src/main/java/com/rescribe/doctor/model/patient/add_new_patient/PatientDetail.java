package com.rescribe.doctor.model.patient.add_new_patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.patient.doctor_patients.PatientReferenceDetails;

public class PatientDetail implements CustomResponse {

    @SerializedName("salutation")
    @Expose
    private int salutation;
    @SerializedName("mobilePatientId")
    @Expose
    private int mobilePatientId;
    @SerializedName("patientFname")
    @Expose
    private String patientFname;
    @SerializedName("patientMname")
    @Expose
    private String patientMname;
    @SerializedName("patientLname")
    @Expose
    private String patientLname;
    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("patientAge")
    @Expose
    private String patientAge;
    @SerializedName("patientDob")
    @Expose
    private String patientDob;
    @SerializedName("patientPhone")
    @Expose
    private String patientPhone;
    @SerializedName("patientGender")
    @Expose
    private String patientGender;
    @SerializedName("referenceId")
    @Expose
    private String offlineReferenceID;

    //-------fields for adding patient in offline mode--------

    private String patientState;
    private String patientAddress;

    @SerializedName("referedDetails")
    @Expose
    private PatientReferenceDetails referedDetails;
    //---------------


    public int getMobilePatientId() {
        return mobilePatientId;
    }

    public void setMobilePatientId(int mobilePatientId) {
        this.mobilePatientId = mobilePatientId;
    }

    public String getPatientFname() {
        return patientFname;
    }

    public void setPatientFname(String patientFname) {
        this.patientFname = patientFname;
    }

    public String getPatientMname() {
        return patientMname;
    }

    public void setPatientMname(String patientMname) {
        this.patientMname = patientMname;
    }

    public String getPatientLname() {
        return patientLname;
    }

    public void setPatientLname(String patientLname) {
        this.patientLname = patientLname;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientDob() {
        return patientDob;
    }

    public void setPatientDob(String patientDob) {
        this.patientDob = patientDob;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getOfflineReferenceID() {
        return offlineReferenceID;
    }

    public void setOfflineReferenceID(String offlineReferenceID) {
        this.offlineReferenceID = offlineReferenceID;
    }

    public String getPatientState() {
        return patientState;
    }

    public void setPatientState(String patientState) {
        this.patientState = patientState;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public PatientReferenceDetails getReferedDetails() {
        return referedDetails;
    }

    public void setReferedDetails(PatientReferenceDetails referedDetails) {
        this.referedDetails = referedDetails;
    }

    public int getSalutation() {
        return salutation;
    }

    public void setSalutation(int salutation) {
        this.salutation = salutation;
    }
}