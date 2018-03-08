
package com.rescribe.doctor.model.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardDetails implements Parcelable
{

    @SerializedName("appointmentList")
    @Expose
    private DashboardAppointmentClinicList dashboardAppointmentClinicList;
    @SerializedName("waitingList")
    @Expose
    private DashboardWaitingList dashboardWaitingList;
    public final static Creator<DashboardDetails> CREATOR = new Creator<DashboardDetails>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DashboardDetails createFromParcel(Parcel in) {
            return new DashboardDetails(in);
        }

        public DashboardDetails[] newArray(int size) {
            return (new DashboardDetails[size]);
        }

    }
    ;

    protected DashboardDetails(Parcel in) {
        this.dashboardAppointmentClinicList = ((DashboardAppointmentClinicList) in.readValue((DashboardAppointmentClinicList.class.getClassLoader())));
        this.dashboardWaitingList = ((DashboardWaitingList) in.readValue((DashboardWaitingList.class.getClassLoader())));
    }

    public DashboardDetails() {
    }

    public DashboardAppointmentClinicList getDashboardAppointmentClinicList() {
        return dashboardAppointmentClinicList;
    }

    public void setDashboardAppointmentClinicList(DashboardAppointmentClinicList dashboardAppointmentClinicList) {
        this.dashboardAppointmentClinicList = dashboardAppointmentClinicList;
    }

    public DashboardWaitingList getDashboardWaitingList() {
        return dashboardWaitingList;
    }

    public void setDashboardWaitingList(DashboardWaitingList dashboardWaitingList) {
        this.dashboardWaitingList = dashboardWaitingList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dashboardAppointmentClinicList);
        dest.writeValue(dashboardWaitingList);
    }

    public int describeContents() {
        return  0;
    }

}
