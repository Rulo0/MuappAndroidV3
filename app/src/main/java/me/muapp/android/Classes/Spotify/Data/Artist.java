
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Artist implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uri")
    @Expose
    private String uri;
    public final static Parcelable.Creator<Artist> CREATOR = new Creator<Artist>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Artist createFromParcel(Parcel in) {
            Artist instance = new Artist();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            instance.uri = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Artist[] newArray(int size) {
            return (new Artist[size]);
        }

    }
    ;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(uri);
    }

    public int describeContents() {
        return  0;
    }

}
