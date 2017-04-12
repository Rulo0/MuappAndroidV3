package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rulo on 12/04/17.
 */


public class FacebookImage implements Parcelable {
    SimpleDateFormat facebookDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    String id;
    Date createdTime;

    public FacebookImage() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public FacebookImage(String id, String createdTimeString) {
        this.id = id;
        try {
            this.createdTime = facebookDateFormat.parse(createdTimeString);
        } catch (Exception x) {
            this.createdTime = new Date();
        }
    }

    protected FacebookImage(Parcel in) {
        id = in.readString();
        long tmpCreatedTime = in.readLong();
        createdTime = tmpCreatedTime != -1 ? new Date(tmpCreatedTime) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(createdTime != null ? createdTime.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FacebookImage> CREATOR = new Parcelable.Creator<FacebookImage>() {
        @Override
        public FacebookImage createFromParcel(Parcel in) {
            return new FacebookImage(in);
        }

        @Override
        public FacebookImage[] newArray(int size) {
            return new FacebookImage[size];
        }
    };
}
