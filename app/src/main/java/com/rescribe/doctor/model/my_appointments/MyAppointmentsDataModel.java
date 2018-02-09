
package com.rescribe.doctor.model.my_appointments;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyAppointmentsDataModel implements Parcelable
{

    @SerializedName("smsTemplate")
    @Expose
    private String smsTemplate;
    @SerializedName("emailSubject")
    @Expose
    private String emailSubject;
    @SerializedName("emailTemplate")
    @Expose
    private String emailTemplate;
    @SerializedName("clinicList")
    @Expose
    private ArrayList<ClinicList> clinicList = new ArrayList<ClinicList>();
    public final static Creator<MyAppointmentsDataModel> CREATOR = new Creator<MyAppointmentsDataModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MyAppointmentsDataModel createFromParcel(Parcel in) {
            return new MyAppointmentsDataModel(in);
        }

        public MyAppointmentsDataModel[] newArray(int size) {
            return (new MyAppointmentsDataModel[size]);
        }

    }
    ;

    protected MyAppointmentsDataModel(Parcel in) {
        this.smsTemplate = ((String) in.readValue((String.class.getClassLoader())));
        this.emailSubject = ((String) in.readValue((String.class.getClassLoader())));
        this.emailTemplate = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.clinicList, (ClinicList.class.getClassLoader()));
    }

    public MyAppointmentsDataModel() {
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public ArrayList<ClinicList> getClinicList() {
        return clinicList;
    }

    public void setClinicList(ArrayList<ClinicList> clinicList) {
        this.clinicList = clinicList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(smsTemplate);
        dest.writeValue(emailSubject);
        dest.writeValue(emailTemplate);
        dest.writeList(clinicList);
    }

    public int describeContents() {
        return  0;
    }

}
