
package me.muapp.android.Classes.Youtube.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YoutubeVideo implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Id id;
    @SerializedName("snippet")
    @Expose
    private Snippet snippet;
    public final static Parcelable.Creator<YoutubeVideo> CREATOR = new Creator<YoutubeVideo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public YoutubeVideo createFromParcel(Parcel in) {
            YoutubeVideo instance = new YoutubeVideo();
            instance.id = ((Id) in.readValue((Id.class.getClassLoader())));
            instance.snippet = ((Snippet) in.readValue((Snippet.class.getClassLoader())));
            return instance;
        }

        public YoutubeVideo[] newArray(int size) {
            return (new YoutubeVideo[size]);
        }

    }
    ;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(snippet);
    }

    public int describeContents() {
        return  0;
    }

}
