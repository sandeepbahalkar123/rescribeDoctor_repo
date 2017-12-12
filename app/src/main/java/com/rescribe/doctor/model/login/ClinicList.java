package com.rescribe.doctor.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClinicList implements Parcelable {

    @SerializedName("clinicName")
    @Expose
    private String clinicName;
    @SerializedName("clinicAddress")
    @Expose
    private String clinicAddress;
    public final static Parcelable.Creator<ClinicList> CREATOR = new Creator<ClinicList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ClinicList createFromParcel(Parcel in) {
            return new ClinicList(in);
        }

        public ClinicList[] newArray(int size) {
            return (new ClinicList[size]);
        }

    };

    protected ClinicList(Parcel in) {
        this.clinicName = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicAddress = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ClinicList() {
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(clinicName);
        dest.writeValue(clinicAddress);
    }

    public int describeContents() {
        return 0;
    }

}