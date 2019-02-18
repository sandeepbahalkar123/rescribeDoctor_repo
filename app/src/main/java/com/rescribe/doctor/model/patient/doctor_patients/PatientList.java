
package com.rescribe.doctor.model.patient.doctor_patients;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientList implements Parcelable, Comparable<PatientList>, CustomResponse {

    @SerializedName("patientId")
    @Expose
    private int patientId;
    @SerializedName("relation")
    @Expose
    private String relation;
    @SerializedName("patientName")
    @Expose
    private String patientName;
    @SerializedName("patientFName")
    @Expose
    private String patientFName;
    @SerializedName("patientMName")
    @Expose
    private String patientMName;
    @SerializedName("patientLName")
    @Expose
    private String patientLName;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("bloodGroup")
    @Expose
    private String bloodGroup;
    @SerializedName("salutation")
    @Expose
    private int salutation;
    @SerializedName("patientEmail")
    @Expose
    private String patientEmail;
    @SerializedName("clinicName")
    @Expose
    private String clinicName;
    @SerializedName("referenceId")
    @Expose
    private String referenceId;
    @SerializedName("hospitalPatId")
    @Expose
    private int hospitalPatId;
    @SerializedName("hospitalName")
    @Expose
    private String hospitalName;
    @SerializedName("clinicId")
    @Expose
    private int clinicId;
    @SerializedName("patientPhone")
    @Expose
    private String patientPhone;
    @SerializedName("patientArea")
    @Expose
    private String patientArea;
    @SerializedName("patientCity")
    @Expose
    private String patientCity;
    @SerializedName("patientCityId")
    @Expose
    private int patientCityId;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("onlineStatus")
    @Expose
    private String onlineStatus;
    @SerializedName("patientDob")
    @Expose
    private String patientDob;
    @SerializedName("outstandingAmount")
    @Expose
    private String outstandingAmount;
    @SerializedName("creationDate")
    @Expose
    private String creationDate;
    @SerializedName("isDead")
    @Expose
    private boolean isDead;
    @SerializedName("patientImageUrl")
    @Expose
    private String patientImageUrl;
    @SerializedName("panNumber")
    @Expose
    private String panNumber;
    @SerializedName("aadharNumber")
    @Expose
    private String aadharNumber;
    @SerializedName("patAltPhoneNumber")
    @Expose
    private String patAltPhoneNumber;
    @SerializedName("registerFor")
    @Expose
    private String registerFor;
    @SerializedName("referedDetails")
    @Expose
    private PatientReferenceDetails referedDetails;


    @SerializedName("patientAddressDetails")
    @Expose
    private PatientAddressDetails patientAddressDetails;

    @SerializedName("patInfoFlag")
    @Expose
    private String patInfoFlag = "";

    @SerializedName("aptId")
    @Expose
    private Integer aptId;

    private String spannableString;
    private boolean selected;
    private boolean isAddedMiddleName;
    //--Added for offline adding patient.

    private boolean isOfflinePatientSynced = true;

    public final static Parcelable.Creator<PatientList> CREATOR = new Creator<PatientList>() {


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
        this.patientId = ((int) in.readValue((int.class.getClassLoader())));
        this.relation = ((String) in.readValue((String.class.getClassLoader())));
        this.patientName = ((String) in.readValue((String.class.getClassLoader())));
        this.patientFName = ((String) in.readValue((String.class.getClassLoader())));
        this.patientMName = ((String) in.readValue((String.class.getClassLoader())));
        this.patientLName = ((String) in.readValue((String.class.getClassLoader())));
        this.gender = ((String) in.readValue((String.class.getClassLoader())));
        this.bloodGroup = ((String) in.readValue((String.class.getClassLoader())));
        this.salutation = ((int) in.readValue((int.class.getClassLoader())));
        this.patientEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicName = ((String) in.readValue((String.class.getClassLoader())));
        this.referenceId = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalPatId = ((int) in.readValue((int.class.getClassLoader())));
        this.hospitalName = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicId = ((int) in.readValue((int.class.getClassLoader())));
        this.patientPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.patientArea = ((String) in.readValue((String.class.getClassLoader())));
        this.patientCity = ((String) in.readValue((String.class.getClassLoader())));
        this.patientCityId = ((int) in.readValue((int.class.getClassLoader())));
        this.age = ((String) in.readValue((String.class.getClassLoader())));
        this.onlineStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.patientDob = ((String) in.readValue((String.class.getClassLoader())));
        this.outstandingAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.creationDate = ((String) in.readValue((String.class.getClassLoader())));
        this.isDead = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.patientImageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.panNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.aadharNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.patAltPhoneNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.registerFor = ((String) in.readValue((String.class.getClassLoader())));
        this.referedDetails = ((PatientReferenceDetails) in.readValue((PatientReferenceDetails.class.getClassLoader())));
        this.patientAddressDetails = ((PatientAddressDetails) in.readValue((PatientAddressDetails.class.getClassLoader())));
        this.patInfoFlag = ((String) in.readValue((String.class.getClassLoader())));
        this.aptId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public PatientList() {
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientFName() {
        return patientFName;
    }

    public void setPatientFName(String patientFName) {
        this.patientFName = patientFName;
    }

    public String getPatientMName() {
        return patientMName;
    }

    public void setPatientMName(String patientMName) {
        this.patientMName = patientMName;
    }

    public String getPatientLName() {
        return patientLName;
    }

    public void setPatientLName(String patientLName) {
        this.patientLName = patientLName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getSalutation() {
        return salutation;
    }

    public void setSalutation(int salutation) {
        this.salutation = salutation;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getReferenceID() {
        return referenceId;
    }

    public void setReferenceID(String referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getHospitalPatId() {
        return hospitalPatId;
    }

    public void setHospitalPatId(int hospitalPatId) {
        this.hospitalPatId = hospitalPatId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getClinicId() {
        return clinicId;
    }

    public void setClinicId(int clinicId) {
        this.clinicId = clinicId;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientArea() {
        return patientArea;
    }

    public void setPatientArea(String patientArea) {
        this.patientArea = patientArea;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }

    public int getPatientCityId() {
        return patientCityId;
    }

    public void setPatientCityId(int patientCityId) {
        this.patientCityId = patientCityId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getDateOfBirth() {
        return patientDob;
    }

    public void setDateOfBirth(String patientDob) {
        this.patientDob = patientDob;
    }

    public String getOutStandingAmount() {
        return outstandingAmount;
    }

    public void setOutStandingAmount(String outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
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

    public boolean isAddedMiddleName() {
        return isAddedMiddleName;
    }

    public void setAddedMiddleName(boolean addedMiddleName) {
        isAddedMiddleName = addedMiddleName;
    }

    public boolean isOfflinePatientSynced() {
        return isOfflinePatientSynced;
    }

    public void setOfflinePatientSynced(boolean offlinePatientSynced) {
        isOfflinePatientSynced = offlinePatientSynced;
    }

    public String getPatientImageUrl() {
        return patientImageUrl;
    }

    public void setPatientImageUrl(String patientImageUrl) {
        this.patientImageUrl = patientImageUrl;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPatAltPhoneNumber() {
        return patAltPhoneNumber;
    }

    public void setPatAltPhoneNumber(String patAltPhoneNumber) {
        this.patAltPhoneNumber = patAltPhoneNumber;
    }

    public String getRegisterFor() {
        return registerFor;
    }

    public void setRegisterFor(String registerFor) {
        this.registerFor = registerFor;
    }

    public PatientReferenceDetails getReferedDetails() {
        return referedDetails;
    }

    public void setReferedDetails(PatientReferenceDetails referedDetails) {
        this.referedDetails = referedDetails;
    }

    public PatientAddressDetails getAddressDetails() {
        return patientAddressDetails;
    }

    public void setAddressDetails(PatientAddressDetails patientAddressDetails) {
        this.patientAddressDetails = patientAddressDetails;
    }

    public String getPatInfoFlag() {
        return patInfoFlag;
    }

    public void setPatInfoFlag(String patInfoFlag) {
        this.patInfoFlag = patInfoFlag;
    }

    public Integer getAptId() {
        return aptId;
    }

    public void setAptId(Integer aptId) {
        this.aptId = aptId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(patientId);
        dest.writeValue(relation);
        dest.writeValue(patientName);
        dest.writeValue(patientFName);
        dest.writeValue(patientMName);
        dest.writeValue(patientLName);
        dest.writeValue(gender);
        dest.writeValue(bloodGroup);
        dest.writeValue(salutation);
        dest.writeValue(patientEmail);
        dest.writeValue(clinicName);
        dest.writeValue(referenceId);
        dest.writeValue(hospitalPatId);
        dest.writeValue(hospitalName);
        dest.writeValue(clinicId);
        dest.writeValue(patientPhone);
        dest.writeValue(patientArea);
        dest.writeValue(patientCity);
        dest.writeValue(patientCityId);
        dest.writeValue(age);
        dest.writeValue(onlineStatus);
        dest.writeValue(patientDob);
        dest.writeValue(outstandingAmount);
        dest.writeValue(creationDate);
        dest.writeValue(isDead);
        dest.writeValue(patientImageUrl);
        dest.writeValue(panNumber);
        dest.writeValue(aadharNumber);
        dest.writeValue(patAltPhoneNumber);
        dest.writeValue(registerFor);
        dest.writeValue(referedDetails);
        dest.writeValue(patientAddressDetails);
        dest.writeValue(patInfoFlag);
        dest.writeValue(aptId);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull PatientList patientList) {
        return 0;
    }


}


