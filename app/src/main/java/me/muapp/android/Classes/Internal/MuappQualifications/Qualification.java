package me.muapp.android.Classes.Internal.MuappQualifications;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rulo on 27/04/17.
 */

public class Qualification implements Parcelable {
    @SerializedName("stars")
    @Expose
    private Integer stars;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    private Date creationDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public Qualification() {
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Date getCreationDate() {
        try {
            Date creation = sdf.parse(getCreatedAt());
            return creation;
        } catch (Exception x) {
            return new Date();
        }
    }

    protected Qualification(Parcel in) {
        stars = in.readByte() == 0x00 ? null : in.readInt();
        userName = in.readString();
        createdAt = in.readString();
        long tmpCreationDate = in.readLong();
        creationDate = tmpCreationDate != -1 ? new Date(tmpCreationDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (stars == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(stars);
        }
        dest.writeString(userName);
        dest.writeString(createdAt);
        dest.writeLong(creationDate != null ? creationDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Qualification> CREATOR = new Parcelable.Creator<Qualification>() {
        @Override
        public Qualification createFromParcel(Parcel in) {
            return new Qualification(in);
        }

        @Override
        public Qualification[] newArray(int size) {
            return new Qualification[size];
        }
    };

    @Override
    public String toString() {
        return "Qualification{" +
                "stars=" + stars +
                ", userName='" + userName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", creationDate=" + getCreationDate().toString() +
                '}';
    }
}