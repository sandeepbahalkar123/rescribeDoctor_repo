package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

import java.util.ArrayList;

public class StateAndCityBaseModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private CityDetailsDataModel cityDetailsDataModel;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public CityDetailsDataModel getCityDetailsDataModel() {
        return cityDetailsDataModel;
    }

    public void setCityDetailsDataModel(CityDetailsDataModel cityDetailsDataModel) {
        this.cityDetailsDataModel = cityDetailsDataModel;
    }

    public class CityDetailsDataModel {
        @SerializedName("cityDetails")
        @Expose
        private ArrayList<StateDetailsModel> stateDetailsMainList;

        public ArrayList<StateDetailsModel> getStateDetailsMainList() {
            return stateDetailsMainList;
        }

        public void setStateDetailsMainList(ArrayList<StateDetailsModel> stateDetailsMainList) {
            this.stateDetailsMainList = stateDetailsMainList;
        }
    }
}
