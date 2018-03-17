
package com.rescribe.doctor.model.new_patient;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewPatientsDetail implements Parcelable
{

    @SerializedName("salutation")
    @Expose
    private String salutation;
    @SerializedName("patientID")
    @Expose
    private Integer patientID;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("patient_gender")
    @Expose
    private String patientGender;
    @SerializedName("patient_phon")
    @Expose
    private String patientPhon;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("patientDob")
    @Expose
    private String patientDob;
    @SerializedName("blood_group")
    @Expose
    private String bloodGroup;
    @SerializedName("patient_city")
    @Expose
    private String patientCity;
    @SerializedName("profilePhoto")
    @Expose
    private String profilePhoto;
    @SerializedName("patient_email")
    @Expose
    private String patientEmail;
    @SerializedName("outstanding_amount")
    @Expose
    private String outstandingAmount;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("hospital_pat_id")
    @Expose
    private Integer hospitalPatId;
    private String spannableString;
    private boolean selected;
    public final static Creator<NewPatientsDetail> CREATOR = new Creator<NewPatientsDetail>() {


        @SuppressWarnings({
            "unchecked"
        })
        public NewPatientsDetail createFromParcel(Parcel in) {
            return new NewPatientsDetail(in);
        }

        public NewPatientsDetail[] newArray(int size) {
            return (new NewPatientsDetail[size]);
        }

    }
    ;

    protected NewPatientsDetail(Parcel in) {
        this.salutation = ((String) in.readValue((String.class.getClassLoader())));
        this.patientID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.patientName = ((String) in.readValue((String.class.getClassLoader())));
        this.patientGender = ((String) in.readValue((String.class.getClassLoader())));
        this.patientPhon = ((String) in.readValue((String.class.getClassLoader())));
        this.age = ((String) in.readValue((String.class.getClassLoader())));
        this.patientDob = ((String) in.readValue((String.class.getClassLoader())));
        this.bloodGroup = ((String) in.readValue((String.class.getClassLoader())));
        this.patientCity = ((String) in.readValue((String.class.getClassLoader())));
        this.profilePhoto = ((String) in.readValue((String.class.getClassLoader())));
        this.patientEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.outstandingAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.cityName = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalPatId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public NewPatientsDetail() {
    }

    public String getSpannableString() {
        return spannableString;
    }

    public void setSpannableString(String spannableString) {
        this.spannableString = spannableString;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public Integer getPatientID() {
        return patientID;
    }

    public void setPatientID(Integer patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientPhon() {
        return patientPhon;
    }

    public void setPatientPhon(String patientPhon) {
        this.patientPhon = patientPhon;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPatientDob() {
        return patientDob;
    }

    public void setPatientDob(String patientDob) {
        this.patientDob = patientDob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(String outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(Integer hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(salutation);
        dest.writeValue(patientID);
        dest.writeValue(patientName);
        dest.writeValue(patientGender);
        dest.writeValue(patientPhon);
        dest.writeValue(age);
        dest.writeValue(patientDob);
        dest.writeValue(bloodGroup);
        dest.writeValue(patientCity);
        dest.writeValue(profilePhoto);
        dest.writeValue(patientEmail);
        dest.writeValue(outstandingAmount);
        dest.writeValue(cityName);
        dest.writeValue(hospitalPatId);
    }

    public int describeContents() {
        return  0;
    }

}
