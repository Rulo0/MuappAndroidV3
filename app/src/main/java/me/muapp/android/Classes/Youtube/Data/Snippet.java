
package me.muapp.android.Classes.Youtube.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Snippet implements Parcelable
{

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumbnails")
    @Expose
    private Thumbnails thumbnails;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    public final static Parcelable.Creator<Snippet> CREATOR = new Creator<Snippet>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Snippet createFromParcel(Parcel in) {
            Snippet instance = new Snippet();
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.thumbnails = ((Thumbnails) in.readValue((Thumbnails.class.getClassLoader())));
            instance.channelTitle = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Snippet[] newArray(int size) {
            return (new Snippet[size]);
        }

    }
    ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(thumbnails);
        dest.writeValue(channelTitle);
    }

    public int describeContents() {
        return  0;
    }

}
