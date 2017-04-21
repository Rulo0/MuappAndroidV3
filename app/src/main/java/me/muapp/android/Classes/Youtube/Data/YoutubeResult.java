
package me.muapp.android.Classes.Youtube.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeResult implements Parcelable {
    @SerializedName("items")
    @Expose
    private List<YoutubeVideo> youtubeVideos = null;
    public final static Parcelable.Creator<YoutubeResult> CREATOR = new Creator<YoutubeResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public YoutubeResult createFromParcel(Parcel in) {
            YoutubeResult instance = new YoutubeResult();
            in.readList(instance.youtubeVideos, (me.muapp.android.Classes.Youtube.Data.YoutubeVideo.class.getClassLoader()));
            return instance;
        }

        public YoutubeResult[] newArray(int size) {
            return (new YoutubeResult[size]);
        }

    };

    public List<YoutubeVideo> getYoutubeVideos() {
        return youtubeVideos;
    }

    public void setYoutubeVideos(List<YoutubeVideo> youtubeVideos) {
        this.youtubeVideos = youtubeVideos;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(youtubeVideos);
    }

    public int describeContents() {
        return 0;
    }

}
