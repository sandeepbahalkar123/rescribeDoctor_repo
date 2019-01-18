package com.rescribe.doctor.model.my_appointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class RequestAppointmentDeleteModel implements CustomResponse {

    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("aptId")
    @Expose
    private int aptId;

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getAptId() {
        return aptId;
    }

    public void setAptId(int aptId) {
        this.aptId = aptId;
    }

}