
package com.rescribe.doctor.model.patient.doctor_patients;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientReferenceDetails implements  CustomResponse {

    private String referredTypeId;
    private String patientId;
    private String docId;
    private String name;
    private String emailId;
    private String phoneNumber;

    public PatientReferenceDetails() {
    }

    public String getReferredTypeId() {
        return referredTypeId;
    }

    public void setReferredTypeId(String referredTypeId) {
        this.referredTypeId = referredTypeId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
