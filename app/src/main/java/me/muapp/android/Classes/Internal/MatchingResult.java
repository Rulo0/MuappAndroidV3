
package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchingResult implements Parcelable {

    @SerializedName("users")
    @Expose
    private List<MatchingUser> matchingUsers = null;
    public final static Parcelable.Creator<MatchingResult> CREATOR = new Creator<MatchingResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MatchingResult createFromParcel(Parcel in) {
            MatchingResult instance = new MatchingResult();
            in.readList(instance.matchingUsers, (me.muapp.android.Classes.Internal.MatchingUser.class.getClassLoader()));
            return instance;
        }

        public MatchingResult[] newArray(int size) {
            return (new MatchingResult[size]);
        }

    };

    public List<MatchingUser> getMatchingUsers() {
        return matchingUsers;
    }

    public void setMatchingUsers(List<MatchingUser> matchingUsers) {
        this.matchingUsers = matchingUsers;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(matchingUsers);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "MatchingResult{" +
                "matchingUsers=" + matchingUsers +
                '}';
    }
}
