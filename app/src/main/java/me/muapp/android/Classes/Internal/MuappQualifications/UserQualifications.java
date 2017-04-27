package me.muapp.android.Classes.Internal.MuappQualifications;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rulo on 27/04/17.
 */

public class UserQualifications implements Parcelable
{

    @SerializedName("elements")
    @Expose
    private Integer elements;
    @SerializedName("qualifications")
    @Expose
    private List<Qualification> qualifications = null;
    public final static Parcelable.Creator<UserQualifications> CREATOR = new Creator<UserQualifications>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UserQualifications createFromParcel(Parcel in) {
            UserQualifications instance = new UserQualifications();
            instance.elements = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.qualifications, (Qualification.class.getClassLoader()));
            return instance;
        }

        public UserQualifications[] newArray(int size) {
            return (new UserQualifications[size]);
        }

    }
            ;

    public Integer getElements() {
        return elements;
    }

    public void setElements(Integer elements) {
        this.elements = elements;
    }

    public List<Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(elements);
        dest.writeList(qualifications);
    }

    public int describeContents() {
        return 0;
    }

}