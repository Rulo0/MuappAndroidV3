package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rulo on 9/05/17.
 */

public class QualificationResult implements Parcelable {

    @SerializedName("authorizathion")
    @Expose
    private Boolean authorizathion;
    @SerializedName("average")
    @Expose
    private String average;
    @SerializedName("qualifications_count")
    @Expose
    private Integer qualificationsCount;
    @SerializedName("is_qualificationed")
    @Expose
    private Boolean isQualificationed;
    public final static Parcelable.Creator<QualificationResult> CREATOR = new Creator<QualificationResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public QualificationResult createFromParcel(Parcel in) {
            QualificationResult instance = new QualificationResult();
            instance.authorizathion = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.average = ((String) in.readValue((String.class.getClassLoader())));
            instance.qualificationsCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.isQualificationed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public QualificationResult[] newArray(int size) {
            return (new QualificationResult[size]);
        }

    };

    public Boolean getAuthorizathion() {
        return authorizathion;
    }

    public void setAuthorizathion(Boolean authorizathion) {
        this.authorizathion = authorizathion;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public Integer getQualificationsCount() {
        return qualificationsCount;
    }

    public void setQualificationsCount(Integer qualificationsCount) {
        this.qualificationsCount = qualificationsCount;
    }

    public Boolean getIsQualificationed() {
        return isQualificationed;
    }

    public void setIsQualificationed(Boolean isQualificationed) {
        this.isQualificationed = isQualificationed;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(authorizathion);
        dest.writeValue(average);
        dest.writeValue(qualificationsCount);
        dest.writeValue(isQualificationed);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "QualificationResult{" +
                "authorizathion=" + authorizathion +
                ", average='" + average + '\'' +
                ", qualificationsCount=" + qualificationsCount +
                ", isQualificationed=" + isQualificationed +
                '}';
    }
}