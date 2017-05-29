package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

import me.muapp.android.Classes.Internal.UserContent;

/**
 * Created by rulo on 15/05/17.
 */

public class Message implements Parcelable {
    String key;
    String content;
    int senderId;
    Long timeStamp;
    UserContent attachment;

    public Message() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UserContent getAttachment() {
        return attachment;
    }

    public void setAttachment(UserContent attachment) {
        this.attachment = attachment;
    }

    protected Message(Parcel in) {
        key = in.readString();
        content = in.readString();
        senderId = in.readInt();
        timeStamp = in.readByte() == 0x00 ? null : in.readLong();
        attachment = (UserContent) in.readValue(UserContent.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(content);
        dest.writeInt(senderId);
        if (timeStamp == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(timeStamp);
        }
        dest.writeValue(attachment);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public String toString() {
        return "Message{" +
                "key='" + key + '\'' +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                ", timeStamp=" + timeStamp +
                ", attachment=" + attachment +
                '}';
    }
}