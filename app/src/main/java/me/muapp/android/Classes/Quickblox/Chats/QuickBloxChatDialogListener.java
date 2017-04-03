package me.muapp.android.Classes.Quickblox.Chats;

import com.quickblox.chat.model.QBChatDialog;

/**
 * Created by Seba on 22/11/2016.
 */
public interface QuickBloxChatDialogListener {
    /**
     * Requested dialog was loaded.
     * @param dialog Required dialog. Null if dialog not found.
     */
    void onDialogLoaded(QBChatDialog dialog);
}
