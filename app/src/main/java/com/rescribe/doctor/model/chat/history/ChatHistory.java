package com.rescribe.doctor.model.chat.history;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatHistory implements Parcelable {

    @SerializedName("chat_id")
    @Expose
    private int chatId;
    @SerializedName("user_1_id")
    @Expose
    private int user1Id;
    @SerializedName("user_2_id")
    @Expose
    private int user2Id;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("msgTime")
    @Expose
    private String msgTime;
    public final static Creator<ChatHistory> CREATOR = new Creator<ChatHistory>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ChatHistory createFromParcel(Parcel in) {
            ChatHistory instance = new ChatHistory();
            instance.chatId = ((int) in.readValue((int.class.getClassLoader())));
            instance.user1Id = ((int) in.readValue((int.class.getClassLoader())));
            instance.user2Id = ((int) in.readValue((int.class.getClassLoader())));
            instance.sender = ((String) in.readValue((String.class.getClassLoader())));
            instance.msg = ((String) in.readValue((String.class.getClassLoader())));
            instance.msgTime = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ChatHistory[] newArray(int size) {
            return (new ChatHistory[size]);
        }

    };

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(chatId);
        dest.writeValue(user1Id);
        dest.writeValue(user2Id);
        dest.writeValue(sender);
        dest.writeValue(msg);
        dest.writeValue(msgTime);
    }

    public int describeContents() {
        return 0;
    }

}