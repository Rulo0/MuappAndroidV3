package me.muapp.android.Classes.Internal.Errors.Login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 7/04/17.
 */

public class ErrorData implements Parcelable {

    @SerializedName("message")
    @Expose
    private String message;
    public final static Parcelable.Creator<ErrorData> CREATOR = new Creator<ErrorData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ErrorData createFromParcel(Parcel in) {
            ErrorData instance = new ErrorData();
            instance.message = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ErrorData[] newArray(int size) {
            return (new ErrorData[size]);
        }

    };

    @Override
    public String toString() {
        return "ErrorData{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(message);
    }

    public int describeContents() {
        return 0;
    }
}
