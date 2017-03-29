package me.muapp.android.Classes.Util;

/**
 * Created by rulo on 22/03/17.
 */

public class Constants {
    public static class Facebook {
        public static final String PERMISSIONS = "public_profile,user_friends,user_birthday,user_work_history,user_education_history,user_hometown,user_photos,email";
        public static final String ALBUM_PATH = "me/albums";
        public static final String PHOTOS_FORMAT = "%s/photos";
        public static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    }

    public static class URL {
        public static final String TERMS = "https://muapp.me/terms";
        public static final String PRIVACY = "https://muapp.me/privacy";
        public static final String HELP = "https://muapp.zendesk.com";
    }
}
