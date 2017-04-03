package me.muapp.android.Classes.Quickblox.cache;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.muapp.android.Classes.Internal.User;

/**
 * Created by Seba on 01/02/2017.
 */
public class CacheUtils {
    /**
     * Get a Realm database instance based on current user.
     * Every user has a diferent database instance.
     *
     * @param currentUser Muapp current user
     * @return Realm database instance
     */
    public static Realm getInstance(User currentUser) {
        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name(currentUser.getId() + ".realm")
                .build();

        return Realm.getInstance(myConfig);

    }
}
