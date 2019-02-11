package com.rescribe.doctor.model.patient.add_new_patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.patient.doctor_patients.PatientAddressDetails;
import com.rescribe.doctor.model.patient.doctor_patients.PatientReferenceDetails;

public class PatientDetail implements CustomResponse {

    @SerializedName("patientAge")
    @Expose
    private String patientAge;
    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("patientPhone")
    @Expose
    private String patientPhone;
    @SerializedName("patientLname")
    @Expose
    private String patientLname;
    @SerializedName("patientFname")
    @Expose
    private String patientFname;
    @SerializedName("salutation")
    @Expose
    private int salutation;
    @SerializedName("patientGender")
    @Expose
    private String patientGender;
    @SerializedName("patientDob")
    @Expose
    private String patientDob;
    @SerializedName("patientMname")
    @Expose
    private String patientMname;
    @SerializedName("referenceId")
    @Expose
    private String referenceId;
    @SerializedName("mobilePatientId")
    @Expose
    private int mobilePatientId;
    @SerializedName("relation")
    @Expose
    private String relation;
    @SerializedName("panNumber")
    @Expose
    private String panNumber;
    @SerializedName("adharNumber")
    @Expose
    private String adharNumber;
    @SerializedName("bloodGroup")
    @Expose
    private int bloodGroup;
    @SerializedName("patientAltNumber")
    @Expose
    private String patientAltNumber;
    @SerializedName("registerFor")
    @Expose
    private String registerFor;
    @SerializedName("patientEmailId")
    @Expose
    private String patientEmailId;
    @SerializedName("referedDetails")
    @Expose
    private PatientReferenceDetails referedDetails;
    @SerializedName("patientAddressDetails")
    @Expose
    private PatientAddressDetails patientAddressDetails;

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientLname() {
        return patientLname;
    }

    public void setPatientLname(String patientLname) {
        this.patientLname = patientLname;
    }

    public String getPatientFname() {
        return patientFname;
    }

    public void setPatientFname(String patientFname) {
        this.patientFname = patientFname;
    }

    public int getSalutation() {
        return salutation;
    }

    public void setSalutation(int salutation) {
        this.salutation = salutation;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientDob() {
        return patientDob;
    }

    public void setPatientDob(String patientDob) {
        this.patientDob = patientDob;
    }

    public String getPatientMname() {
        return patientMname;
    }

    public void setPatientMname(String patientMname) {
        this.patientMname = patientMname;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public int getMobilePatientId() {
        return mobilePatientId;
    }

    public void setMobilePatientId(int mobilePatientId) {
        this.mobilePatientId = mobilePatientId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAdharNumber() {
        return adharNumber;
    }

    public void setAdharNumber(String adharNumber) {
        this.adharNumber = adharNumber;
    }

    public int getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(int bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPatientAltNumber() {
        return patientAltNumber;
    }

    public void setPatientAltNumber(String patientAltNumber) {
        this.patientAltNumber = patientAltNumber;
    }

    public String getRegisterFor() {
        return registerFor;
    }

    public void setRegisterFor(String registerFor) {
        this.registerFor = registerFor;
    }

    public String getPatientEmailId() {
        return patientEmailId;
    }

    public void setPatientEmailId(String patientEmailId) {
        this.patientEmailId = patientEmailId;
    }

    public PatientReferenceDetails getReferedDetails() {
        return referedDetails;
    }

    public void setReferedDetails(PatientReferenceDetails referedDetails) {
        this.referedDetails = referedDetails;
    }

    public PatientAddressDetails getPatientAddressDetails() {
        return patientAddressDetails;
    }

    public void setPatientAddressDetails(PatientAddressDetails patientAddressDetails) {
        this.patientAddressDetails = patientAddressDetails;
    }

}