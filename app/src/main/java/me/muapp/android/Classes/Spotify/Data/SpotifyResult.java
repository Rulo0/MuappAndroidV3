
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyResult implements Parcelable
{

    @SerializedName("items")
    @Expose
    private List<Song> songs = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    public final static Parcelable.Creator<SpotifyResult> CREATOR = new Creator<SpotifyResult>() {


        @SuppressWarnings({
            "unchecked"
        })
        public SpotifyResult createFromParcel(Parcel in) {
            SpotifyResult instance = new SpotifyResult();
            in.readList(instance.songs, (me.muapp.android.Classes.Spotify.Data.Song.class.getClassLoader()));
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public SpotifyResult[] newArray(int size) {
            return (new SpotifyResult[size]);
        }

    }
    ;

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(songs);
        dest.writeValue(total);
    }

    public int describeContents() {
        return  0;
    }

}
