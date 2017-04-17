
package me.muapp.android.Classes.Giphy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GiphyEntry implements Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private Images images;
    public final static Parcelable.Creator<GiphyEntry> CREATOR = new Creator<GiphyEntry>() {


        @SuppressWarnings({
            "unchecked"
        })
        public GiphyEntry createFromParcel(Parcel in) {
            GiphyEntry instance = new GiphyEntry();
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.images = ((Images) in.readValue((Images.class.getClassLoader())));
            return instance;
        }

        public GiphyEntry[] newArray(int size) {
            return (new GiphyEntry[size]);
        }

    }
    ;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(id);
        dest.writeValue(images);
    }

    public int describeContents() {
        return  0;
    }

}
