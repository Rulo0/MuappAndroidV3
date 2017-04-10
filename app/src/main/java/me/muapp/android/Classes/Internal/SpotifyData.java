package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 10/04/17.
 */

public class SpotifyData implements Parcelable {
    String artistName;
    String id;
    String name;
    String previewUrl;
    String thumb;

    public SpotifyData() {
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    protected SpotifyData(Parcel in) {
        artistName = in.readString();
        id = in.readString();
        name = in.readString();
        previewUrl = in.readString();
        thumb = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(previewUrl);
        dest.writeString(thumb);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SpotifyData> CREATOR = new Parcelable.Creator<SpotifyData>() {
        @Override
        public SpotifyData createFromParcel(Parcel in) {
            return new SpotifyData(in);
        }

        @Override
        public SpotifyData[] newArray(int size) {
            return new SpotifyData[size];
        }
    };
}