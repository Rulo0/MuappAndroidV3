
package me.muapp.android.Classes.Instagram.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InstagramPhoto implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("created_time")
    @Expose
    private String createdTime;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("link")
    @Expose
    private String link;
    public final static Parcelable.Creator<InstagramPhoto> CREATOR = new Creator<InstagramPhoto>() {


        @SuppressWarnings({
                "unchecked"
        })
        public InstagramPhoto createFromParcel(Parcel in) {
            InstagramPhoto instance = new InstagramPhoto();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.images = ((Images) in.readValue((Images.class.getClassLoader())));
            instance.createdTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            instance.link = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public InstagramPhoto[] newArray(int size) {
            return (new InstagramPhoto[size]);
        }

    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(images);
        dest.writeValue(createdTime);
        dest.writeValue(type);
        dest.writeValue(link);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "InstagramPhoto{" +
                "id='" + id + '\'' +
                ", images=" + images +
                ", createdTime='" + createdTime + '\'' +
                ", type='" + type + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
