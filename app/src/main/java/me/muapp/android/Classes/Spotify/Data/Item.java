
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item implements Parcelable
{

    @SerializedName("album")
    @Expose
    private Album album;
    @SerializedName("artists")
    @Expose
    private List<Artist> artists = null;
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
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("uri")
    @Expose
    private String uri;
    public final static Parcelable.Creator<Item> CREATOR = new Creator<Item>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Item createFromParcel(Parcel in) {
            Item instance = new Item();
            instance.album = ((Album) in.readValue((Album.class.getClassLoader())));
            in.readList(instance.artists, (me.muapp.android.Classes.Spotify.Data.Artist.class.getClassLoader()));
            instance.explicit = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.previewUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Item[] newArray(int size) {
            return (new Item[size]);
        }

    }
    ;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(album);
        dest.writeList(artists);
        dest.writeValue(explicit);
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(previewUrl);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
