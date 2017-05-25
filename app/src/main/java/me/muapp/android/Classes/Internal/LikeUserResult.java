
package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeUserResult implements Parcelable {

    @SerializedName("dialog_key")
    @Expose
    private String dialogKey;
    @SerializedName("match")
    @Expose
    private LikeUserMatch likeUserMatch;
    @SerializedName("message")
    @Expose
    private String message;
    public final static Parcelable.Creator<LikeUserResult> CREATOR = new Creator<LikeUserResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LikeUserResult createFromParcel(Parcel in) {
            LikeUserResult instance = new LikeUserResult();
            instance.dialogKey = ((String) in.readValue((String.class.getClassLoader())));
            instance.likeUserMatch = ((LikeUserMatch) in.readValue((LikeUserMatch.class.getClassLoader())));
            instance.message = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public LikeUserResult[] newArray(int size) {
            return (new LikeUserResult[size]);
        }

    };

    public String getDialogKey() {
        return dialogKey;
    }

    public void setDialogKey(String dialogKey) {
        this.dialogKey = dialogKey;
    }

    public LikeUserMatch getLikeUserMatch() {
        return likeUserMatch;
    }

    public void setLikeUserMatch(LikeUserMatch likeUserMatch) {
        this.likeUserMatch = likeUserMatch;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dialogKey);
        dest.writeValue(likeUserMatch);
        dest.writeValue(message);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "LikeUserResult{" +
                "dialogKey='" + dialogKey + '\'' +
                ", likeUserMatch=" + likeUserMatch +
                ", message='" + message + '\'' +
                '}';
    }
}
