package me.muapp.android.Classes.Util;

import android.content.Context;
import android.content.SharedPreferences;

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
}
