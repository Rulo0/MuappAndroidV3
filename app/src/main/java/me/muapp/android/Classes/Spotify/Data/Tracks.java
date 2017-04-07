
package me.muapp.android.Classes.Spotify.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tracks implements Parcelable
{

    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    public final static Parcelable.Creator<Tracks> CREATOR = new Creator<Tracks>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Tracks createFromParcel(Parcel in) {
            Tracks instance = new Tracks();
            in.readList(instance.items, (me.muapp.android.Classes.Spotify.Data.Item.class.getClassLoader()));
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Tracks[] newArray(int size) {
            return (new Tracks[size]);
        }

    }
    ;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(items);
        dest.writeValue(total);
    }

    public int describeContents() {
        return  0;
    }

}
