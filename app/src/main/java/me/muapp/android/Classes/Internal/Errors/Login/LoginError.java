package me.muapp.android.Classes.Internal.Errors.Login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 7/04/17.
 */

public class LoginError implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("sub_status")
    @Expose
    private Integer subStatus;
    @SerializedName("error")
    @Expose
    private ErrorData error;
    public final static Parcelable.Creator<LoginError> CREATOR = new Creator<LoginError>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LoginError createFromParcel(Parcel in) {
            LoginError instance = new LoginError();
            instance.status = ((String) in.readValue((String.class.getClassLoader())));
            instance.subStatus = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.error = ((ErrorData) in.readValue((Error.class.getClassLoader())));
            return instance;
        }

        public LoginError[] newArray(int size) {
            return (new LoginError[size]);
        }

    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(Integer subStatus) {
        this.subStatus = subStatus;
    }

    public ErrorData getError() {
        return error;
    }

    public void setError(ErrorData error) {
        this.error = error;
    }

    public ErrorType getErrorType() {
        ErrorType thisError = ErrorType.Unknown;
        try {
            thisError = ErrorType.getError(getSubStatus());
        } catch (Exception x) {
            x.printStackTrace();
        }
        return thisError;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(status);
        dest.writeValue(subStatus);
        dest.writeValue(error);
    }

    public int describeContents() {
        return 0;
    }

    public enum ErrorType {
        Unknown(-1), Canceled(-999), Underage(3), NoPhoto(8), NoFriends(9),
        Expelled(10), GenericError(11), FBPermissions(101);
        private int id;

        ErrorType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }

        public static ErrorType getError(int id) {
            switch (id) {
                case -999:
                    return Canceled;
                case 3:
                    return Underage;
                case 8:
                    return NoPhoto;
                case 9:
                    return NoFriends;
                case 10:
                    return Expelled;
                case 11:
                    return GenericError;
                case 101:
                    return FBPermissions;
                default:
                    return Unknown;
            }
        }
    }

    @Override
    public String toString() {
        return "LoginError{" +
                "status='" + status + '\'' +
                ", subStatus=" + subStatus +
                ", error=" + error.toString() +
                '}';
    }
}