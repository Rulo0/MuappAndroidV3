
package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeUserMatch implements Parcelable {

    @SerializedName("matcher_id")
    @Expose
    private Integer matcherId;
    @SerializedName("user")
    @Expose
    private LikeUserMatchUser likeUserMatchUser;
    public final static Parcelable.Creator<LikeUserMatch> CREATOR = new Creator<LikeUserMatch>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LikeUserMatch createFromParcel(Parcel in) {
            LikeUserMatch instance = new LikeUserMatch();
            instance.matcherId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.likeUserMatchUser = ((LikeUserMatchUser) in.readValue((LikeUserMatchUser.class.getClassLoader())));
            return instance;
        }

        public LikeUserMatch[] newArray(int size) {
            return (new LikeUserMatch[size]);
        }

    };

    public Integer getMatcherId() {
        return matcherId;
    }

    public void setMatcherId(Integer matcherId) {
        this.matcherId = matcherId;
    }

    public LikeUserMatchUser getLikeUserMatchUser() {
        return likeUserMatchUser;
    }

    public void setLikeUserMatchUser(LikeUserMatchUser likeUserMatchUser) {
        this.likeUserMatchUser = likeUserMatchUser;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(matcherId);
        dest.writeValue(likeUserMatchUser);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "LikeUserMatch{" +
                "matcherId=" + matcherId +
                ", likeUserMatchUser=" + likeUserMatchUser +
                '}';
    }
}
