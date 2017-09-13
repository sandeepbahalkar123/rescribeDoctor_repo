package com.rescribe.doctor.model.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageList implements Parcelable {

    @SerializedName("msgId")
    @Expose
    private int msgId;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("docId")
    @Expose
    private int docId;
    @SerializedName("patId")
    @Expose
    private int patId;
    @SerializedName("who")
    @Expose
    private int who;
    public final static Parcelable.Creator<MessageList> CREATOR = new Creator<MessageList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MessageList createFromParcel(Parcel in) {
            MessageList instance = new MessageList();
            instance.msgId = ((int) in.readValue((int.class.getClassLoader())));
            instance.topic = ((String) in.readValue((String.class.getClassLoader())));
            instance.msg = ((String) in.readValue((String.class.getClassLoader())));
            instance.docId = ((int) in.readValue((int.class.getClassLoader())));
            instance.patId = ((int) in.readValue((int.class.getClassLoader())));
            instance.who = ((int) in.readValue((int.class.getClassLoader())));
            return instance;
        }

        public MessageList[] newArray(int size) {
            return (new MessageList[size]);
        }

    };

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
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

    public int getWho() {
        return who;
    }

    public void setWho(int who) {
        this.who = who;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(msgId);
        dest.writeValue(topic);
        dest.writeValue(msg);
        dest.writeValue(docId);
        dest.writeValue(patId);
        dest.writeValue(who);
    }

    public int describeContents() {
        return 0;
    }

}