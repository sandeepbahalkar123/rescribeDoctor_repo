
package com.rescribe.doctor.model.patient.doctor_patients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientReferenceDetails implements CustomResponse {
    @SerializedName("referredTypeId")
    @Expose
    private int referredTypeId;
    @SerializedName("patientId")
    @Expose
    private String patientId;
    @SerializedName("docId")
    @Expose
    private String docId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("salutation")
    @Expose
    private String salutation;
    @SerializedName("description")
    @Expose
    private String description;

    public int getReferredTypeId() {
        return referredTypeId;
    }

    public void setReferredTypeId(int referredTypeId) {
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

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}