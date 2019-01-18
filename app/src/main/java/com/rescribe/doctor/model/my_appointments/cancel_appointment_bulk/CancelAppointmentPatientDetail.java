package com.rescribe.doctor.model.my_appointments.cancel_appointment_bulk;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class CancelAppointmentPatientDetail implements Parcelable, CustomResponse
{

    @SerializedName("patientId")
    @Expose
    private Integer patientId;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("aptId")
    @Expose
    private Integer aptId;
    @SerializedName("appointmentDate")
    @Expose
    private String appointmentDate;
    @SerializedName("appointmentTime")
    @Expose
    private String appointmentTime;
    public final static Parcelable.Creator<CancelAppointmentPatientDetail> CREATOR = new Creator<CancelAppointmentPatientDetail>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CancelAppointmentPatientDetail createFromParcel(Parcel in) {
            return new CancelAppointmentPatientDetail(in);
        }

        public CancelAppointmentPatientDetail[] newArray(int size) {
            return (new CancelAppointmentPatientDetail[size]);
        }

    }
    ;

    protected CancelAppointmentPatientDetail(Parcel in) {
        this.patientId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.locationId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.aptId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.appointmentDate = ((String) in.readValue((String.class.getClassLoader())));
        this.appointmentTime = ((String) in.readValue((String.class.getClassLoader())));
    }

    public CancelAppointmentPatientDetail() {
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getAptId() {
        return aptId;
    }

    public void setAptId(Integer aptId) {
        this.aptId = aptId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(patientId);
        dest.writeValue(locationId);
        dest.writeValue(aptId);
        dest.writeValue(appointmentDate);
        dest.writeValue(appointmentTime);
    }

    public int describeContents() {
        return  0;
    }

}