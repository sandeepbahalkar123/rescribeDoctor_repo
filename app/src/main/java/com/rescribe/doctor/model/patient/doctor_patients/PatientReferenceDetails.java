
package com.rescribe.doctor.model.patient.doctor_patients;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientReferenceDetails implements Parcelable, CustomResponse {

    @SerializedName("detailedId")
    @Expose
    private int detailedId;
    @SerializedName("referredTypeId")
    @Expose
    private int referredTypeId;
    @SerializedName("patientId")
    @Expose
    private String patientId;
    @SerializedName("docId")
    @Expose
    private String docId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("salutation")
    @Expose
    private int salutation;
    @SerializedName("description")
    @Expose
    private String description;
    public final static Parcelable.Creator<PatientReferenceDetails> CREATOR = new Creator<PatientReferenceDetails>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PatientReferenceDetails createFromParcel(Parcel in) {
            return new PatientReferenceDetails(in);
        }

        public PatientReferenceDetails[] newArray(int size) {
            return (new PatientReferenceDetails[size]);
        }

    };

    protected PatientReferenceDetails(Parcel in) {
        this.detailedId = ((int) in.readValue((int.class.getClassLoader())));
        this.referredTypeId = ((int) in.readValue((int.class.getClassLoader())));
        this.patientId = ((String) in.readValue((String.class.getClassLoader())));
        this.docId = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.emailId = ((String) in.readValue((String.class.getClassLoader())));
        this.phoneNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.salutation = ((int) in.readValue((int.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
    }

    public PatientReferenceDetails() {
    }

    public int getDetailedId() {
        return detailedId;
    }

    public void setDetailedId(int detailedId) {
        this.detailedId = detailedId;
    }

    public int getReferredTypeId() {
        return referredTypeId;
    }

    public void setReferredTypeId(int referredTypeId) {
        this.referredTypeId = referredTypeId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSalutation() {
        return salutation;
    }

    public void setSalutation(int salutation) {
        this.salutation = salutation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(detailedId);
        dest.writeValue(referredTypeId);
        dest.writeValue(patientId);
        dest.writeValue(docId);
        dest.writeValue(name);
        dest.writeValue(emailId);
        dest.writeValue(phoneNumber);
        dest.writeValue(salutation);
        dest.writeValue(description);
    }

    public int describeContents() {
        return 0;
    }

}