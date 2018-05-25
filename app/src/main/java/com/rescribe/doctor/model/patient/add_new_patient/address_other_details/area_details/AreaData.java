package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class AreaData  implements CustomResponse {
    @SerializedName("id")
    @Expose
    private int areaId;
    @SerializedName("area_name")
    @Expose
    private String areaName;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
