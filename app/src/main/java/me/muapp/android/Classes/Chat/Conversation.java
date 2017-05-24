package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 15/05/17.
 */

public class Conversation implements Parcelable {
    String key;
    Boolean crush;
    Boolean likeByMe;
    Boolean likeByOpponent;
    Message lastMessage;
    Long creationDate;
    String opponentConversationId;
    int opponentId;
    Long lastSeenByOpponent;

    public Conversation() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getCrush() {
        return crush;
    }

    public void setCrush(Boolean crush) {
        this.crush = crush;
    }

    public Boolean getLikeByMe() {
        return likeByMe;
    }

    public void setLikeByMe(Boolean likeByMe) {
        this.likeByMe = likeByMe;
    }

    public Boolean getLikeByOpponent() {
        return likeByOpponent;
    }

    public void setLikeByOpponent(Boolean likeByOpponent) {
        this.likeByOpponent = likeByOpponent;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
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

    public Long getLastSeenByOpponent() {
        return lastSeenByOpponent;
    }

    public void setLastSeenByOpponent(Long lastSeenByOpponent) {
        this.lastSeenByOpponent = lastSeenByOpponent;
    }

    protected Conversation(Parcel in) {
        key = in.readString();
        byte crushVal = in.readByte();
        crush = crushVal == 0x02 ? null : crushVal != 0x00;
        byte likeByMeVal = in.readByte();
        likeByMe = likeByMeVal == 0x02 ? null : likeByMeVal != 0x00;
        byte likeByOpponentVal = in.readByte();
        likeByOpponent = likeByOpponentVal == 0x02 ? null : likeByOpponentVal != 0x00;
        lastMessage = (Message) in.readValue(Message.class.getClassLoader());
        creationDate = in.readByte() == 0x00 ? null : in.readLong();
        opponentConversationId = in.readString();
        opponentId = in.readInt();
        lastSeenByOpponent = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "key='" + key + '\'' +
                ", crush=" + crush +
                ", likeByMe=" + likeByMe +
                ", likeByOpponent=" + likeByOpponent +
                ", lastMessage=" + lastMessage +
                ", creationDate=" + creationDate +
                ", opponentConversationId='" + opponentConversationId + '\'' +
                ", opponentId=" + opponentId +
                ", lastSeenByOpponent=" + lastSeenByOpponent +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        if (crush == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (crush ? 0x01 : 0x00));
        }
        if (likeByMe == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (likeByMe ? 0x01 : 0x00));
        }
        if (likeByOpponent == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (likeByOpponent ? 0x01 : 0x00));
        }
        dest.writeValue(lastMessage);
        if (creationDate == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(creationDate);
        }
        dest.writeString(opponentConversationId);
        dest.writeInt(opponentId);
        if (lastSeenByOpponent == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(lastSeenByOpponent);
        }


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
}