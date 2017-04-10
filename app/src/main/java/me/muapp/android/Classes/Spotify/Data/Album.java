
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable
{

    @SerializedName("artists")
    @Expose
    private List<Artist> artists = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("uri")
    @Expose
    private String uri;
    public final static Parcelable.Creator<Album> CREATOR = new Creator<Album>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Album createFromParcel(Parcel in) {
            Album instance = new Album();
            in.readList(instance.artists, (me.muapp.android.Classes.Spotify.Data.Artist.class.getClassLoader()));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.images, (me.muapp.android.Classes.Spotify.Data.Image.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Album[] newArray(int size) {
            return (new Album[size]);
        }

    }
    ;

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getArtistNames() {
        List<String> artistNames = new ArrayList<>();
        for (Artist a : getArtists()) {
            artistNames.add(a.getName());
        }
        return TextUtils.join(", ", artistNames);
    }

    public String getHigherImage() {
        int currentSize = 0;
        String res = "";
        for (Image img : getImages()) {
            if (img.getHeight() > currentSize) {
                res = img.getUrl();
                currentSize = img.getHeight();
            }
        }
        return res;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(artists);
        dest.writeValue(id);
        dest.writeList(images);
        dest.writeValue(name);
        dest.writeValue(type);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
