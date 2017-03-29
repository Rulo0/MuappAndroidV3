package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 22/03/17.
 */

public class AgeRange implements Parcelable {
    @SerializedName("1")
    @Expose
    private Integer _1;
    @SerializedName("2")
    @Expose
    private Integer _2;
    public final static Parcelable.Creator<AgeRange> CREATOR = new Creator<AgeRange>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AgeRange createFromParcel(Parcel in) {
            AgeRange instance = new AgeRange();
            instance._1 = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance._2 = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public AgeRange[] newArray(int size) {
            return (new AgeRange[size]);
        }

    };

    public Integer get1() {
        return _1;
    }

    public void set1(Integer _1) {
        this._1 = _1;
    }

    public Integer get2() {
        return _2;
    }

    public void set2(Integer _2) {
        this._2 = _2;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_1);
        dest.writeValue(_2);
    }

    public int describeContents() {
        return 0;
    }
}
