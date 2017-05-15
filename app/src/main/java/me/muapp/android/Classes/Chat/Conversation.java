package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 15/05/17.
 */

public class Conversation implements Parcelable {
    String key;
    Boolean isCrush;
    Message lastMessage;
    String content;
    int senderId;
    Long  creationDate;
    String opponentConversationId;
    int opponentId;

    public Conversation() {
    }

    protected Conversation(Parcel in) {
        key = in.readString();
        byte isCrushVal = in.readByte();
        isCrush = isCrushVal == 0x02 ? null : isCrushVal != 0x00;
        lastMessage = (Message) in.readValue(Message.class.getClassLoader());
        content = in.readString();
        senderId = in.readInt();
        creationDate = in.readByte() == 0x00 ? null : in.readLong();
        opponentConversationId = in.readString();
        opponentId = in.readInt();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getCrush() {
        return isCrush;
    }

    public void setCrush(Boolean crush) {
        isCrush = crush;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
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

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getOpponentConversationId() {
        return opponentConversationId;
    }

    public void setOpponentConversationId(String opponentConversationId) {
        this.opponentConversationId = opponentConversationId;
    }

    public int getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(int opponentId) {
        this.opponentId = opponentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        if (isCrush == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (isCrush ? 0x01 : 0x00));
        }
        dest.writeValue(lastMessage);
        dest.writeString(content);
        dest.writeInt(senderId);
        if (creationDate == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(creationDate);
        }
        dest.writeString(opponentConversationId);
        dest.writeInt(opponentId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Conversation> CREATOR = new Parcelable.Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    @Override
    public String toString() {
        return "Conversation{" +
                "key='" + key + '\'' +
                ", isCrush=" + isCrush +
                ", lastMessage=" + lastMessage +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                ", creationDate=" + creationDate +
                ", opponentConversationId='" + opponentConversationId + '\'' +
                ", opponentId=" + opponentId +
                '}';
    }
}