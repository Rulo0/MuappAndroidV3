
package me.muapp.android.Classes.Instagram.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images implements Parcelable {
    @SerializedName("thumbnail")
    @Expose
    private Thumbnail thumbnail;
    @SerializedName("low_resolution")
    @Expose
    private LowResolution lowResolution;
    @SerializedName("standard_resolution")
    @Expose
    private StandardResolution standardResolution;
    public final static Parcelable.Creator<Images> CREATOR = new Creator<Images>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Images createFromParcel(Parcel in) {
            Images instance = new Images();
            instance.thumbnail = ((Thumbnail) in.readValue((Thumbnail.class.getClassLoader())));
            instance.lowResolution = ((LowResolution) in.readValue((LowResolution.class.getClassLoader())));
            instance.standardResolution = ((StandardResolution) in.readValue((StandardResolution.class.getClassLoader())));
            return instance;
        }

        public Images[] newArray(int size) {
            return (new Images[size]);
        }

    };

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public LowResolution getLowResolution() {
        return lowResolution;
    }

    public void setLowResolution(LowResolution lowResolution) {
        this.lowResolution = lowResolution;
    }

    public StandardResolution getStandardResolution() {
        return standardResolution;
    }

    public void setStandardResolution(StandardResolution standardResolution) {
        this.standardResolution = standardResolution;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(thumbnail);
        dest.writeValue(lowResolution);
        dest.writeValue(standardResolution);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Images{" +
                "thumbnail=" + thumbnail +
                ", lowResolution=" + lowResolution +
                ", standardResolution=" + standardResolution +
                '}';
    }
}
