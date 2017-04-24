package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 24/04/17.
 */

public class MuappQuote implements Parcelable {
    String key;
    String captionEng;
    String captionSpa;
    int order;

    public MuappQuote() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCaptionEng() {
        return captionEng;
    }

    public void setCaptionEng(String captionEng) {
        this.captionEng = captionEng;
    }

    public String getCaptionSpa() {
        return captionSpa;
    }

    public void setCaptionSpa(String captionSpa) {
        this.captionSpa = captionSpa;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    protected MuappQuote(Parcel in) {
        key = in.readString();
        captionEng = in.readString();
        captionSpa = in.readString();
        order = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(captionEng);
        dest.writeString(captionSpa);
        dest.writeInt(order);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MuappQuote> CREATOR = new Parcelable.Creator<MuappQuote>() {
        @Override
        public MuappQuote createFromParcel(Parcel in) {
            return new MuappQuote(in);
        }

        @Override
        public MuappQuote[] newArray(int size) {
            return new MuappQuote[size];
        }
    };

    @Override
    public String toString() {
        return "MuappQuote{" +
                "key='" + key + '\'' +
                ", captionEng='" + captionEng + '\'' +
                ", captionSpa='" + captionSpa + '\'' +
                ", order=" + order +
                '}';
    }
}