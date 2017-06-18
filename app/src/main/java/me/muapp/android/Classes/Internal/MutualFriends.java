package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fickz on 16/06/2017.
 */
public class MutualFriends implements Parcelable {
    @SerializedName("users")
    @Expose
    private List<MutualFriend> mutualFriends = null;
    public final static Parcelable.Creator<MutualFriends> CREATOR = new Creator<MutualFriends>() {
        @SuppressWarnings({
                "unchecked"
        })
        public MutualFriends createFromParcel(Parcel in) {
            MutualFriends instance = new MutualFriends();
            in.readList(instance.mutualFriends, (MutualFriend.class.getClassLoader()));
            return instance;
        }

        public MutualFriends[] newArray(int size) {
            return (new MutualFriends[size]);
        }

    };

    public List<MutualFriend> getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(List<MutualFriend> mutualFriends) {
        this.mutualFriends = mutualFriends;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mutualFriends);
    }

    public int describeContents() {
        return 0;
    }


    public class MutualFriend implements Parcelable {

        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("id")
        @Expose
        private Integer id;
        public final Parcelable.Creator<MutualFriend> CREATOR = new Creator<MutualFriend>() {
            @SuppressWarnings({
                    "unchecked"
            })
            public MutualFriend createFromParcel(Parcel in) {
                MutualFriend instance = new MutualFriend();
                instance.firstName = ((String) in.readValue((String.class.getClassLoader())));
                instance.lastName = ((String) in.readValue((String.class.getClassLoader())));
                instance.photo = ((String) in.readValue((String.class.getClassLoader())));
                instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
                return instance;
            }

            public MutualFriend[] newArray(int size) {
                return (new MutualFriend[size]);
            }

        };

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFullName() {
            return String.format("%s %s", firstName, lastName);
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(firstName);
            dest.writeValue(lastName);
            dest.writeValue(photo);
            dest.writeValue(id);
        }

        public int describeContents() {
            return 0;
        }

        @Override
        public String toString() {
            return "MutualFriend{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", photo='" + photo + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
