package com.rescribe.doctor.model.add_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

import java.util.ArrayList;

public class SaveOPDRequestBaseModel implements CustomResponse {

    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("patientId")
    @Expose
    private int patientId;
    @SerializedName("hospitalPatId")
    @Expose
    private int hospitalPatId;
    @SerializedName("locationId")
    @Expose
    private int locationId;
    @SerializedName("hospitalId")
    @Expose
    private int hospitalId;
    @SerializedName("opdDate")
    @Expose
    private String opdDate;
    @SerializedName("opdTime")
    @Expose
    private String opdTime;
    @SerializedName("complaints")
    @Expose
    private ArrayList<ComplaintModel> complaints = null;
    @SerializedName("diagnosis")
    @Expose
    private ArrayList<DiagnosisModel> diagnosis = null;
    @SerializedName("radioDetails")
    @Expose
    private ArrayList<RadioDetailModel> radioDetails = null;
    @SerializedName("labDetails")
    @Expose
    private ArrayList<LabDetailModel> labDetails = null;
    @SerializedName("vitals")
    @Expose
    private ArrayList<VitalModel> vitals = null;
    @SerializedName("prescription")
    @Expose
    private ArrayList<PrescriptionModel> prescription = null;

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(int hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getOpdDate() {
        return opdDate;
    }

    public void setOpdDate(String opdDate) {
        this.opdDate = opdDate;
    }

    public String getOpdTime() {
        return opdTime;
    }

    public void setOpdTime(String opdTime) {
        this.opdTime = opdTime;
    }

    public ArrayList<ComplaintModel> getComplaints() {
        return complaints;
    }

    public void setComplaints(ArrayList<ComplaintModel> complaints) {
        this.complaints = complaints;
    }

    public ArrayList<DiagnosisModel> getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(ArrayList<DiagnosisModel> diagnosis) {
        this.diagnosis = diagnosis;
    }

    public ArrayList<RadioDetailModel> getRadioDetails() {
        return radioDetails;
    }

    public void setRadioDetails(ArrayList<RadioDetailModel> radioDetails) {
        this.radioDetails = radioDetails;
    }

    public ArrayList<LabDetailModel> getLabDetails() {
        return labDetails;
    }

    public void setLabDetails(ArrayList<LabDetailModel> labDetails) {
        this.labDetails = labDetails;
    }

    public ArrayList<VitalModel> getVitals() {
        return vitals;
    }

    public void setVitals(ArrayList<VitalModel> vitals) {
        this.vitals = vitals;
    }

    public ArrayList<PrescriptionModel> getPrescription() {
        return prescription;
    }

    public void setPrescription(ArrayList<PrescriptionModel> prescription) {
        this.prescription = prescription;
    }

}