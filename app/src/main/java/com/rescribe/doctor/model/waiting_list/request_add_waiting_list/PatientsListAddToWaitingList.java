
package com.rescribe.doctor.model.waiting_list.request_add_waiting_list;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientsListAddToWaitingList implements Parcelable
{

    @SerializedName("patientId")
    @Expose
    private String patientId;
    @SerializedName("hospitalPatId")
    @Expose
    private String hospitalPatId;
    @SerializedName("patientName")
    @Expose
    private String patientName;
    public final static Creator<PatientsListAddToWaitingList> CREATOR = new Creator<PatientsListAddToWaitingList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PatientsListAddToWaitingList createFromParcel(Parcel in) {
            return new PatientsListAddToWaitingList(in);
        }

        public PatientsListAddToWaitingList[] newArray(int size) {
            return (new PatientsListAddToWaitingList[size]);
        }

    }
    ;

    protected PatientsListAddToWaitingList(Parcel in) {
        this.patientId = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalPatId = ((String) in.readValue((String.class.getClassLoader())));
        this.patientName = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PatientsListAddToWaitingList() {
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(String hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(patientId);
        dest.writeValue(hospitalPatId);
        dest.writeValue(patientName);
    }

    public int describeContents() {
        return  0;
    }

}
