package com.rescribe.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UploadStatus implements Parcelable {

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
    private String uploadId;
    private String visitDate;
    private String mOpdtime;
    private String parentCaption;
    private String imagePath;
    private String recordType;
    private boolean uploading = false;
    private HashMap<String, String> headerMap = new HashMap<>();

    public UploadStatus(String uploadId, String visitDate, String mOpdtime, String parentCaption, String imagePath, String recordType, HashMap<String, String> headerMap) {
        this.uploadId = uploadId;
        this.visitDate = visitDate;
        this.mOpdtime = mOpdtime;
        this.parentCaption = parentCaption;
        this.imagePath = imagePath;
        this.recordType = recordType;
        this.headerMap = headerMap;
    }

    protected UploadStatus(Parcel in) {
        this.uploadId = in.readString();
        this.visitDate = in.readString();
        this.mOpdtime = in.readString();
        this.parentCaption = in.readString();
        this.imagePath = in.readString();
        this.recordType = in.readString();
        this.headerMap = (HashMap<String, String>) in.readSerializable();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uploadId);
        dest.writeString(this.visitDate);
        dest.writeString(this.mOpdtime);
        dest.writeString(this.parentCaption);
        dest.writeString(this.imagePath);
        dest.writeString(this.recordType);
        dest.writeSerializable(this.headerMap);
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
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

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public boolean isUploading() {
        return uploading;
    }

    public void setUploading(boolean uploading) {
        this.uploading = uploading;
    }
}
