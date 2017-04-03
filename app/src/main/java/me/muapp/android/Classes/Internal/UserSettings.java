package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 3/04/17.
 */

public class UserSettings implements Parcelable, Cloneable {

    @SerializedName("visible_education")
    @Expose
    private Boolean visibleEducation;
    @SerializedName("visible_work")
    @Expose
    private Boolean visibleWork;
    @SerializedName("visible_last_name")
    @Expose
    private Boolean visibleLastName;
    @SerializedName("notify_matches")
    @Expose
    private Boolean notifyMatches;
    @SerializedName("notify_pokes")
    @Expose
    private Boolean notifyPokes;
    public final static Parcelable.Creator<UserSettings> CREATOR = new Creator<UserSettings>() {


        @SuppressWarnings({
                "unchecked"
        })
        public UserSettings createFromParcel(Parcel in) {
            UserSettings instance = new UserSettings();
            instance.visibleEducation = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.visibleWork = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.visibleLastName = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.notifyMatches = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.notifyPokes = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public UserSettings[] newArray(int size) {
            return (new UserSettings[size]);
        }

    };

    public Boolean getVisibleEducation() {
        return visibleEducation;
    }

    public void setVisibleEducation(Boolean visibleEducation) {
        this.visibleEducation = visibleEducation;
    }

    public Boolean getVisibleWork() {
        return visibleWork;
    }

    public void setVisibleWork(Boolean visibleWork) {
        this.visibleWork = visibleWork;
    }

    public Boolean getVisibleLastName() {
        return visibleLastName;
    }

    public void setVisibleLastName(Boolean visibleLastName) {
        this.visibleLastName = visibleLastName;
    }

    public Boolean getNotifyMatches() {
        return notifyMatches;
    }

    public void setNotifyMatches(Boolean notifyMatches) {
        this.notifyMatches = notifyMatches;
    }

    public Boolean getNotifyPokes() {
        return notifyPokes;
    }

    public void setNotifyPokes(Boolean notifyPokes) {
        this.notifyPokes = notifyPokes;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(visibleEducation);
        dest.writeValue(visibleWork);
        dest.writeValue(visibleLastName);
        dest.writeValue(notifyMatches);
        dest.writeValue(notifyPokes);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "visibleEducation=" + visibleEducation +
                ", visibleWork=" + visibleWork +
                ", visibleLastName=" + visibleLastName +
                ", notifyMatches=" + notifyMatches +
                ", notifyPokes=" + notifyPokes +
                '}';
    }

    public UserSettings clone() throws CloneNotSupportedException {
        return (UserSettings) super.clone();
    }
}
