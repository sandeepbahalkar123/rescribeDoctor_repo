package com.rescribe.doctor.model.add_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class ComplaintModel implements CustomResponse {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("compDays")
    @Expose
    private int compDays;
    @SerializedName("compMonth")
    @Expose
    private int compMonth;
    @SerializedName("compYear")
    @Expose
    private int compYear;
    @SerializedName("complaintId")
    @Expose
    private int complaintId;
    @SerializedName("isDeleted")
    @Expose
    private int isDeleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompDays() {
        return compDays;
    }

    public void setCompDays(int compDays) {
        this.compDays = compDays;
    }

    public int getCompMonth() {
        return compMonth;
    }

    public void setCompMonth(int compMonth) {
        this.compMonth = compMonth;
    }

    public int getCompYear() {
        return compYear;
    }

    public void setCompYear(int compYear) {
        this.compYear = compYear;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

}