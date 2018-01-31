
package com.rescribe.doctor.helpers.doctor_patients;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientDataModel implements Parcelable
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
    @SerializedName("patientList")
    @Expose
    private List<PatientList> patientList = new ArrayList<PatientList>();
    public final static Creator<PatientDataModel> CREATOR = new Creator<PatientDataModel>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PatientDataModel createFromParcel(Parcel in) {
            return new PatientDataModel(in);
        }

        public PatientDataModel[] newArray(int size) {
            return (new PatientDataModel[size]);
        }

    }
    ;

    protected PatientDataModel(Parcel in) {
        this.smsTemplate = ((String) in.readValue((String.class.getClassLoader())));
        this.emailSubject = ((String) in.readValue((String.class.getClassLoader())));
        this.emailTemplate = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.patientList, (PatientList.class.getClassLoader()));
    }

    public PatientDataModel() {
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

    public List<PatientList> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<PatientList> patientList) {
        this.patientList = patientList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(smsTemplate);
        dest.writeValue(emailSubject);
        dest.writeValue(emailTemplate);
        dest.writeList(patientList);
    }

    public int describeContents() {
        return  0;
    }

}
