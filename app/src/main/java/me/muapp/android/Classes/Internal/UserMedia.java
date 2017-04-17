package me.muapp.android.Classes.Internal;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by rulo on 17/04/17.
 */

public class UserMedia implements Parcelable {
    int id;
    String path;
    Uri uri;
    Date creationDate;
    int mediaType;

    public UserMedia() {
    }

    public UserMedia(int id, String path, Uri uri, Long date, int mediaType) {
        this.id = id;
        this.path = path;
        this.uri = uri;
        this.creationDate = new Date(date);
        this.mediaType = mediaType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    protected UserMedia(Parcel in) {
        id = in.readInt();
        path = in.readString();
        uri = (Uri) in.readValue(Uri.class.getClassLoader());
        long tmpCreationDate = in.readLong();
        creationDate = tmpCreationDate != -1 ? new Date(tmpCreationDate) : null;
        mediaType = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeValue(uri);
        dest.writeLong(creationDate != null ? creationDate.getTime() : -1L);
        dest.writeInt(mediaType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserMedia> CREATOR = new Parcelable.Creator<UserMedia>() {
        @Override
        public UserMedia createFromParcel(Parcel in) {
            return new UserMedia(in);
        }

        @Override
        public UserMedia[] newArray(int size) {
            return new UserMedia[size];
        }
    };

    @Override
    public String toString() {
        return "UserMedia{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", uri=" + uri +
                ", creationDate=" + creationDate +
                ", mediaType=" + mediaType +
                '}';
    }
}
