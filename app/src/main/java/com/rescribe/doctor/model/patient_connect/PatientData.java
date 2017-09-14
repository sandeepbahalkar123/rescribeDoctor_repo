
package com.rescribe.doctor.model.patient_connect;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientData implements Parcelable, CustomResponse {

    @SerializedName("patientId")
    @Expose
    private Integer patientId;
    @SerializedName("patientName")
    @Expose
    private String patientName;
    @SerializedName("onlineStatus")
    @Expose
    private String onlineStatus;

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public final static Creator<PatientData> CREATOR = new Creator<PatientData>() {
        @SuppressWarnings({
                "unchecked"
        })
        public PatientData createFromParcel(Parcel in) {
            PatientData instance = new PatientData();
            instance.patientId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.patientName = ((String) in.readValue((String.class.getClassLoader())));
            instance.onlineStatus = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public PatientData[] newArray(int size) {
            return (new PatientData[size]);
        }

    };


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(patientId);
        dest.writeValue(patientName);
        dest.writeValue(onlineStatus);
    }

    public int describeContents() {
        return 0;
    }

}