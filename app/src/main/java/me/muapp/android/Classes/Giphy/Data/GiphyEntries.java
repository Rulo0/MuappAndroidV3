
package me.muapp.android.Classes.Giphy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiphyEntries implements Parcelable
{
    @SerializedName("data")
    @Expose
    private List<GiphyEntry> giphyEntries = null;
    public final static Parcelable.Creator<GiphyEntries> CREATOR = new Creator<GiphyEntries>() {


        @SuppressWarnings({
            "unchecked"
        })
        public GiphyEntries createFromParcel(Parcel in) {
            GiphyEntries instance = new GiphyEntries();
            in.readList(instance.giphyEntries, (me.muapp.android.Classes.Giphy.Data.GiphyEntry.class.getClassLoader()));
            return instance;
        }

        public GiphyEntries[] newArray(int size) {
            return (new GiphyEntries[size]);
        }

    }
    ;

    public List<GiphyEntry> getGiphyEntries() {
        return giphyEntries;
    }

    public void setGiphyEntries(List<GiphyEntry> giphyEntries) {
        this.giphyEntries = giphyEntries;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(giphyEntries);
    }

    public int describeContents() {
        return  0;
    }

}
