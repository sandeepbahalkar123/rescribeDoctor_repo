package com.rescribe.doctor.model.patient.add_new_patient;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyncPatientsRequest {

    @SerializedName("docId")
    @Expose
    private String docId;
    @SerializedName("patientDetails")
    @Expose
    private List<PatientDetail> patientDetails = null;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public List<PatientDetail> getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(List<PatientDetail> patientDetails) {
        this.patientDetails = patientDetails;
    }
}