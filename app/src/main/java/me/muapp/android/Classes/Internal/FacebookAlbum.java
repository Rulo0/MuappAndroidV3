package me.muapp.android.Classes.Internal;

import android.os.Parcel;
import android.os.Parcelable;
import me.muapp.android.Classes.Util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rulo on 28/03/17.
 */
public class FacebookAlbum implements Parcelable {
    private String id;
    private String name;
    private Date created_time;
    private List<String> photosId;
    private String firstPhotoId;

    public FacebookAlbum(String id, String name, List<String> photosId, String firstPhotoId) {
        this.id = id;
        this.name = name;
        this.photosId = photosId;
        this.firstPhotoId = firstPhotoId;
    }

    public FacebookAlbum() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotosId() {
        return photosId;
    }

    public void setPhotosId(List<String> photosId) {
        this.photosId = photosId;
    }

    public String getFirstPhotoId() {
        return firstPhotoId;
    }

    public void setFirstPhotoId(String firstPhotoId) {
        this.firstPhotoId = firstPhotoId;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public static final String JSON_ID = "id";
    public static final String JSON_NAME = "name";
    public static final String JSON_CREATED_TIME = "created_time";

    public static List<FacebookAlbum> asList(JSONArray jsonArray) {
        List<FacebookAlbum> albums = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonAlbum = jsonArray.getJSONObject(i);
                albums.add(fromJson(jsonAlbum));
            }
        } catch (JSONException e) {
            Log.e("Facebook", e.getMessage());
        }
        return albums;
    }

    public static List<String> asAlbumList(JSONArray jsonArray) {
        List<String> items = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonAlbum = jsonArray.getJSONObject(i);
                items.add(fromJsonAlbum(jsonAlbum));
            }
        } catch (JSONException e) {
            Log.e("Facebook", e.getMessage());
        }
        return items;
    }

    public static List<FacebookImage> imagesAsAlbumList(JSONArray jsonArray) {
        List<FacebookImage> items = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonAlbum = jsonArray.getJSONObject(i);
                items.add(imageFromJsonAlbum(jsonAlbum));
            }
        } catch (JSONException e) {
            Log.e("Facebook", e.getMessage());
        }
        return items;
    }

    public static FacebookAlbum fromJson(JSONObject jsonAlbum) throws JSONException {
        FacebookAlbum album = new FacebookAlbum();
        if (jsonAlbum.has(JSON_ID) && !jsonAlbum.isNull(JSON_ID))
            album.id = jsonAlbum.getString(JSON_ID);
        if (jsonAlbum.has(JSON_NAME) && !jsonAlbum.isNull(JSON_NAME))
            album.name = jsonAlbum.getString(JSON_NAME);
        if (jsonAlbum.has(JSON_CREATED_TIME) && !jsonAlbum.isNull(JSON_CREATED_TIME)) {
            album.created_time = getFacebookDate(jsonAlbum.getString(JSON_CREATED_TIME));
        }
        return album;
    }

    private static Date getFacebookDate(String date) {
        Date d = new Date(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        try {
            d = dateFormat.parse(date);
        } catch (Exception x) {
        }
        return d;
    }

    public static String fromJsonAlbum(JSONObject jsonAlbum) throws JSONException {
        String pictureId = "";
        if (jsonAlbum.has(JSON_ID) && !jsonAlbum.isNull(JSON_ID))
            pictureId = jsonAlbum.getString(JSON_ID);
        return pictureId;
    }

    public static FacebookImage imageFromJsonAlbum(JSONObject jsonAlbum) throws JSONException {
        return new FacebookImage(jsonAlbum.optString(JSON_ID), jsonAlbum.optString(JSON_CREATED_TIME));
    }

    protected FacebookAlbum(Parcel in) {
        id = in.readString();
        name = in.readString();
        long tmpCreated_time = in.readLong();
        created_time = tmpCreated_time != -1 ? new Date(tmpCreated_time) : null;
        if (in.readByte() == 0x01) {
            photosId = new ArrayList<String>();
            in.readList(photosId, String.class.getClassLoader());
        } else {
            photosId = null;
        }
        firstPhotoId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(created_time != null ? created_time.getTime() : -1L);
        if (photosId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(photosId);
        }
        dest.writeString(firstPhotoId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FacebookAlbum> CREATOR = new Parcelable.Creator<FacebookAlbum>() {
        @Override
        public FacebookAlbum createFromParcel(Parcel in) {
            return new FacebookAlbum(in);
        }

        @Override
        public FacebookAlbum[] newArray(int size) {
            return new FacebookAlbum[size];
        }
    };
}
