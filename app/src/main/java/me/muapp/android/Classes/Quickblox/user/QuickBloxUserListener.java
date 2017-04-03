package me.muapp.android.Classes.Quickblox.user;

import com.quickblox.users.model.QBUser;

/**
 * Created by Seba on 16/02/2017.
 */

public interface QuickBloxUserListener {
    /**
     * User has been loaded.
     * @param user Required user. Null if not found.
     */
    void onUserLoaded(QBUser user);
}
