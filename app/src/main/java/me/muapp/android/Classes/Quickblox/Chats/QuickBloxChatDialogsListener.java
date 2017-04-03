package me.muapp.android.Classes.Quickblox.Chats;

import com.quickblox.chat.model.QBChatDialog;

import java.util.List;

/**
 * Created by Seba on 22/11/2016.
 */

public interface QuickBloxChatDialogsListener {
    /**
     * Required dialogs was loaded
     * @param dialogs List of required dialogs
     * @param success True if operation was successfull. False otherwise.
     */
    void onDialogsLoaded(List<QBChatDialog> dialogs, boolean success);
}
