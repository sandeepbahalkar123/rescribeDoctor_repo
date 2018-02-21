
package com.rescribe.doctor.model.patient.patient_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientHistoryInfo {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("whichDate")
    @Expose
    private int whichDate;
    @SerializedName("opdStatus")
    @Expose
    private String opdStatus;
    @SerializedName("opdId")
    @Expose
    private int opdId;
    @SerializedName("visitDate")
    @Expose
    private String visitDate;
    @SerializedName("opdName")
    @Expose
    private String opdName;

    public String getOpdName() {
        return opdName;
    }

    public void setOpdName(String opdName) {
        this.opdName = opdName;
    }

    private boolean longpressed;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWhichDate() {
        return whichDate;
    }

    public void setWhichDate(int whichDate) {
        this.whichDate = whichDate;
    }

    public String getOpdStatus() {
        return opdStatus;
    }

    public void setOpdStatus(String opdStatus) {
        this.opdStatus = opdStatus;
    }

    public int getOpdId() {
        return opdId;
    }

    public void setOpdId(int opdId) {
        this.opdId = opdId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
    public boolean isLongpressed() {
        return longpressed;
    }

    public void setLongpressed(boolean longpressed) {
        this.longpressed = longpressed;
    }

}
