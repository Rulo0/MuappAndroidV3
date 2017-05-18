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

    public String getFullName() {
        return String.format("%s %s", name, lastName);
    }

    public ConversationItem() {
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

    protected ConversationItem(Parcel in) {
        key = in.readString();
        profilePicture = in.readString();
        name = in.readString();
        lastName = in.readString();
        conversation = (Conversation) in.readValue(Conversation.class.getClassLoader());
        pushToken = in.readString();
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
                '}';
    }
}