package me.muapp.android.Classes.Quickblox.user;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;

/**
 * Created by Seba on 16/02/2017.
 * Helper methods to manage Quickblox users communications
 */

public class QuickBloxUserHelper {

    /**
     * Updated custom data of the current user
     *
     * @param json JSON string with new custom data. See LastMessageTimestampUpdater
     */
    public static void updateUserCustomData(String json) {
        if (QuickBloxChatHelper.getInstance().isSessionActive()) {
            QBUser qbUser = new QBUser(QuickBloxChatHelper.getInstance().getCurrentUserId());
            qbUser.setCustomData(json);
            QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(QBUser qbUser, Bundle bundle) {
                    Log.d("updateUser", "onSuccess");
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("updateUser", "onError");
                }
            });
        }
    }

    /**
     * Get a Quickblox user by id
     *
     * @param id       User to retrieve
     * @param listener Listener to be notified when user loaded
     */
    public static void getUserById(int id, final QuickBloxUserListener listener) {
        QBUsers.getUser(id).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                if (listener != null) {
                    listener.onUserLoaded(qbUser);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                if (listener != null) {
                    listener.onUserLoaded(null);
                }
            }
        });
    }
}
