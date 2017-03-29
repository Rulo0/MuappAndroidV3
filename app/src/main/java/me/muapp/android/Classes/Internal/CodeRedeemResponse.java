package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 27/03/17.
 */

public class CodeRedeemResponse implements Parcelable {

    @SerializedName("authorization")
    @Expose
    private Boolean authorization;
    @SerializedName("percentage")
    @Expose
    private Integer percentage;
    @SerializedName("got_percentage")
    @Expose
    private Integer gotPercentage;
    @SerializedName("has_use_invitation")
    @Expose
    private Boolean hasUseInvitation;
    public final static Parcelable.Creator<CodeRedeemResponse> CREATOR = new Creator<CodeRedeemResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CodeRedeemResponse createFromParcel(Parcel in) {
            CodeRedeemResponse instance = new CodeRedeemResponse();
            instance.authorization = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.percentage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.gotPercentage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.hasUseInvitation = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public CodeRedeemResponse[] newArray(int size) {
            return (new CodeRedeemResponse[size]);
        }

    };

    public Boolean getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Boolean authorization) {
        this.authorization = authorization;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getGotPercentage() {
        return gotPercentage;
    }

    public void setGotPercentage(Integer gotPercentage) {
        this.gotPercentage = gotPercentage;
    }

    public Boolean getHasUseInvitation() {
        return hasUseInvitation;
    }

    public void setHasUseInvitation(Boolean hasUseInvitation) {
        this.hasUseInvitation = hasUseInvitation;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(authorization);
        dest.writeValue(percentage);
        dest.writeValue(gotPercentage);
        dest.writeValue(hasUseInvitation);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "CodeRedeemResponse{" +
                "authorization=" + authorization +
                ", percentage=" + percentage +
                ", gotPercentage=" + gotPercentage +
                ", hasUseInvitation=" + hasUseInvitation +
                '}';
    }
}
