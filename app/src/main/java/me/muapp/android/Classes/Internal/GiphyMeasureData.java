package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 19/04/17.
 */

public class GiphyMeasureData implements Parcelable {
    int width;
    int height;

    public GiphyMeasureData() {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected GiphyMeasureData(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GiphyMeasureData> CREATOR = new Parcelable.Creator<GiphyMeasureData>() {
        @Override
        public GiphyMeasureData createFromParcel(Parcel in) {
            return new GiphyMeasureData(in);
        }

        @Override
        public GiphyMeasureData[] newArray(int size) {
            return new GiphyMeasureData[size];
        }
    };

    @Override
    public String toString() {
        return "GiphyMeasureData{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}