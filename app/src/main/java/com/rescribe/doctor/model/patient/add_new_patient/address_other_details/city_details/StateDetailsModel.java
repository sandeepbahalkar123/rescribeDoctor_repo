package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

import java.util.ArrayList;

public class StateDetailsModel  implements CustomResponse {
    @SerializedName("stateId")
    @Expose
    private int stateId;
    @SerializedName("stateName")
    @Expose
    private String stateName;
    @SerializedName("cities")
    @Expose
    private ArrayList<CityData> cityDataList = new ArrayList();

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public ArrayList<CityData> getCityDataList() {
        return cityDataList;
    }

    public void setCityDataList(ArrayList<CityData> cityDataMainList) {
        this.cityDataList = cityDataMainList;
    }
}
