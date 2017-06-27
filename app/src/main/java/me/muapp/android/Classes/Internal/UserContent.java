package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 10/04/17.
 */

public class UserContent implements Parcelable {
    String key;
    String catContent;
    String videoTitle;
    String comment;
    String contentUrl;
    Long createdAt;
    Integer likes;
    String thumbUrl;
    String videoId;
    String storageName;
    String videoThumbStorage;
    String quoteId;
    SpotifyData spotifyData;
    GiphyMeasureData giphyMeasureData;
    Integer audioLenght;

    public UserContent() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCatContent() {
        return catContent;
    }

    public void setCatContent(String catContent) {
        this.catContent = catContent;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getVideoThumbStorage() {
        return videoThumbStorage;
    }

    public void setVideoThumbStorage(String videoThumbStorage) {
        this.videoThumbStorage = videoThumbStorage;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public SpotifyData getSpotifyData() {
        return spotifyData;
    }

    public void setSpotifyData(SpotifyData spotifyData) {
        this.spotifyData = spotifyData;
    }

    public GiphyMeasureData getGiphyMeasureData() {
        return giphyMeasureData;
    }

    public void setGiphyMeasureData(GiphyMeasureData giphyMeasureData) {
        this.giphyMeasureData = giphyMeasureData;
    }

    public Integer getAudioLenght() {
        return audioLenght;
    }

    public void setAudioLenght(Integer audioLenght) {
        this.audioLenght = audioLenght;
    }

    protected UserContent(Parcel in) {
        key = in.readString();
        catContent = in.readString();
        videoTitle = in.readString();
        comment = in.readString();
        contentUrl = in.readString();
        createdAt = in.readByte() == 0x00 ? null : in.readLong();
        likes = in.readByte() == 0x00 ? null : in.readInt();
        thumbUrl = in.readString();
        videoId = in.readString();
        storageName = in.readString();
        videoThumbStorage = in.readString();
        quoteId = in.readString();
        spotifyData = (SpotifyData) in.readValue(SpotifyData.class.getClassLoader());
        giphyMeasureData = (GiphyMeasureData) in.readValue(GiphyMeasureData.class.getClassLoader());
        audioLenght = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(catContent);
        dest.writeString(videoTitle);
        dest.writeString(comment);
        dest.writeString(contentUrl);
        if (createdAt == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(createdAt);
        }
        if (likes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(likes);
        }
        dest.writeString(thumbUrl);
        dest.writeString(videoId);
        dest.writeString(storageName);
        dest.writeString(videoThumbStorage);
        dest.writeString(quoteId);
        dest.writeValue(spotifyData);
        dest.writeValue(giphyMeasureData);
        if (audioLenght == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(audioLenght);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserContent> CREATOR = new Parcelable.Creator<UserContent>() {
        @Override
        public UserContent createFromParcel(Parcel in) {
            return new UserContent(in);
        }

        @Override
        public UserContent[] newArray(int size) {
            return new UserContent[size];
        }
    };

    @Override
    public String toString() {
        return "UserContent{" +
                "key='" + key + '\'' +
                ", catContent='" + catContent + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", comment='" + comment + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", createdAt=" + createdAt +
                ", likes=" + likes +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", videoId='" + videoId + '\'' +
                ", storageName='" + storageName + '\'' +
                ", videoThumbStorage='" + videoThumbStorage + '\'' +
                ", quoteId='" + quoteId + '\'' +
                ", spotifyData=" + spotifyData +
                ", giphyMeasureData=" + giphyMeasureData +
                ", audioLenght=" + audioLenght +
                '}';
    }
}