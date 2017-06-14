package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rulo on 14/06/17.
 */

public class MuappStickers implements Parcelable {

    @SerializedName("stickers")
    @Expose
    private List<MuappSticker> muappStickers = null;
    public final static Parcelable.Creator<MuappStickers> CREATOR = new Creator<MuappStickers>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MuappStickers createFromParcel(Parcel in) {
            MuappStickers instance = new MuappStickers();
            in.readList(instance.muappStickers, (MuappSticker.class.getClassLoader()));
            return instance;
        }

        public MuappStickers[] newArray(int size) {
            return (new MuappStickers[size]);
        }

    };

    public List<MuappSticker> getMuappStickers() {
        return muappStickers;
    }

    public void setMuappStickers(List<MuappSticker> muappStickers) {
        this.muappStickers = muappStickers;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(muappStickers);
    }

    public int describeContents() {
        return 0;
    }

}

