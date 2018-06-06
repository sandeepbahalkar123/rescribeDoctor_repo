package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.states_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class StatesData implements CustomResponse {
    @SerializedName("id")
    @Expose
    private int stateID;
    @SerializedName("state_name")
    @Expose
    private String stateName;

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
