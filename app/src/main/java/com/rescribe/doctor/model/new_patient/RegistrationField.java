package com.rescribe.doctor.model.new_patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegistrationField {

    @SerializedName("fieldName")
    @Expose
    private String fieldName;
    @SerializedName("fieldDisplayName")
    @Expose
    private String fieldDisplayName;
    @SerializedName("fieldValue")
    @Expose
    private boolean fieldValue;

    @SerializedName("isMandatory")
    @Expose
    private boolean isMandatory;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDisplayName() {
        return fieldDisplayName;
    }

    public void setFieldDisplayName(String fieldDisplayName) {
        this.fieldDisplayName = fieldDisplayName;
    }

    public boolean isFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(boolean fieldValue) {
        this.fieldValue = fieldValue;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }
}