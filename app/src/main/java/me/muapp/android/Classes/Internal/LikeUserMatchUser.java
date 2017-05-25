
package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeUserMatchUser implements Parcelable
{

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("photo")
    @Expose
    private String photo;
    public final static Parcelable.Creator<LikeUserMatchUser> CREATOR = new Creator<LikeUserMatchUser>() {


        @SuppressWarnings({
            "unchecked"
        })
        public LikeUserMatchUser createFromParcel(Parcel in) {
            LikeUserMatchUser instance = new LikeUserMatchUser();
            instance.firstName = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.photo = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public LikeUserMatchUser[] newArray(int size) {
            return (new LikeUserMatchUser[size]);
        }

    }
    ;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(firstName);
        dest.writeValue(id);
        dest.writeValue(photo);
    }

    public int describeContents() {
        return  0;
    }

    @Override
    public String toString() {
        return "LikeUserMatchUser{" +
                "firstName='" + firstName + '\'' +
                ", id=" + id +
                ", photo='" + photo + '\'' +
                '}';
    }
}
