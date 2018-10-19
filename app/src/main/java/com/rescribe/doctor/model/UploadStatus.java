package com.rescribe.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UploadStatus implements Parcelable {

    private String uploadId;
    private String patientId;
    private int docId;
    private String visitDate;
    private String mOpdtime;
    private String opdId;
    private String mHospitalId;
    private String mHospitalPatId;
    private String mLocationId;
    private String parentCaption;
    private String imagePath;
    private int mAptId;
    private HashMap <String,String> headerMap = new HashMap<>();



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uploadId);
        dest.writeString(this.patientId);
        dest.writeInt(this.docId);
        dest.writeString(this.visitDate);
        dest.writeString(this.mOpdtime);
        dest.writeString(this.opdId);
        dest.writeString(this.mHospitalId);
        dest.writeString(this.mHospitalPatId);
        dest.writeString(this.mLocationId);
        dest.writeString(this.parentCaption);
        dest.writeString(this.imagePath);
        dest.writeInt(this.mAptId);
        dest.writeSerializable(this.headerMap);
    }

    public UploadStatus(String uploadId, String patientId, int docId, String visitDate, String mOpdtime, String opdId, String mHospitalId, String mHospitalPatId, String mLocationId, String parentCaption, String imagePath, int mAptId, HashMap<String, String> headerMap) {
        this.uploadId = uploadId;
        this.patientId = patientId;
        this.docId = docId;
        this.visitDate = visitDate;
        this.mOpdtime = mOpdtime;
        this.opdId = opdId;
        this.mHospitalId = mHospitalId;
        this.mHospitalPatId = mHospitalPatId;
        this.mLocationId = mLocationId;
        this.parentCaption = parentCaption;
        this.imagePath = imagePath;
        this.mAptId = mAptId;
        this.headerMap = headerMap;
    }

    protected UploadStatus(Parcel in) {
        this.uploadId = in.readString();
        this.patientId = in.readString();
        this.docId = in.readInt();
        this.visitDate = in.readString();
        this.mOpdtime = in.readString();
        this.opdId = in.readString();
        this.mHospitalId = in.readString();
        this.mHospitalPatId = in.readString();
        this.mLocationId = in.readString();
        this.parentCaption = in.readString();
        this.imagePath = in.readString();
        this.mAptId = in.readInt();
        this.headerMap = (HashMap<String, String>) in.readSerializable();

    }

    public static final Creator<UploadStatus> CREATOR = new Creator<UploadStatus>() {
        @Override
        public UploadStatus createFromParcel(Parcel source) {
            return new UploadStatus(source);
        }

        @Override
        public UploadStatus[] newArray(int size) {
            return new UploadStatus[size];
        }
    };

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getmOpdtime() {
        return mOpdtime;
    }

    public void setmOpdtime(String mOpdtime) {
        this.mOpdtime = mOpdtime;
    }

    public String getOpdId() {
        return opdId;
    }

    public void setOpdId(String opdId) {
        this.opdId = opdId;
    }

    public String getmHospitalId() {
        return mHospitalId;
    }

    public void setmHospitalId(String mHospitalId) {
        this.mHospitalId = mHospitalId;
    }

    public String getmHospitalPatId() {
        return mHospitalPatId;
    }

    public void setmHospitalPatId(String mHospitalPatId) {
        this.mHospitalPatId = mHospitalPatId;
    }

    public String getmLocationId() {
        return mLocationId;
    }

    public void setmLocationId(String mLocationId) {
        this.mLocationId = mLocationId;
    }

    public String getParentCaption() {
        return parentCaption;
    }

    public void setParentCaption(String parentCaption) {
        this.parentCaption = parentCaption;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getmAptId() {
        return mAptId;
    }

    public void setmAptId(int mAptId) {
        this.mAptId = mAptId;
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, String> headerMap) {
        this.headerMap = headerMap;
    }
}
