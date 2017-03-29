package me.muapp.android.Classes.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import me.muapp.android.Classes.Internal.User;

/**
 * Created by rulo on 23/03/17.
 */

public class UserHelper {
    private SharedPreferences preferences;
    private static final String LOGGED_USER = "LOGGED_USER";
    Context context;

    public UserHelper(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("MUAPP_PREF", Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(LOGGED_USER, json);
        edit.commit();
    }



    public void logOut() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }


    public User getLoggedUser() {
        Gson gson = new Gson();
        String json = preferences.getString(LOGGED_USER, "");
        Log.wtf("getLoggedUser", json);
        User user = gson.fromJson(json, User.class);
        return user;
    }
}
