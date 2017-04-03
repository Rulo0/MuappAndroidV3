package me.muapp.android.Classes.Quickblox;

import android.Manifest;
import android.content.Context;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.SubscribePushStrategy;

/**
 * Created by rulo on 3/04/17.
 */

public class QuickbloxHelper {
    public static final String POKE_STICKER = "{TypeSticker}";
    public static final String POKE_IMAGE = "{TypeImage}";
    public static final String POKE_VOICE = "{TypeVoice}";
    public static final String POKE_VOICE_SIZE = "attachment_size";
    public static final String POKE_DATE = "{TypeDate}";

    public static final String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    //Voice cache directory
    public static final String VOICE_NOTES_CACHE_DIRECTORY = "voiceNotes";

 /*   //Quickblox configuration PRODUCTION
    private static final String QB_APP_ID = "53859";
    private static final String QB_AUTH_KEY = "5TpQXvcrX6uxbfp";
    private static final String QB_AUTH_SECRET = "fuc63NNjGK436hv";
    private static final String QB_ACCOUNT_KEY = "whdzsqHDqSfzvp7Ycsyu";*/


    //Quickblox configuration Staging
    private static final String QB_APP_ID = "49676";
    private static final String QB_AUTH_KEY = "5zEjTabNPh6eyvX";
    private static final String QB_AUTH_SECRET = "a3qTFNNErM8d2Ox";
    private static final String QB_ACCOUNT_KEY = "Da5Hnyz3BpSXUnzkRZYM";


    public static void init(Context applicationContext) {
        QBSettings.getInstance().init(applicationContext,
                QB_APP_ID,
                QB_AUTH_KEY,
                QB_AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(QB_ACCOUNT_KEY);
        QBSettings.getInstance().setEnablePushNotification(true);
        QBSettings.getInstance().setSubscribePushStrategy(SubscribePushStrategy.ALWAYS);
    }
}
