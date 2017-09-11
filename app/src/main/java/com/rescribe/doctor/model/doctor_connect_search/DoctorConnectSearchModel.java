
package com.rescribe.doctor.model.doctor_connect_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;


public class DoctorConnectSearchModel implements CustomResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("speciality")
    @Expose
    private String speciality;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

}
