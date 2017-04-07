
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable
{

    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("width")
    @Expose
    private Integer width;
    public final static Parcelable.Creator<Image> CREATOR = new Creator<Image>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Image createFromParcel(Parcel in) {
            Image instance = new Image();
            instance.height = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.width = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Image[] newArray(int size) {
            return (new Image[size]);
        }

    }
    ;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(height);
        dest.writeValue(url);
        dest.writeValue(width);
    }

    public int describeContents() {
        return  0;
    }

}
