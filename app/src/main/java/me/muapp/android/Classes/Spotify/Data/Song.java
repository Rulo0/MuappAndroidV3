
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Song implements Parcelable
{

    @SerializedName("album")
    @Expose
    private Album album;
    @SerializedName("explicit")
    @Expose
    private Boolean explicit;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("preview_url")
    @Expose
    private String previewUrl;
    @SerializedName("uri")
    @Expose
    private String uri;
    public final static Parcelable.Creator<Song> CREATOR = new Creator<Song>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Song createFromParcel(Parcel in) {
            Song instance = new Song();
            instance.album = ((Album) in.readValue((Album.class.getClassLoader())));
            instance.explicit = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.previewUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Song[] newArray(int size) {
            return (new Song[size]);
        }

    }
    ;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(album);
        dest.writeValue(explicit);
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(previewUrl);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
