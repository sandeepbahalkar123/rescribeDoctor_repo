package com.rescribe.doctor.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MessageModel implements Parcelable {

    @SerializedName("messageList")
    @Expose
    private ArrayList<MessageList> messageList = new ArrayList<MessageList>();
    public final static Parcelable.Creator<MessageModel> CREATOR = new Creator<MessageModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MessageModel createFromParcel(Parcel in) {
            MessageModel instance = new MessageModel();
            in.readList(instance.messageList, (MessageList.class.getClassLoader()));
            return instance;
        }

        public MessageModel[] newArray(int size) {
            return (new MessageModel[size]);
        }

    };

    public ArrayList<MessageList> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<MessageList> messageList) {
        this.messageList = messageList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(messageList);
    }

    public int describeContents() {
        return 0;
    }

}