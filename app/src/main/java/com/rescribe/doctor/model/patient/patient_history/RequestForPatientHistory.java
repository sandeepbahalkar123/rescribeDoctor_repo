package com.rescribe.doctor.model.patient.patient_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

/**
 * Created by jeetal on 27/2/18.
 */

public class RequestForPatientHistory implements CustomResponse {
    @SerializedName("docId")
    @Expose
    private Integer docId;
    @SerializedName("patientId")
    @Expose
    private Integer patientId;
    @SerializedName("year")
    @Expose
    private String year;


    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }


}
