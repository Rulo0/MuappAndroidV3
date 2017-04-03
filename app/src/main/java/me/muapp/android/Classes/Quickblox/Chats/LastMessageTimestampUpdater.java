package me.muapp.android.Classes.Quickblox.Chats;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.muapp.android.Classes.Quickblox.user.QuickBloxUserHelper;
import me.muapp.android.Classes.Util.PreferenceHelper;

/**
 * Created by Seba on 16/02/2017.
 * Helper class. Update last message sent of every dialog in Quickblox user profile.
 * It is user for parallel conversations count.
 */
public class LastMessageTimestampUpdater {

    private static final int WAIT_TO_SEND = 30000; //30 secs

    public static final String INTERVAL = "interval";
    public static final String CONVERSATIONS = "conversations";
    public static final String ID = "id";
    public static final String TS = "ts";

    PreferenceHelper preferences;
    Handler handler;

    // {"interval":24,"conversations":[{"id":"58a36a48a28f9a4e6000002b","ts":1487243078.685769}]}
    JSONObject json;
    int hoursAgo;

    Timer timer;

    /**
     * Constructor
     *
     * @param preferences
     * @param hoursAgo    hoursAgo from Muapp user profile
     */
    public LastMessageTimestampUpdater(PreferenceHelper preferences, int hoursAgo) {
        this.preferences = preferences;
        handler = new Handler();
        this.hoursAgo = hoursAgo;
        try {
            json = new JSONObject(preferences.getdialogsLastSentMessageTimestamp());
        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONObject();
        }
    }

    /**
     * A new message was sent by the user
     *
     * @param dialogId
     */
    public void messageSent(String dialogId) {
        cancelTimer();
        updateJson(dialogId);
        startTimer();
    }

    /**
     * Chat activity is stopped
     */
    public void onStop() {
        cancelTimer();
        updateQuickbloxUser();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateQuickbloxUser();
                    }
                });
            }
        }, WAIT_TO_SEND);
    }

    private void updateJson(String dialogId) {
        try {
            json.put(INTERVAL, hoursAgo);
            JSONArray array = json.optJSONArray(CONVERSATIONS);
            if (array == null) {
                array = new JSONArray();
                json.put(CONVERSATIONS, array);
            } else {
                int i = 0;
                JSONObject dialog = null;
                while (i < array.length() && dialog == null) {
                    if (array.getJSONObject(i).getString(ID).equalsIgnoreCase(dialogId)) {
                        dialog = array.getJSONObject(i);
                        dialog.put(TS, new Date().getTime() / 1000);
                    }
                    i++;
                }
                if (dialog == null) {
                    JSONObject dialogJson = new JSONObject();
                    dialogJson.put(ID, dialogId);
                    dialogJson.put(TS, new Date().getTime() / 1000);
                    array.put(dialogJson);
                }
            }
            preferences.putdialogsLastSentMessageTimestamp(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateQuickbloxUser() {
       QuickBloxUserHelper.updateUserCustomData(json.toString());
    }


}
