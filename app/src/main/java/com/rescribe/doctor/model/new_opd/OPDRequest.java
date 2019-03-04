package com.rescribe.doctor.model.new_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OPDRequest {

    @SerializedName("prescription")
    @Expose
    private List<Prescription> prescription = new ArrayList<Prescription>();

    public List<Prescription> getPrescription() {
        return prescription;
    }

    public void setPrescription(List<Prescription> prescription) {
        this.prescription = prescription;
    }

}