
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album implements Parcelable
{

    @SerializedName("album_type")
    @Expose
    private String albumType;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uri")
    @Expose
    private String uri;
    public final static Parcelable.Creator<Album> CREATOR = new Creator<Album>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Album createFromParcel(Parcel in) {
            Album instance = new Album();
            instance.albumType = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.images, (me.muapp.android.Classes.Spotify.Data.Image.class.getClassLoader()));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Album[] newArray(int size) {
            return (new Album[size]);
        }

    }
    ;

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(albumType);
        dest.writeValue(id);
        dest.writeList(images);
        dest.writeValue(name);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
