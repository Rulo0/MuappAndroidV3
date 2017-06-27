package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 16/05/17.
 */

public class ConversationItem implements Parcelable {
    String key;
    String profilePicture;
    String name;
    String lastName;
    Conversation conversation;
    String pushToken;
    Boolean hasInstagramToken;
    Boolean online;

    public ConversationItem() {
    }

    public String getFullName() {
        return this.name + " " + this.lastName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public Boolean getHasInstagramToken() {
        return hasInstagramToken;
    }

    public void setHasInstagramToken(Boolean hasInstagramToken) {
        this.hasInstagramToken = hasInstagramToken;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    protected ConversationItem(Parcel in) {
        key = in.readString();
        profilePicture = in.readString();
        name = in.readString();
        lastName = in.readString();
        conversation = (Conversation) in.readValue(Conversation.class.getClassLoader());
        pushToken = in.readString();
        byte hasInstagramTokenVal = in.readByte();
        hasInstagramToken = hasInstagramTokenVal == 0x02 ? null : hasInstagramTokenVal != 0x00;
        byte onlineVal = in.readByte();
        online = onlineVal == 0x02 ? null : onlineVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(profilePicture);
        dest.writeString(name);
        dest.writeString(lastName);
        dest.writeValue(conversation);
        dest.writeString(pushToken);
        if (hasInstagramToken == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (hasInstagramToken ? 0x01 : 0x00));
        }
        if (online == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (online ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ConversationItem> CREATOR = new Parcelable.Creator<ConversationItem>() {
        @Override
        public ConversationItem createFromParcel(Parcel in) {
            return new ConversationItem(in);
        }

        @Override
        public ConversationItem[] newArray(int size) {
            return new ConversationItem[size];
        }
    };

    @Override
    public String toString() {
        return "ConversationItem{" +
                "key='" + key + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", conversation=" + conversation +
                ", pushToken='" + pushToken + '\'' +
                ", hasInstagramToken=" + hasInstagramToken +
                ", online=" + online +
                '}';
    }
}