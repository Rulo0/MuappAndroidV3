package me.muapp.android.Classes.Chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 12/06/17.
 */

public class MuappSticker implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("image")
    @Expose
    private String image;
    public final static Parcelable.Creator<MuappSticker> CREATOR = new Creator<MuappSticker>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MuappSticker createFromParcel(Parcel in) {
            MuappSticker instance = new MuappSticker();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.image = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public MuappSticker[] newArray(int size) {
            return (new MuappSticker[size]);
        }

    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(image);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "MuappSticker{" +
                "id=" + id +
                ", image='" + image + '\'' +
                '}';
    }
}




