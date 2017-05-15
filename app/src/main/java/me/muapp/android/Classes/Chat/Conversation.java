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
    Long timeStamp;
    String opponentConversationId;
    int opponentId;

    protected Conversation(Parcel in) {
        key = in.readString();
        byte isCrushVal = in.readByte();
        isCrush = isCrushVal == 0x02 ? null : isCrushVal != 0x00;
        lastMessage = (Message) in.readValue(Message.class.getClassLoader());
        content = in.readString();
        senderId = in.readInt();
        timeStamp = in.readByte() == 0x00 ? null : in.readLong();
        opponentConversationId = in.readString();
        opponentId = in.readInt();
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
        if (timeStamp == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(timeStamp);
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
                ", timeStamp=" + timeStamp +
                ", opponentConversationId='" + opponentConversationId + '\'' +
                ", opponentId=" + opponentId +
                '}';
    }
}