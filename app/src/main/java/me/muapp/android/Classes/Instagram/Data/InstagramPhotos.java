
package me.muapp.android.Classes.Instagram.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstagramPhotos implements Parcelable
{
    @SerializedName("data")
    @Expose
    private List<InstagramPhoto> data = null;
    public final static Parcelable.Creator<InstagramPhotos> CREATOR = new Creator<InstagramPhotos>() {


        @SuppressWarnings({
            "unchecked"
        })
        public InstagramPhotos createFromParcel(Parcel in) {
            InstagramPhotos instance = new InstagramPhotos();
            in.readList(instance.data, (InstagramPhoto.class.getClassLoader()));
            return instance;
        }

        public InstagramPhotos[] newArray(int size) {
            return (new InstagramPhotos[size]);
        }

    }
    ;

    public List<InstagramPhoto> getData() {
        return data;
    }

    public void setData(List<InstagramPhoto> data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(data);
    }

    public int describeContents() {
        return  0;
    }

    @Override
    public String toString() {
        return "InstagramPhotos{" +
                "data=" + data +
                '}';
    }
}
