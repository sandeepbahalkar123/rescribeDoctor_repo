package com.rescribe.doctor.model.new_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Prescription {

    @SerializedName("medicineTypeId")
    @Expose
    private String medicineTypeId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("medicineId")
    @Expose
    private int medicineId;
    @SerializedName("genericName")
    @Expose
    private String genericName;
    @SerializedName("frequency")
    @Expose
    private String frequency;
    @SerializedName("freqSchedule")
    @Expose
    private String freqSchedule;
    @SerializedName("dosage")
    @Expose
    private String dosage;
    @SerializedName("days")
    @Expose
    private String days;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("remarks")
    @Expose
    private String remarks;

    public String getMedicineTypeId() {
        return medicineTypeId;
    }

    public void setMedicineTypeId(String medicineTypeId) {
        this.medicineTypeId = medicineTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFreqSchedule() {
        return freqSchedule;
    }

    public void setFreqSchedule(String freqSchedule) {
        this.freqSchedule = freqSchedule;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}