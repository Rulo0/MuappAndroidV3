
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpotifyResult implements Parcelable
{

    @SerializedName("tracks")
    @Expose
    private Tracks tracks;
    public final static Parcelable.Creator<SpotifyResult> CREATOR = new Creator<SpotifyResult>() {


        @SuppressWarnings({
            "unchecked"
        })
        public SpotifyResult createFromParcel(Parcel in) {
            SpotifyResult instance = new SpotifyResult();
            instance.tracks = ((Tracks) in.readValue((Tracks.class.getClassLoader())));
            return instance;
        }

        public SpotifyResult[] newArray(int size) {
            return (new SpotifyResult[size]);
        }

    }
    ;

    public Tracks getTracks() {
        return tracks;
    }

    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tracks);
    }

    public int describeContents() {
        return  0;
    }

}
