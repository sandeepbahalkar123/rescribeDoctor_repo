
package com.rescribe.doctor.model.waiting_list.request_delete_waiting_list;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class RequestWaitingListStatusChangeBaseModel implements Parcelable, CustomResponse {

    @SerializedName("waitingId")
    @Expose
    private Integer waitingId;
    @SerializedName("waitingSequence")
    @Expose
    private Integer waitingSequence;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("docId")
    @Expose
    private Integer docId;
    @SerializedName("waitingDate")
    @Expose
    private String waitingDate;

    //-------- required to update waiting status---
    private String time;
    private String patientId;
    private String hospitalPatId;
    private String hospitalId;
    private String status;

    //---------------------------------------------
    public final static Creator<RequestWaitingListStatusChangeBaseModel> CREATOR = new Creator<RequestWaitingListStatusChangeBaseModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RequestWaitingListStatusChangeBaseModel createFromParcel(Parcel in) {
            return new RequestWaitingListStatusChangeBaseModel(in);
        }

        public RequestWaitingListStatusChangeBaseModel[] newArray(int size) {
            return (new RequestWaitingListStatusChangeBaseModel[size]);
        }

    };

    protected RequestWaitingListStatusChangeBaseModel(Parcel in) {
        this.waitingId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.waitingSequence = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.locationId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.docId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.waitingDate = ((String) in.readValue((String.class.getClassLoader())));
        //-----------
        this.time = ((String) in.readValue((String.class.getClassLoader())));
        this.patientId = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalPatId = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalId = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));

        //-----------
    }

    public RequestWaitingListStatusChangeBaseModel() {
    }

    public Integer getWaitingId() {
        return waitingId;
    }

    public void setWaitingId(Integer waitingId) {
        this.waitingId = waitingId;
    }

    public Integer getWaitingSequence() {
        return waitingSequence;
    }

    public void setWaitingSequence(Integer waitingSequence) {
        this.waitingSequence = waitingSequence;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getWaitingDate() {
        return waitingDate;
    }

    public void setWaitingDate(String waitingDate) {
        this.waitingDate = waitingDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(waitingId);
        dest.writeValue(waitingSequence);
        dest.writeValue(locationId);
        dest.writeValue(docId);
        dest.writeValue(waitingDate);
        dest.writeValue(time);
        dest.writeValue(patientId);
        dest.writeValue(hospitalPatId);
        dest.writeValue(hospitalId);
        dest.writeValue(status);

    }

    public int describeContents() {
        return 0;
    }

}
