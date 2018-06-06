package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.states_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

import java.util.ArrayList;

public class StatesDetailsBaseModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private StatesDetailsDataModel statesDetailsDataModel;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public StatesDetailsDataModel getStatesDetailsDataModel() {
        return statesDetailsDataModel;
    }

    public void setStatesDetailsDataModel(StatesDetailsDataModel statesDetailsDataModel) {
        this.statesDetailsDataModel = statesDetailsDataModel;
    }

    public class StatesDetailsDataModel {
        @SerializedName("stateDetails")
        @Expose
        private ArrayList<StatesData> statesDataList =new ArrayList<>();

        public ArrayList<StatesData> getStatesDataList() {
            return statesDataList;
        }

        public void setStatesDataList(ArrayList<StatesData> statesDataList) {
            this.statesDataList = statesDataList;
        }
    }
}
