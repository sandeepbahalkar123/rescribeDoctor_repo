package com.rescribe.doctor.model.login;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClinicList implements Parcelable
{

    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("clinicName")
    @Expose
    private String clinicName;
    @SerializedName("clinicAddress")
    @Expose
    private String clinicAddress;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("services")
    @Expose
    private ArrayList<String> services = new ArrayList<>();

    @SerializedName("stateId")
    @Expose
    private Integer stateId;
    @SerializedName("cityId")
    @Expose
    private Integer cityId;

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
        this.clinicId = ((int) in.readValue((Integer.class.getClassLoader())));
        this.clinicName = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.locationId = ((Integer) in.readValue((Integer.class.getClassLoader())));

        this.stateId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));

        in.readList(this.services, (java.lang.String.class.getClassLoader()));
    }

    public ClinicList() {
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }


    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(clinicId);
        dest.writeValue(clinicName);
        dest.writeValue(clinicAddress);
        dest.writeValue(locationId);
        dest.writeValue(stateId);
        dest.writeValue(cityId);
        dest.writeList(services);
    }

    public int describeContents() {
        return 0;
    }

}