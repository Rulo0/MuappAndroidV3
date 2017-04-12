
package me.muapp.android.Classes.Instagram.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LowResolution implements Parcelable
{

    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<LowResolution> CREATOR = new Creator<LowResolution>() {


        @SuppressWarnings({
            "unchecked"
        })
        public LowResolution createFromParcel(Parcel in) {
            LowResolution instance = new LowResolution();
            instance.width = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.height = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public LowResolution[] newArray(int size) {
            return (new LowResolution[size]);
        }

    }
    ;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(width);
        dest.writeValue(height);
        dest.writeValue(url);
    }

    public int describeContents() {
        return  0;
    }

    @Override
    public String toString() {
        return "LowResolution{" +
                "width=" + width +
                ", height=" + height +
                ", url='" + url + '\'' +
                '}';
    }
}
