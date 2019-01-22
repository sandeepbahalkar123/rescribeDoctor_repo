
package com.rescribe.doctor.model.dashboard;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DashboardDetails implements Parcelable
{
    @SerializedName("versionCode")
    @Expose
    private Integer versionCode;
    @SerializedName("appURL")
    @Expose
    private String appURL;

    @SerializedName("appointmentType")
    @Expose
    private String appointmentType;
    @SerializedName("calendarTypeList")
    @Expose
    private ArrayList<CalendarTypeList> calendarTypeList = new ArrayList<CalendarTypeList>();
    @SerializedName("appointmentList")
    @Expose
    private DashboardAppointmentClinicList dashboardAppointmentClinicList;
    @SerializedName("waitingList")
    @Expose
    private DashboardWaitingList dashboardWaitingList;

    @SerializedName("appointmentFormat")
    @Expose
    private int appointmentFormat;

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
        this.versionCode = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.appURL = ((String) in.readValue((String.class.getClassLoader())));
        this.appointmentType = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.calendarTypeList, (CalendarTypeList.class.getClassLoader()));
        this.dashboardAppointmentClinicList = ((DashboardAppointmentClinicList) in.readValue((DashboardAppointmentClinicList.class.getClassLoader())));
        this.dashboardWaitingList = ((DashboardWaitingList) in.readValue((DashboardWaitingList.class.getClassLoader())));
        this.appointmentFormat = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public DashboardDetails() {
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getAppURL() {
        return appURL;
    }

    public void setAppURL(String appURL) {
        this.appURL = appURL;
    }
    public DashboardAppointmentClinicList getDashboardAppointmentClinicList() {
        return dashboardAppointmentClinicList;
    }
    public ArrayList<CalendarTypeList> getCalendarTypeList() {
        return calendarTypeList;
    }

    public void setCalendarTypeList(ArrayList<CalendarTypeList> calendarTypeList) {
        this.calendarTypeList = calendarTypeList;
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

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public int getAppointmentFormat() {
        return appointmentFormat;
    }

    public void setAppointmentFormat(int appointmentFormat) {
        this.appointmentFormat = appointmentFormat;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(versionCode);
        dest.writeValue(appURL);
        dest.writeList(calendarTypeList);
        dest.writeValue(dashboardAppointmentClinicList);
        dest.writeValue(dashboardWaitingList);
        dest.writeValue(appointmentType);
        dest.writeValue(appointmentFormat);
    }

    public int describeContents() {
        return  0;
    }

}
