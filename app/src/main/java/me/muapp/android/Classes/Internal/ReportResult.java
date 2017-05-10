package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 9/05/17.
 */

public class ReportResult implements Parcelable {

    @SerializedName("quickblox_dialog")
    @Expose
    private String quickbloxDialog;
    @SerializedName("message")
    @Expose
    private String message;
    public final static Parcelable.Creator<ReportResult> CREATOR = new Creator<ReportResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ReportResult createFromParcel(Parcel in) {
            ReportResult instance = new ReportResult();
            instance.quickbloxDialog = ((String) in.readValue((String.class.getClassLoader())));
            instance.message = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public ReportResult[] newArray(int size) {
            return (new ReportResult[size]);
        }

    };

    public String getQuickbloxDialog() {
        return quickbloxDialog;
    }

    public void setQuickbloxDialog(String quickbloxDialog) {
        this.quickbloxDialog = quickbloxDialog;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(quickbloxDialog);
        dest.writeValue(message);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "ReportResult{" +
                "quickbloxDialog='" + quickbloxDialog + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}