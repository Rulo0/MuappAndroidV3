package me.muapp.android.Classes.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

/**
 * Created by rulo on 22/03/17.
 */

public class PreferenceHelper {
    private SharedPreferences preferences;
    private final String FB_TOKEN = "facebookToken";
    private final String FB_EXPIRATION = "facebookExpiration";
    private final String FB_USER_ID = "facebookId";
    private final String GCM_TOKEN = "gcm_token";
    private final String FIRST_LOGIN = "first_login";
    private final String FIRST_TIME_CHAT = "fist_time_chat";
    private final String LAST_SENT_MESSAGE_TIMESTAMP = "last_sent";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String TUTORIAL_CANDIDATES = "tutorial_candidates";
    private final String PENDING_MATCH = "pendingMatch";
    Context context;

    public PreferenceHelper(Context context) {
        preferences = context.getSharedPreferences("MUAPP_PREF", Context.MODE_PRIVATE);
        this.context = context;
    }

    public void clear() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    public void putFirstLoginDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(FIRST_LOGIN, false);
        edit.apply();
    }

    public String getdialogsLastSentMessageTimestamp() {
        return preferences.getString(LAST_SENT_MESSAGE_TIMESTAMP, "{}");
    }

    public void putFirstTimeChatDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(FIRST_TIME_CHAT, false);
        edit.apply();
    }

    public void putPendingMatch(String pendingMatch) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(PENDING_MATCH, pendingMatch);
        edit.apply();
    }

    public String getPendingMatch() {
        return preferences.getString(PENDING_MATCH, "");
    }


    public void cleatPendingMatch() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(PENDING_MATCH);
        edit.apply();
    }

    public Boolean getFirstTimeChat() {
        return preferences.getBoolean(FIRST_TIME_CHAT, true);
    }

    public void putdialogsLastSentMessageTimestamp(String s) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(LAST_SENT_MESSAGE_TIMESTAMP, s);
        edit.apply();
    }

    public Boolean getFirstLogin() {
        return preferences.getBoolean(FIRST_LOGIN, true);
    }

    public void putFacebookId(String s) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(FB_USER_ID, s);
        edit.apply();
    }

    public String getFacebookId() {
        return preferences.getString(FB_USER_ID, null);
    }

    public void putGCMToken(String s) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(GCM_TOKEN, s);
        edit.apply();
    }

    public String getGCMToken() {
        return preferences.getString(GCM_TOKEN, null);
    }


    public void putFacebookToken(String s) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(FB_TOKEN, s);
        edit.apply();
    }

    public String getFacebookToken() {
        return preferences.getString(FB_TOKEN, null);
    }

    public void putFacebookTokenExpiration(Long l) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(FB_EXPIRATION, l);
        edit.apply();
    }

    public Long getFacebookTokenExpiration() {
        return preferences.getLong(FB_EXPIRATION, -1);
    }


    public void putLocation(Location location) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(LATITUDE, Double.doubleToLongBits(location.getLatitude()));
        edit.putLong(LONGITUDE, Double.doubleToLongBits(location.getLongitude()));
        edit.apply();
    }

    public Location getLocation() {
        Location l = new Location("LAST_LOCATION");
        l.setLatitude(Double.longBitsToDouble(preferences.getLong(LATITUDE, 0)));
        l.setLongitude(Double.longBitsToDouble(preferences.getLong(LONGITUDE, 0)));
        return l;
    }

    public void putCandidatesTutorialDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_CANDIDATES, false);
        edit.apply();
    }

    public boolean getCandidatesTutorial() {
        return preferences.getBoolean(TUTORIAL_CANDIDATES, true);
    }

}
