package com.rescribe.doctor.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashSet;

public class DocDetail implements Parcelable
{

    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("premium")
    @Expose
    private boolean premium;
    @SerializedName("docName")
    @Expose
    private String docName;
    @SerializedName("docEmail")
    @Expose
    private String docEmail;
    @SerializedName("docSpaciality")
    @Expose
    private String docSpaciality;
    @SerializedName("docAddress")
    @Expose
    private String docAddress;
    @SerializedName("docImgUrl")
    @Expose
    private String docImgUrl;
    @SerializedName("docDegree")
    @Expose
    private String docDegree;
    @SerializedName("docExperience")
    @Expose
    private String docExperience;
    @SerializedName("docGender")
    @Expose
    private String docGender;
    @SerializedName("docPhone")
    @Expose
    private String docPhone;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("docInfo")
    @Expose
    private String docInfo;
    @SerializedName("clinicList")
    @Expose
    private ArrayList<ClinicList> clinicList = null;
    public final static Parcelable.Creator<DocDetail> CREATOR = new Creator<DocDetail>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DocDetail createFromParcel(Parcel in) {
            return new DocDetail(in);
        }

        public DocDetail[] newArray(int size) {
            return (new DocDetail[size]);
        }

    }
            ;

    protected DocDetail(Parcel in) {
        this.docId = ((int) in.readValue((int.class.getClassLoader())));
        this.premium = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.docName = ((String) in.readValue((String.class.getClassLoader())));
        this.docEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.docSpaciality = ((String) in.readValue((String.class.getClassLoader())));
        this.docAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.docImgUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.docDegree = ((String) in.readValue((String.class.getClassLoader())));
        this.docExperience = ((String) in.readValue((String.class.getClassLoader())));
        this.docGender = ((String) in.readValue((String.class.getClassLoader())));
        this.docPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.website = ((String) in.readValue((String.class.getClassLoader())));
        this.docInfo = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.clinicList, (ClinicList.class.getClassLoader()));
    }

    public DocDetail() {
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocEmail() {
        return docEmail;
    }

    public void setDocEmail(String docEmail) {
        this.docEmail = docEmail;
    }

    public String getDocSpaciality() {
        return docSpaciality;
    }

    public void setDocSpaciality(String docSpaciality) {
        this.docSpaciality = docSpaciality;
    }

    public String getDocAddress() {
        return docAddress;
    }

    public void setDocAddress(String docAddress) {
        this.docAddress = docAddress;
    }

    public String getDocImgUrl() {
        return docImgUrl;
    }

    public void setDocImgUrl(String docImgUrl) {
        this.docImgUrl = docImgUrl;
    }

    public String getDocDegree() {
        return docDegree;
    }

    public void setDocDegree(String docDegree) {
        this.docDegree = docDegree;
    }

    public String getDocExperience() {
        return docExperience;
    }

    public void setDocExperience(String docExperience) {
        this.docExperience = docExperience;
    }

    public String getDocGender() {
        return docGender;
    }

    public void setDocGender(String docGender) {
        this.docGender = docGender;
    }

    public String getDocPhone() {
        return docPhone;
    }

    public void setDocPhone(String docPhone) {
        this.docPhone = docPhone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(String docInfo) {
        this.docInfo = docInfo;
    }

    public ArrayList<ClinicList> getClinicList() {
        return clinicList;
    }

    public void setClinicList(ArrayList<ClinicList> clinicList) {
        this.clinicList = clinicList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(docId);
        dest.writeValue(premium);
        dest.writeValue(docName);
        dest.writeValue(docEmail);
        dest.writeValue(docSpaciality);
        dest.writeValue(docAddress);
        dest.writeValue(docImgUrl);
        dest.writeValue(docDegree);
        dest.writeValue(docExperience);
        dest.writeValue(docGender);
        dest.writeValue(docPhone);
        dest.writeValue(website);
        dest.writeValue(docInfo);
        dest.writeList(clinicList);
    }

    public int describeContents() {
        return 0;
    }


    public HashSet<Integer> getUniqStatesOfClinic() {
        HashSet<Integer> states = new HashSet<Integer>();

        for (ClinicList clinicList :
                clinicList) {
            states.add(clinicList.getStateId());
        }

        return states;
    }

}