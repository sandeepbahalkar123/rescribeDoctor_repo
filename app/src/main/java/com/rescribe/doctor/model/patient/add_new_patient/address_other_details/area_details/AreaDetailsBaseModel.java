package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateDetailsModel;

import java.util.ArrayList;

public class AreaDetailsBaseModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private AreaDetailsDataModel areaDetailsDataModel;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public AreaDetailsDataModel getAreaDetailsDataModel() {
        return areaDetailsDataModel;
    }

    public void setAreaDetailsDataModel(AreaDetailsDataModel areaDetailsDataModel) {
        this.areaDetailsDataModel = areaDetailsDataModel;
    }

    public class AreaDetailsDataModel {
        @SerializedName("areaDetails")
        @Expose
        private ArrayList<AreaData> areaDataList =new ArrayList<>();

        public ArrayList<AreaData> getAreaDataList() {
            return areaDataList;
        }

        public void setAreaDataList(ArrayList<AreaData> areaDataList) {
            this.areaDataList = areaDataList;
        }
    }
}
