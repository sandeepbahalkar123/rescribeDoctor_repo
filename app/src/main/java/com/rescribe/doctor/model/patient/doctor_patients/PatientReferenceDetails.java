
package com.rescribe.doctor.model.patient.doctor_patients;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientReferenceDetails implements  CustomResponse {

    private int referredTypeId;
    private int patientId;
    private int docId;
    private String name;
    private String emailId;
    private int phoneNumber;

    public PatientReferenceDetails() {
    }

    public int getReferredTypeId() {
        return referredTypeId;
    }

    public void setReferredTypeId(int referredTypeId) {
        this.referredTypeId = referredTypeId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
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

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
