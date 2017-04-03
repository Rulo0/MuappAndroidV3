package me.muapp.android.Classes.Quickblox.Chats;

import com.quickblox.chat.model.QBChatDialog;

/**
 * Created by Seba on 13/02/2017.
 */

public interface QuickBloxChatDialogUpdateListener {
    /**
     * The dialog was updated
     * @param dialog Updated dialog. Null if operation failed.
     */
    void onDialogUpdated(QBChatDialog dialog);
}
