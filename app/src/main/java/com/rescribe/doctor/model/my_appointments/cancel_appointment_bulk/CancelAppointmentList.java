package com.rescribe.doctor.model.my_appointments.cancel_appointment_bulk;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class CancelAppointmentList implements Parcelable, CustomResponse
{

    @SerializedName("docId")
    @Expose
    private Integer docId;
    @SerializedName("patientDetails")
    @Expose
    private List<CancelAppointmentPatientDetail> patientDetails = null;
    public final static Parcelable.Creator<CancelAppointmentList> CREATOR = new Creator<CancelAppointmentList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CancelAppointmentList createFromParcel(Parcel in) {
            return new CancelAppointmentList(in);
        }

        public CancelAppointmentList[] newArray(int size) {
            return (new CancelAppointmentList[size]);
        }

    }
    ;

    protected CancelAppointmentList(Parcel in) {
        this.docId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.patientDetails, (CancelAppointmentPatientDetail.class.getClassLoader()));
    }

    public CancelAppointmentList() {
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public List<CancelAppointmentPatientDetail> getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(List<CancelAppointmentPatientDetail> patientDetails) {
        this.patientDetails = patientDetails;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(docId);
        dest.writeList(patientDetails);
    }

    public int describeContents() {
        return  0;
    }

}