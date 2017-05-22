package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 22/05/17.
 */

public class ChatReferences implements Parcelable {
    String myConversationRef;
    String yourConversationRef;

    public ChatReferences(String myConversationRef, String yourConversationRef) {
        this.myConversationRef = myConversationRef;
        this.yourConversationRef = yourConversationRef;
    }

    public String getMyConversationRef() {
        return myConversationRef;
    }

    public String getYourConversationRef() {
        return yourConversationRef;
    }

    protected ChatReferences(Parcel in) {
        myConversationRef = in.readString();
        yourConversationRef = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myConversationRef);
        dest.writeString(yourConversationRef);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ChatReferences> CREATOR = new Parcelable.Creator<ChatReferences>() {
        @Override
        public ChatReferences createFromParcel(Parcel in) {
            return new ChatReferences(in);
        }

        @Override
        public ChatReferences[] newArray(int size) {
            return new ChatReferences[size];
        }
    };

    @Override
    public String toString() {
        return "ChatReferences{" +
                "myConversationRef='" + myConversationRef + '\'' +
                ", yourConversationRef='" + yourConversationRef + '\'' +
                '}';
    }
}
