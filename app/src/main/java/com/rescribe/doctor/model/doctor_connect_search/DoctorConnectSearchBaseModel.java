
package com.rescribe.doctor.model.doctor_connect_search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;


import java.util.List;

public class DoctorConnectSearchBaseModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private List<DoctorConnectSearchModel> data = null;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public List<DoctorConnectSearchModel> getData() {
        return data;
    }

    public void setData(List<DoctorConnectSearchModel> data) {
        this.data = data;
    }

}
