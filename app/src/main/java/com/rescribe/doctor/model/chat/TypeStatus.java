package com.rescribe.doctor.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TypeStatus implements Parcelable {

    @SerializedName("msgId")
    @Expose
    private String msgId;
    @SerializedName("user1id")
    @Expose
    private int user1id;
    @SerializedName("user2id")
    @Expose
    private int user2id;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("msgTime")
    @Expose
    private String msgTime;
    @SerializedName("typeStatus")
    @Expose
    private boolean typeStatus;
    public final static Creator<TypeStatus> CREATOR = new Creator<TypeStatus>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TypeStatus createFromParcel(Parcel in) {
            return new TypeStatus(in);
        }

        public TypeStatus[] newArray(int size) {
            return (new TypeStatus[size]);
        }

    };

    protected TypeStatus(Parcel in) {
        this.msgId = ((String) in.readValue((String.class.getClassLoader())));
        this.user1id = ((int) in.readValue((int.class.getClassLoader())));
        this.user2id = ((int) in.readValue((int.class.getClassLoader())));
        this.sender = ((String) in.readValue((String.class.getClassLoader())));
        this.msgTime = ((String) in.readValue((String.class.getClassLoader())));
        this.typeStatus = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public TypeStatus() {
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getDocId() {
        return user1id;
    }

    public void setDocId(int user1id) {
        this.user1id = user1id;
    }

    public int getPatId() {
        return user2id;
    }

    public void setPatId(int user2id) {
        this.user2id = user2id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public boolean isTyping() {
        return typeStatus;
    }

    public void setTypeStatus(boolean typeStatus) {
        this.typeStatus = typeStatus;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(msgId);
        dest.writeValue(user1id);
        dest.writeValue(user2id);
        dest.writeValue(sender);
        dest.writeValue(msgTime);
        dest.writeValue(typeStatus);
    }

    public int describeContents() {
        return 0;
    }

}