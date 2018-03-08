
package com.rescribe.doctor.model.waiting_list.request_add_waiting_list;

import java.util.ArrayList;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class RequestForWaitingListPatients implements Parcelable,CustomResponse
{

    @SerializedName("docId")
    @Expose
    private Integer docId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;
    @SerializedName("patientsList")
    @Expose
    private ArrayList<PatientsListAddToWaitingList> patientsListAddToWaitingList = new ArrayList<PatientsListAddToWaitingList>();
    public final static Creator<RequestForWaitingListPatients> CREATOR = new Creator<RequestForWaitingListPatients>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RequestForWaitingListPatients createFromParcel(Parcel in) {
            return new RequestForWaitingListPatients(in);
        }

        public RequestForWaitingListPatients[] newArray(int size) {
            return (new RequestForWaitingListPatients[size]);
        }

    }
    ;

    protected RequestForWaitingListPatients(Parcel in) {
        this.docId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.locationId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.patientsListAddToWaitingList, (PatientsListAddToWaitingList.class.getClassLoader()));
    }

    public RequestForWaitingListPatients() {
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public ArrayList<PatientsListAddToWaitingList> getPatientsListAddToWaitingList() {
        return patientsListAddToWaitingList;
    }

    public void setPatientsListAddToWaitingList(ArrayList<PatientsListAddToWaitingList> patientsListAddToWaitingList) {
        this.patientsListAddToWaitingList = patientsListAddToWaitingList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(docId);
        dest.writeValue(date);
        dest.writeValue(locationId);
        dest.writeList(patientsListAddToWaitingList);
    }

    public int describeContents() {
        return  0;
    }

}
