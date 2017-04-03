package me.muapp.android.Classes.Quickblox.Chats;

import com.quickblox.users.model.QBUser;

/**
 * Created by Seba on 02/02/2017.
 */

public interface QuickbloxUserRequestListener {
    void onUserLoaded(QBUser user);
}
