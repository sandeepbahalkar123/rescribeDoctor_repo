
package com.rescribe.doctor.helpers.doctor_patients;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.util.CommonMethods;

public class PatientList implements Parcelable, Comparable<PatientList>, CustomResponse {

    @SerializedName("salutation")
    @Expose
    private Integer salutation;
    @SerializedName("patientName")
    @Expose
    private String patientName;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("patientDob")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("outstandingAmount")
    @Expose
    private String outStandingAmount;
    @SerializedName("patientId")
    @Expose
    private Integer patientId;
    @SerializedName("patientPhone")
    @Expose
    private String patientPhone;
    @SerializedName("patientImageUrl")
    @Expose
    private String patientImageUrl;
    @SerializedName("patientEmail")
    @Expose
    private String patientEmail;
    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("clinicName")
    @Expose
    private String clinicName;
    @SerializedName("hospitalPatId")
    @Expose
    private Integer hospitalPatId;
    @SerializedName("patientCity")
    @Expose
    private String patientCity = "";

    @SerializedName("patientArea")
    @Expose
    private String patientArea = "";

    private String spannableString;
    private boolean selected;

    //--Added for offline adding patient.
    private String offlineReferenceID;
    private boolean isPatientInsertedOffline;
    private boolean isOfflinePatientSynced;
    private String offlinePatientCreatedTimeStamp;
    //--------
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

    };

    protected PatientList(Parcel in) {
        this.salutation = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientName = ((String) in.readValue((String.class.getClassLoader())));
        this.age = ((String) in.readValue((String.class.getClassLoader())));
        this.dateOfBirth = ((String) in.readValue((String.class.getClassLoader())));
        this.gender = ((String) in.readValue((String.class.getClassLoader())));
        this.outStandingAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.patientId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.patientImageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.patientEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicId = ((int) in.readValue((int.class.getClassLoader())));
        this.clinicName = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalPatId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientCity = ((String) in.readValue((String.class.getClassLoader())));
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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

    public String getOutStandingAmount() {
        return outStandingAmount;
    }

    public void setOutStandingAmount(String outStandingAmount) {
        this.outStandingAmount = outStandingAmount;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
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

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSpannableString() {
        return spannableString;
    }

    public void setSpannableString(String spannableString) {
        this.spannableString = spannableString;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public Integer getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(Integer hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(salutation);
        dest.writeValue(patientName);
        dest.writeValue(age);
        dest.writeValue(dateOfBirth);
        dest.writeValue(gender);
        dest.writeValue(outStandingAmount);
        dest.writeValue(patientId);
        dest.writeValue(patientPhone);
        dest.writeValue(patientImageUrl);
        dest.writeValue(patientEmail);
        dest.writeValue(clinicId);
        dest.writeValue(clinicName);
        dest.writeValue(hospitalPatId);
        dest.writeValue(patientCity);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull PatientList o) {
        return 0;
    }

    public String getPatientArea() {
        return patientArea;
    }

    public String getOfflineReferenceID() {
        return offlineReferenceID;
    }

    public void setOfflineReferenceID(String offlineReferenceID) {
        this.offlineReferenceID = offlineReferenceID;
    }

    public boolean isPatientInsertedOffline() {
        return isPatientInsertedOffline;
    }

    public void setPatientInsertedOffline(boolean patientInsertedOffline) {
        isPatientInsertedOffline = patientInsertedOffline;
    }

    public boolean isOfflinePatientSynced() {
        return isOfflinePatientSynced;
    }

    public void setOfflinePatientSynced(boolean offlinePatientSynced) {
        isOfflinePatientSynced = offlinePatientSynced;
    }

    public String getOfflinePatientCreatedTimeStamp() {
        return offlinePatientCreatedTimeStamp;
    }

    public void setOfflinePatientCreatedTimeStamp(String offlinePatientCreatedTimeStamp) {
        this.offlinePatientCreatedTimeStamp = offlinePatientCreatedTimeStamp;
    }
}
