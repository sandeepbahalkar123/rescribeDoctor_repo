package com.rescribe.doctor.model.new_patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReferenceData {

    @SerializedName("referenceIdSetting")
    @Expose
    private Boolean referenceIdSetting;
    @SerializedName("types")
    @Expose
    private List<ReferenceType> referenceTypeList = null;

    public Boolean getReferenceIdSetting() {
        return referenceIdSetting;
    }

    public void setReferenceIdSetting(Boolean referenceIdSetting) {
        this.referenceIdSetting = referenceIdSetting;
    }

    public List<ReferenceType> getReferenceTypeList() {
        return referenceTypeList;
    }

    public void setReferenceTypeList(List<ReferenceType> referenceTypeList) {
        this.referenceTypeList = referenceTypeList;
    }
}