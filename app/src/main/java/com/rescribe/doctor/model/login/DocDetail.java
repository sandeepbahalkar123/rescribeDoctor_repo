package com.rescribe.doctor.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DocDetail implements Parcelable {

    @SerializedName("docId")
    @Expose
    private int docId;
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
    @SerializedName("clinicList")
    @Expose
    private ArrayList<ClinicList> clinicList = new ArrayList<ClinicList>();
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

    };

    protected DocDetail(Parcel in) {
        this.docId = ((int) in.readValue((int.class.getClassLoader())));
        this.docName = ((String) in.readValue((String.class.getClassLoader())));
        this.docEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.docSpaciality = ((String) in.readValue((String.class.getClassLoader())));
        this.docAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.docImgUrl = ((String) in.readValue((String.class.getClassLoader())));
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

    public ArrayList<ClinicList> getClinicList() {
        return clinicList;
    }

    public void setClinicList(ArrayList<ClinicList> clinicList) {
        this.clinicList = clinicList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(docId);
        dest.writeValue(docName);
        dest.writeValue(docEmail);
        dest.writeValue(docSpaciality);
        dest.writeValue(docAddress);
        dest.writeValue(docImgUrl);
        dest.writeList(clinicList);
    }

    public int describeContents() {
        return 0;
    }

}