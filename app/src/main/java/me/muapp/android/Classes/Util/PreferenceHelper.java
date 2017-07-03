package me.muapp.android.Classes.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;

import java.util.List;

import me.muapp.android.Classes.Chat.MuappSticker;
import me.muapp.android.Classes.Chat.MuappStickers;

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
    private final String SEARCH_PREFERENCES_CHANGED = "search_preferences_changed";
    private final String FIRST_TIME_CHAT = "fist_time_chat";
    private final String LAST_SENT_MESSAGE_TIMESTAMP = "last_sent";
    private final String LATITUDE = "latitude";
    private final String LONGITUDE = "longitude";
    private final String TUTORIAL_CANDIDATES = "tutorial_candidates";
    private final String PENDING_MATCH = "pendingMatch";
    private final String ACTIVE_CHAT = "activeChat";
    private final String STICKERS = "stickers";
    private final String TUTORIAL_CRUSH = "tutorialCrush";
    private final String TUTORIAL_RATE = "tutorialRate";
    private final String TUTORIAL_CRUSH_CONVERSATION = "tutorialCrushConversation";
    private final String TUTORIAL_MATCH_CONVERSATION = "tutorialMatchConversation";
    private final String TUTORIAL_PROFILE_COUNTER = "tutorialProfileCounter";
    private final String TUTORIAL_GATE_CARDS = "tutorialGateCards";
    private final String TUTORIAL_ADD_CONTENT = "tutorial_add_content";
    private final String REQUEST_CODE = "request_code";
    private final String HAS_ADDED_CONTENT = "has_added_content";
    private final String SEEN_DIALOGS = "seen_dialogs";
    private static final String STICKERS_PLACEHOLDER = "{\n" +
            "  \"stickers\": [\n" +
            "    {\n" +
            "      \"id\": 64,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_1_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 65,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_2_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 66,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_3_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 67,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_4_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 69,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_6_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 70,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_7_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 71,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_8_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 72,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_9_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 73,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_10_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 74,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_11_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 75,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_12_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 76,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_13_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 77,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_14_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 81,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_18_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 82,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_19_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 83,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_20_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 84,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_21_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 85,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_22_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 86,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_23_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 87,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_24_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 88,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_25_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 90,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_27_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 91,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_28_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 92,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_29_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 93,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_30_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 94,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_31_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 95,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_32_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 96,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_33_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 97,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_34_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 98,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_35_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 99,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_36_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 100,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_37_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 101,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_38_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 103,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_40_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 104,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_41_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 105,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_42_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 106,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_43_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 107,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_44_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 108,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_45_es.png\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 110,\n" +
            "      \"image\": \"https://s3-eu-west-1.amazonaws.com/dating-staging/stickers/patrocinados_47_es.png\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
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


    public void putStickers(MuappStickers muappStickers) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(STICKERS, new Gson().toJson(muappStickers));
        edit.apply();
    }

    public List<MuappSticker> getStickers() {
        MuappStickers stickers = new Gson().fromJson(preferences.getString(STICKERS, STICKERS_PLACEHOLDER), MuappStickers.class);
        return stickers.getMuappStickers();
    }

    public void putTutorialCrushDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_CRUSH, false);
        edit.apply();
    }

    public boolean getMustRequestCode() {
        return preferences.getBoolean(REQUEST_CODE, true);
    }

    public void putMustRequestCodeDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(REQUEST_CODE, false);
        edit.apply();
    }


    public boolean getTutorialCrush() {
        return preferences.getBoolean(TUTORIAL_CRUSH, true);
    }

    public boolean getHasAddedContent() {
        return preferences.getBoolean(HAS_ADDED_CONTENT, false);
    }

    public void putAddedContentEnabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(HAS_ADDED_CONTENT, true);
        edit.apply();
    }

    public void putAddedContentDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(HAS_ADDED_CONTENT, false);
        edit.apply();
    }


    public void putTutorialAddContentDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_ADD_CONTENT, false);
        edit.apply();
    }

    public boolean getTutorialAddContent() {
        return preferences.getBoolean(TUTORIAL_ADD_CONTENT, true);
    }

    public void putTutorialCardsDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_GATE_CARDS, false);
        edit.apply();
    }

    public boolean getTutorialCards() {
        return preferences.getBoolean(TUTORIAL_GATE_CARDS, true);
    }

    public void putTutorialRateDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_RATE, false);
        edit.apply();
    }

    public boolean getTutorialRate() {
        return preferences.getBoolean(TUTORIAL_RATE, true);
    }


    public int getTutorialProfileCounter() {
        return preferences.getInt(TUTORIAL_PROFILE_COUNTER, 1);
    }

    public void addCounterToProfile() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(TUTORIAL_PROFILE_COUNTER, getTutorialProfileCounter() + 1);
        edit.apply();
    }

    public void putTutorialCrushConversationDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_CRUSH_CONVERSATION, false);
        edit.apply();
    }

    public boolean getTutorialCrushConversation() {
        return preferences.getBoolean(TUTORIAL_CRUSH_CONVERSATION, true);
    }

    public void putTutorialMatchConversationDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(TUTORIAL_MATCH_CONVERSATION, false);
        edit.apply();
    }

    public boolean getTutorialMatchConversation() {
        return preferences.getBoolean(TUTORIAL_MATCH_CONVERSATION, true);
    }

    public void putFirstLoginDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(FIRST_LOGIN, false);
        edit.apply();
    }

    public void putSearchPreferencesChanged() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(SEARCH_PREFERENCES_CHANGED, true);
        edit.apply();
    }

    public void putSearchPreferencesChangedDisabled() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(SEARCH_PREFERENCES_CHANGED, false);
        edit.apply();
    }

    public Boolean getSeachPreferencesChanged() {
        return preferences.getBoolean(SEARCH_PREFERENCES_CHANGED, false);
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

    public void putCurrentActiveChat(String activeChat) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(ACTIVE_CHAT, activeChat);
        edit.apply();
    }

    public void clearCurrentActiveChat() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(ACTIVE_CHAT);
        edit.apply();
    }

    public String getCurrentActiveChat() {
        return preferences.getString(ACTIVE_CHAT, null);
    }


    public void clearPendingMatch() {
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

    public void putDialogAsSeen(String dialogKey) {
        if (!getSeenDialogs().contains(dialogKey)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(SEEN_DIALOGS, getSeenDialogs() + "," + dialogKey);
            edit.apply();
        }
    }

    private String getSeenDialogs() {
        Log.wtf("getSeenDialogs", preferences.getString(SEEN_DIALOGS, "none"));
        return preferences.getString(SEEN_DIALOGS, "");
    }

    public boolean isDialogSeen(String dialogKey) {
        return getSeenDialogs().contains(dialogKey);
    }

}
