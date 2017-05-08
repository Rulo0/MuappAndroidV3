
package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.muapp.android.Classes.Util.Utils;

public class MatchingUser implements Parcelable {

    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("hometown")
    @Expose
    private String hometown;
    @SerializedName("location")
    @Expose
    private Object location;
    @SerializedName("audio_id")
    @Expose
    private Integer audioId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("hours_ago")
    @Expose
    private Integer hoursAgo;
    @SerializedName("code_user")
    @Expose
    private String codeUser;
    @SerializedName("has_use_invitation")
    @Expose
    private Boolean hasUseInvitation;
    @SerializedName("inv_percentage")
    @Expose
    private Integer invPercentage;
    @SerializedName("fake_account")
    @Expose
    private Boolean fakeAccount;
    @SerializedName("average")
    @Expose
    private String average;
    @SerializedName("qualifications_count")
    @Expose
    private Integer qualificationsCount;
    @SerializedName("is_qualificationed")
    @Expose
    private Boolean isQualificationed;
    @SerializedName("album")
    @Expose
    private List<String> album = null;
    @SerializedName("common_friendships")
    @Expose
    private Integer commonFriendships;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("quickblox_id")
    @Expose
    private Object quickbloxId;
    @SerializedName("is_fb_friend")
    @Expose
    private Boolean isFbFriend;

    private static final String HIDDEN_STRING = "__hiddenField__";
    public final static Parcelable.Creator<MatchingUser> CREATOR = new Creator<MatchingUser>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MatchingUser createFromParcel(Parcel in) {
            MatchingUser instance = new MatchingUser();
            instance.education = ((String) in.readValue((String.class.getClassLoader())));
            instance.work = ((String) in.readValue((String.class.getClassLoader())));
            instance.hometown = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.audioId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.firstName = ((String) in.readValue((String.class.getClassLoader())));
            instance.photo = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.lastName = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.hoursAgo = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.codeUser = ((String) in.readValue((String.class.getClassLoader())));
            instance.hasUseInvitation = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.invPercentage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.fakeAccount = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.average = ((String) in.readValue((String.class.getClassLoader())));
            instance.qualificationsCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.isQualificationed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            in.readList(instance.album, (java.lang.String.class.getClassLoader()));
            instance.commonFriendships = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.longitude = ((String) in.readValue((String.class.getClassLoader())));
            instance.latitude = ((String) in.readValue((String.class.getClassLoader())));
            instance.lastSeen = ((String) in.readValue((String.class.getClassLoader())));
            instance.birthday = ((String) in.readValue((String.class.getClassLoader())));
            instance.quickbloxId = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.isFbFriend = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public MatchingUser[] newArray(int size) {
            return (new MatchingUser[size]);
        }

    };

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Integer getAudioId() {
        return audioId;
    }

    public void setAudioId(Integer audioId) {
        this.audioId = audioId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getLastName() {
        return !lastName.equals(HIDDEN_STRING) ? this.lastName : "";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHoursAgo() {
        return hoursAgo;
    }

    public void setHoursAgo(Integer hoursAgo) {
        this.hoursAgo = hoursAgo;
    }

    public String getCodeUser() {
        return codeUser;
    }

    public void setCodeUser(String codeUser) {
        this.codeUser = codeUser;
    }

    public Boolean getHasUseInvitation() {
        return hasUseInvitation;
    }

    public void setHasUseInvitation(Boolean hasUseInvitation) {
        this.hasUseInvitation = hasUseInvitation;
    }

    public Integer getInvPercentage() {
        return invPercentage;
    }

    public void setInvPercentage(Integer invPercentage) {
        this.invPercentage = invPercentage;
    }

    public Boolean getFakeAccount() {
        return fakeAccount;
    }

    public void setFakeAccount(Boolean fakeAccount) {
        this.fakeAccount = fakeAccount;
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

    public List<String> getAlbum() {
        return album;
    }

    public void setAlbum(List<String> album) {
        this.album = album;
    }

    public Integer getCommonFriendships() {
        return commonFriendships;
    }

    public void setCommonFriendships(Integer commonFriendships) {
        this.commonFriendships = commonFriendships;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Date getLastSeenDate() {
        Date result = null;
        if (birthday != null) {
            try {
                result = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(getLastSeen());
            } catch (Exception x) {
            }
        }
        return result;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Object getQuickbloxId() {
        return quickbloxId;
    }

    public void setQuickbloxId(Object quickbloxId) {
        this.quickbloxId = quickbloxId;
    }

    public Boolean getIsFbFriend() {
        return isFbFriend;
    }

    public void setIsFbFriend(Boolean isFbFriend) {
        this.isFbFriend = isFbFriend;
    }

    public int getAge() {
        if (getBirthdayDate() == null) return 0;
        return Utils.getDiffYears(getBirthdayDate(), new Date());
    }

    public Date getBirthdayDate() {
        Date result = null;
        if (birthday != null) {
            try {
                result = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
            } catch (Exception x) {
            }
        }
        return result;
    }

    public boolean getVisibleEducation() {
        if (getEducation() != null && !getEducation().equals("null") && !getEducation().equals(HIDDEN_STRING)) {
            return true;
        }
        return false;
    }

    public boolean getVisibleWork() {
        if (getWork() != null && !getWork().equals("null") && !getWork().equals(HIDDEN_STRING)) {
            return true;
        }
        return false;
    }

    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(education);
        dest.writeValue(work);
        dest.writeValue(hometown);
        dest.writeValue(location);
        dest.writeValue(audioId);
        dest.writeValue(firstName);
        dest.writeValue(photo);
        dest.writeValue(id);
        dest.writeValue(lastName);
        dest.writeValue(description);
        dest.writeValue(hoursAgo);
        dest.writeValue(codeUser);
        dest.writeValue(hasUseInvitation);
        dest.writeValue(invPercentage);
        dest.writeValue(fakeAccount);
        dest.writeValue(average);
        dest.writeValue(qualificationsCount);
        dest.writeValue(isQualificationed);
        dest.writeList(album);
        dest.writeValue(commonFriendships);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
        dest.writeValue(lastSeen);
        dest.writeValue(birthday);
        dest.writeValue(quickbloxId);
        dest.writeValue(isFbFriend);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "MatchingUser{" +
                "education='" + education + '\'' +
                ", work='" + work + '\'' +
                ", hometown='" + hometown + '\'' +
                ", location=" + location +
                ", audioId=" + audioId +
                ", firstName='" + firstName + '\'' +
                ", photo='" + photo + '\'' +
                ", id=" + id +
                ", lastName='" + lastName + '\'' +
                ", description='" + description + '\'' +
                ", hoursAgo=" + hoursAgo +
                ", codeUser='" + codeUser + '\'' +
                ", hasUseInvitation=" + hasUseInvitation +
                ", invPercentage=" + invPercentage +
                ", fakeAccount=" + fakeAccount +
                ", average='" + average + '\'' +
                ", qualificationsCount=" + qualificationsCount +
                ", isQualificationed=" + isQualificationed +
                ", album=" + album +
                ", commonFriendships=" + commonFriendships +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", lastSeen='" + lastSeen + '\'' +
                ", birthday='" + birthday + '\'' +
                ", quickbloxId=" + quickbloxId +
                ", isFbFriend=" + isFbFriend +
                '}';
    }
}
