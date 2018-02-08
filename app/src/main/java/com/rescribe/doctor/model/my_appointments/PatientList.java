
package com.rescribe.doctor.model.my_appointments;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientList implements Parcelable ,Comparable<PatientList>
{

    @SerializedName("salutation")
    @Expose
    private Integer salutation;
    @SerializedName("patientName")
    @Expose
    private String patientName;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("dateOfBirth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("outStandingAmount")
    @Expose
    private Integer outStandingAmount;
    @SerializedName("patientId")
    @Expose
    private Integer patientId;
    @SerializedName("aptId")
    @Expose
    private Integer aptId;
    @SerializedName("opdId")
    @Expose
    private Integer opdId;
    @SerializedName("patientPhone")
    @Expose
    private String patientPhone;
    @SerializedName("patientImageUrl")
    @Expose
    private String patientImageUrl;
    @SerializedName("appointmentDate")
    @Expose
    private String appointmentDate;
    @SerializedName("appointmentTime")
    @Expose
    private String appointmentTime;
    @SerializedName("appointmentStatus")
    @Expose
    private String appointmentStatus;
    @SerializedName("patientEmail")
    @Expose
    private String patientEmail;

    private boolean selected;

    private boolean showHeader = true;
    public final static Creator<PatientList> CREATOR = new Creator<PatientList>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PatientList createFromParcel(Parcel in) {
            return new PatientList(in);
        }

        public PatientList[] newArray(int size) {
            return (new PatientList[size]);
        }

    }
    ;

    protected PatientList(Parcel in) {
        this.salutation = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientName = ((String) in.readValue((String.class.getClassLoader())));
        this.age = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.dateOfBirth = ((String) in.readValue((String.class.getClassLoader())));
        this.gender = ((String) in.readValue((String.class.getClassLoader())));
        this.outStandingAmount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.aptId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.opdId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.patientImageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.appointmentDate = ((String) in.readValue((String.class.getClassLoader())));
        this.appointmentTime = ((String) in.readValue((String.class.getClassLoader())));
        this.appointmentStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.patientEmail = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PatientList() {
    }

    public Integer getSalutation() {
        return salutation;
    }

    public void setSalutation(Integer salutation) {
        this.salutation = salutation;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getOutStandingAmount() {
        return outStandingAmount;
    }

    public void setOutStandingAmount(Integer outStandingAmount) {
        this.outStandingAmount = outStandingAmount;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getAptId() {
        return aptId;
    }

    public void setAptId(Integer aptId) {
        this.aptId = aptId;
    }

    public Integer getOpdId() {
        return opdId;
    }

    public void setOpdId(Integer opdId) {
        this.opdId = opdId;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientImageUrl() {
        return patientImageUrl;
    }

    public void setPatientImageUrl(String patientImageUrl) {
        this.patientImageUrl = patientImageUrl;
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

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(salutation);
        dest.writeValue(patientName);
        dest.writeValue(age);
        dest.writeValue(dateOfBirth);
        dest.writeValue(gender);
        dest.writeValue(outStandingAmount);
        dest.writeValue(patientId);
        dest.writeValue(aptId);
        dest.writeValue(opdId);
        dest.writeValue(patientPhone);
        dest.writeValue(patientImageUrl);
        dest.writeValue(appointmentDate);
        dest.writeValue(appointmentTime);
        dest.writeValue(appointmentStatus);
        dest.writeValue(patientEmail);
    }


    public int describeContents() {
        return  0;
    }

    @Override
    public int compareTo(@NonNull PatientList o) {
        return 0;
    }
}
