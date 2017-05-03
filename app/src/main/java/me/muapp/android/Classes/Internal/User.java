package me.muapp.android.Classes.Internal;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;

/**
 * Created by rulo on 22/03/17.
 */

public class User implements Parcelable {

    public static User getNullUser = null;

    public ValidationResult validate() {
        if (getBirthdayDate().getTime() == 0) return ValidationResult.NoBirthday;
        if (Gender.getGender(getGender()) == Gender.Unknown) return ValidationResult.NoGender;
        if (getConfirmed() == null || !getConfirmed()) return ValidationResult.NotConfirmed;
        return ValidationResult.Ok;
    }

    public enum Gender {
        Female(0), Male(1), Unknown(-1);
        private final int id;

        Gender(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }

        public static Gender getGender(int id) {
            switch (id) {
                case 1:
                    return Male;
                case 0:
                    return Female;
                default:
                    return Unknown;
            }
        }
    }

    public enum ValidationResult {
        Ok(1), NoBirthday(2), NoGender(3), NotConfirmed(4);
        private final int id;

        ValidationResult(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }

        public static ValidationResult getValidationResult(int id) {
            switch (id) {
                case 1:
                    return Ok;
                case 2:
                    return NoBirthday;
                case 3:
                    return NoGender;
                case 4:
                    return NotConfirmed;
                default:
                    return null;
            }
        }
    }

    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("gender")
    @Expose
    private Integer gender;
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
    private int audioId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("photo_menu")
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
    @SerializedName("active_conversations")
    @Expose
    private Integer activeConversations;
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
    private List<String> album = new ArrayList<>();
    @SerializedName("age_range")
    @Expose
    private AgeRange ageRange;
    @SerializedName("pending")
    @Expose
    private Boolean pending;
    @SerializedName("notify_pokes")
    @Expose
    private Boolean notifyPokes;
    @SerializedName("notify_matches")
    @Expose
    private Boolean notifyMatches;
    @SerializedName("confirmed")
    @Expose
    private Boolean confirmed;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("matches")
    @Expose
    private Integer matches;
    @SerializedName("visits")
    @Expose
    private Integer visits;
    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("push_token")
    @Expose
    private String pushToken;
    @SerializedName("visible_age")
    @Expose
    private Boolean visibleAge;
    @SerializedName("visible_education")
    @Expose
    private Boolean visibleEducation;
    @SerializedName("visible_last_name")
    @Expose
    private Boolean visibleLastName;
    @SerializedName("visible_work")
    @Expose
    private Boolean visibleWork;
    public final static Parcelable.Creator<User> CREATOR = new Creator<User>() {


        @SuppressWarnings({
                "unchecked"
        })
        public User createFromParcel(Parcel in) {
            User instance = new User();
            instance.birthday = ((String) in.readValue((String.class.getClassLoader())));
            instance.gender = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.education = ((String) in.readValue((String.class.getClassLoader())));
            instance.work = ((String) in.readValue((String.class.getClassLoader())));
            instance.hometown = ((String) in.readValue((String.class.getClassLoader())));
            instance.location = ((Object) in.readValue((Object.class.getClassLoader())));
            instance.audioId = ((int) in.readValue((int.class.getClassLoader())));
            instance.firstName = ((String) in.readValue((String.class.getClassLoader())));
            instance.photo = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.lastName = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.activeConversations = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.hoursAgo = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.codeUser = ((String) in.readValue((String.class.getClassLoader())));
            instance.hasUseInvitation = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.invPercentage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.fakeAccount = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.average = ((String) in.readValue((String.class.getClassLoader())));
            instance.qualificationsCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.isQualificationed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            in.readList(instance.album, (java.lang.String.class.getClassLoader()));
            instance.ageRange = ((AgeRange) in.readValue((AgeRange.class.getClassLoader())));
            instance.pending = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.notifyPokes = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.notifyMatches = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.confirmed = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.likes = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.matches = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.visits = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.pushToken = ((String) in.readValue((String.class.getClassLoader())));
            instance.visibleAge = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.visibleEducation = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.visibleLastName = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.visibleWork = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            return instance;
        }

        public User[] newArray(int size) {
            return (new User[size]);
        }

    };

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getGender() {
        return gender;
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

    public String getGenderString(Context context) {
        Gender userGender = Gender.getGender(getGender());
        switch (userGender) {
            case Male:
                return context.getString(R.string.lbl_gender_male);
            case Female:
                return context.getString(R.string.lbl_gender_female);
            default:
                return context.getString(R.string.lbl_gender_unknown);
        }
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

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

    public int getAudioId() {
        return audioId;
    }

    public void setAudioId(int audioId) {
        this.audioId = audioId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhoto() {
        return photo != null ? photo : album.get(0);
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
        return lastName;
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

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public Boolean getNotifyPokes() {
        return notifyPokes;
    }

    public void setNotifyPokes(Boolean notifyPokes) {
        this.notifyPokes = notifyPokes;
    }

    public Boolean getNotifyMatches() {
        return notifyMatches;
    }

    public void setNotifyMatches(Boolean notifyMatches) {
        this.notifyMatches = notifyMatches;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getMatches() {
        return matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public Integer getVisits() {
        return visits;
    }

    public void setVisits(Integer visits) {
        this.visits = visits;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public Boolean getVisibleAge() {
        return visibleAge;
    }

    public void setVisibleAge(Boolean visibleAge) {
        this.visibleAge = visibleAge;
    }

    public Boolean getVisibleEducation() {
        return visibleEducation;
    }

    public void setVisibleEducation(Boolean visibleEducation) {
        this.visibleEducation = visibleEducation;
    }

    public Boolean getVisibleLastName() {
        return visibleLastName;
    }

    public void setVisibleLastName(Boolean visibleLastName) {
        this.visibleLastName = visibleLastName;
    }

    public Boolean getVisibleWork() {
        return visibleWork;
    }

    public void setVisibleWork(Boolean visibleWork) {
        this.visibleWork = visibleWork;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(birthday);
        dest.writeValue(gender);
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
        dest.writeValue(activeConversations);
        dest.writeValue(hoursAgo);
        dest.writeValue(codeUser);
        dest.writeValue(hasUseInvitation);
        dest.writeValue(invPercentage);
        dest.writeValue(fakeAccount);
        dest.writeValue(average);
        dest.writeValue(qualificationsCount);
        dest.writeValue(isQualificationed);
        dest.writeList(album);
        dest.writeValue(ageRange);
        dest.writeValue(pending);
        dest.writeValue(notifyPokes);
        dest.writeValue(notifyMatches);
        dest.writeValue(confirmed);
        dest.writeValue(likes);
        dest.writeValue(matches);
        dest.writeValue(visits);
        dest.writeValue(distance);
        dest.writeValue(pushToken);
        dest.writeValue(visibleAge);
        dest.writeValue(visibleEducation);
        dest.writeValue(visibleLastName);
        dest.writeValue(visibleWork);
    }

    public String getFullName() {
    return String.format("%s %s",getFirstName(),getLastName());
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "birthday='" + birthday + '\'' +
                ", gender=" + gender +
                ", education=" + education +
                ", work=" + work +
                ", hometown=" + hometown +
                ", location=" + location +
                ", audioId=" + audioId +
                ", firstName='" + firstName + '\'' +
                ", photo_menu='" + photo + '\'' +
                ", id=" + id +
                ", lastName='" + lastName + '\'' +
                ", description='" + description + '\'' +
                ", activeConversations=" + activeConversations +
                ", hoursAgo=" + hoursAgo +
                ", codeUser='" + codeUser + '\'' +
                ", hasUseInvitation=" + hasUseInvitation +
                ", invPercentage=" + invPercentage +
                ", fakeAccount=" + fakeAccount +
                ", average='" + average + '\'' +
                ", qualificationsCount=" + qualificationsCount +
                ", isQualificationed=" + isQualificationed +
                ", album=" + album +
                ", ageRange=" + ageRange +
                ", pending=" + pending +
                ", notifyPokes=" + notifyPokes +
                ", notifyMatches=" + notifyMatches +
                ", confirmed=" + confirmed +
                ", likes=" + likes +
                ", matches=" + matches +
                ", visits=" + visits +
                ", distance=" + distance +
                ", pushToken='" + pushToken + '\'' +
                ", visibleAge=" + visibleAge +
                ", visibleEducation=" + visibleEducation +
                ", visibleLastName=" + visibleLastName +
                ", visibleWork=" + visibleWork +
                '}';
    }
}