package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rulo on 5/04/17.
 */

public class MuappUser implements Parcelable {
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
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("album")
    @Expose
    private List<String> album = null;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("common_friendships")
    @Expose
    private Integer commonFriendships;
    @SerializedName("active_conversations")
    @Expose
    private Integer activeConversations;
    @SerializedName("hours_ago")
    @Expose
    private Integer hoursAgo;
    public final static Parcelable.Creator<MuappUser> CREATOR = new Creator<MuappUser>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MuappUser createFromParcel(Parcel in) {
            MuappUser instance = new MuappUser();
            instance.education = ((String) in.readValue((String.class.getClassLoader())));
            instance.work = ((String) in.readValue((String.class.getClassLoader())));
            instance.hometown = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.audioId = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.lastName = ((String) in.readValue((String.class.getClassLoader())));
            instance.birthday = ((String) in.readValue((String.class.getClassLoader())));
            instance.firstName = ((String) in.readValue((String.class.getClassLoader())));
            instance.photo = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.album, (java.lang.String.class.getClassLoader()));
            instance.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
            instance.lastSeen = ((String) in.readValue((String.class.getClassLoader())));
            instance.commonFriendships = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.activeConversations = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.hoursAgo = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public MuappUser[] newArray(int size) {
            return (new MuappUser[size]);
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public List<String> getAlbum() {
        return album;
    }

    public void setAlbum(List<String> album) {
        this.album = album;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Integer getCommonFriendships() {
        return commonFriendships;
    }

    public void setCommonFriendships(Integer commonFriendships) {
        this.commonFriendships = commonFriendships;
    }

    public Integer getActiveConversations() {
        return activeConversations;
    }

    public void setActiveConversations(Integer activeConversations) {
        this.activeConversations = activeConversations;
    }

    public Integer getHoursAgo() {
        return hoursAgo;
    }

    public void setHoursAgo(Integer hoursAgo) {
        this.hoursAgo = hoursAgo;
    }

    public MatchingUser toMatchingUser() {
        MatchingUser user = new MatchingUser();
        user.setAlbum(getAlbum());
        user.setAudioId(getAudioId());
        user.setAverage("");
        user.setBirthday(getBirthday());
        user.setCodeUser("");
        user.setCommonFriendships(getCommonFriendships());
        user.setDescription("");
        user.setEducation(getEducation());
        user.setFakeAccount(false);
        user.setFirstName(getFirstName());
        user.setIsFbFriend(false);
        user.setHasUseInvitation(false);
        user.setHometown(getHometown());
        user.setHoursAgo(getHoursAgo());
        user.setId(getId());
        user.setInvPercentage(0);
        user.setIsQualificationed(false);
        user.setLastName(getLastName());
        user.setLastSeen(getLastSeen());
        user.setLatitude(getLatitude().toString());
        user.setLongitude(getLongitude().toString());
        user.setLocation(getLocation());
        user.setQualificationsCount(0);
        user.setWork(getWork());
        return user;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(education);
        dest.writeValue(work);
        dest.writeValue(hometown);
        dest.writeValue(location);
        dest.writeValue(audioId);
        dest.writeValue(lastName);
        dest.writeValue(birthday);
        dest.writeValue(firstName);
        dest.writeValue(photo);
        dest.writeValue(id);
        dest.writeList(album);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
        dest.writeValue(lastSeen);
        dest.writeValue(commonFriendships);
        dest.writeValue(activeConversations);
        dest.writeValue(hoursAgo);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "MuappUser{" +
                "education='" + education + '\'' +
                ", work='" + work + '\'' +
                ", hometown='" + hometown + '\'' +
                ", location=" + location +
                ", audioId=" + audioId +
                ", lastName='" + lastName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", firstName='" + firstName + '\'' +
                ", photo='" + photo + '\'' +
                ", id=" + id +
                ", album=" + album +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", lastSeen='" + lastSeen + '\'' +
                ", commonFriendships=" + commonFriendships +
                ", activeConversations=" + activeConversations +
                ", hoursAgo=" + hoursAgo +
                '}';
    }
}
