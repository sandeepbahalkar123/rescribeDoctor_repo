package com.rescribe.doctor.model.add_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SelectedOpedCommon  {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("compDays")
    @Expose
    private String compDays;

    @SerializedName("compMonth")
    @Expose
    private String compMonth;

    @SerializedName("compYear")
    @Expose
    private String compYear;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompDays() {
        return compDays;
    }

    public void setCompDays(String compDays) {
        this.compDays = compDays;
    }

    public String getCompMonth() {
        return compMonth;
    }

    public void setCompMonth(String compMonth) {
        this.compMonth = compMonth;
    }

    public String getCompYear() {
        return compYear;
    }

    public void setCompYear(String compYear) {
        this.compYear = compYear;
    }
}
