package com.rescribe.doctor.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.rescribe.doctor.services.MQTTService.DOCTOR;
import static com.rescribe.doctor.util.RescribeConstants.DOWNLOADING;
import static com.rescribe.doctor.util.RescribeConstants.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.UPLOADING;

public class MQTTMessage implements Parcelable {

    @SerializedName("msgId")
    @Expose
    private String msgId;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("msgTime")
    @Expose
    private String msgTime;

    // change

    @SerializedName("sender")
    @Expose
    private String sender = DOCTOR;

    @SerializedName("user2id")
    @Expose
    private int patId;
    @SerializedName("user1id")
    @Expose
    private int docId;
    @SerializedName("senderName")
    @Expose
    private String name;
    @SerializedName("speciality")
    @Expose
    private String specialization;
    @SerializedName("onlineStatus")
    @Expose
    private String onlineStatus;

    @SerializedName("msgStatus")
    @Expose
    private String msgStatus = "";

    @SerializedName("paidStatus")
    @Expose
    private int paidStatus;
    @SerializedName("senderImgUrl")
    @Expose
    private String imageUrl = "";
    @SerializedName("fileUrl")
    @Expose
    private String fileUrl = "";
    @SerializedName("fileType")
    @Expose
    private String fileType = "";

    @SerializedName("uploadStatus")
    @Expose
    private int uploadStatus = UPLOADING;

    @SerializedName("downloadStatus")
    @Expose
    private int downloadStatus = FAILED;

    private boolean isDateVisible;

    // Added End

    public final static Creator<MQTTMessage> CREATOR = new Creator<MQTTMessage>() {

        @SuppressWarnings({
                "unchecked"
        })
        public MQTTMessage createFromParcel(Parcel in) {
            MQTTMessage instance = new MQTTMessage();
            instance.msgId = ((String) in.readValue((int.class.getClassLoader())));
            instance.topic = ((String) in.readValue((String.class.getClassLoader())));
            instance.msg = ((String) in.readValue((String.class.getClassLoader())));
            instance.msgTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.sender = ((String) in.readValue((String.class.getClassLoader())));
            instance.patId = ((int) in.readValue((int.class.getClassLoader())));
            instance.docId = ((int) in.readValue((int.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.specialization = ((String) in.readValue((String.class.getClassLoader())));
            instance.onlineStatus = ((String) in.readValue((String.class.getClassLoader())));

            instance.msgStatus = ((String) in.readValue((String.class.getClassLoader())));

            instance.paidStatus = ((int) in.readValue((int.class.getClassLoader())));
            instance.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.fileUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.fileType = ((String) in.readValue((String.class.getClassLoader())));

            instance.uploadStatus = ((int) in.readValue((int.class.getClassLoader())));
            instance.downloadStatus = ((int) in.readValue((int.class.getClassLoader())));

            instance.isDateVisible = ((boolean) in.readValue((int.class.getClassLoader())));
            return instance;
        }

        public MQTTMessage[] newArray(int size) {
            return (new MQTTMessage[size]);
        }

    };

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getPatId() {
        return patId;
    }

    public void setPatId(int patId) {
        this.patId = patId;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public int getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Integer paidStatus) {
        this.paidStatus = paidStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPaidStatus(int paidStatus) {
        this.paidStatus = paidStatus;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public boolean isDateVisible() {
        return isDateVisible;
    }

    public void setDateVisible(boolean dateVisible) {
        isDateVisible = dateVisible;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(msgId);
        dest.writeValue(topic);
        dest.writeValue(msg);
        dest.writeValue(msgTime);
        dest.writeValue(sender);
        dest.writeValue(patId);
        dest.writeValue(docId);
        dest.writeValue(name);
        dest.writeValue(specialization);
        dest.writeValue(onlineStatus);
        dest.writeValue(msgStatus);
        dest.writeValue(paidStatus);
        dest.writeValue(imageUrl);
        dest.writeValue(fileUrl);
        dest.writeValue(fileType);

        dest.writeValue(uploadStatus);
        dest.writeValue(downloadStatus);

        dest.writeValue(isDateVisible);
    }

    public int describeContents() {
        return 0;
    }

}