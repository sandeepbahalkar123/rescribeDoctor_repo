
package com.rescribe.doctor.model.my_appointments;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClinicList implements Parcelable, Cloneable, Comparable<ClinicList> {
    @SerializedName("clinic_name")
    @Expose
    private String clinicName;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("patientList")
    @Expose
    private List<PatientList> patientList = new ArrayList<PatientList>();

    private PatientList patientHeader;
    private boolean selectedGroupCheckbox;

    public final static Creator<ClinicList> CREATOR = new Creator<ClinicList>() {


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
        this.locationId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.area = ((String) in.readValue((String.class.getClassLoader())));
        this.city = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.patientList, (PatientList.class.getClassLoader()));
    }

    public ClinicList() {
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<PatientList> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<PatientList> patientList) {
        this.patientList = patientList;
    }

    public PatientList getPatientHeader() {
        return patientHeader;
    }

    public void setPatientHeader(PatientList patientHeader) {
        this.patientHeader = patientHeader;
    }

    public boolean isSelectedGroupCheckbox() {
        return selectedGroupCheckbox;
    }

    public void setSelectedGroupCheckbox(boolean selectedGroupCheckbox) {
        this.selectedGroupCheckbox = selectedGroupCheckbox;
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(clinicName);
        dest.writeValue(locationId);
        dest.writeValue(area);
        dest.writeValue(city);
        dest.writeValue(address);
        dest.writeList(patientList);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull ClinicList o) {
        return 0;
    }
}
