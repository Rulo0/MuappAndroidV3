package me.muapp.android.Classes.Quickblox.messages;

/**
 * Created by Seba on 25/11/2016.
 * Listener to receive Quickblox messages events.
 */

public interface QuickBloxMessagesListener {
    /**
     * A dialog has benn updated. Normally a new messages was received
     * @param chatDialog dialog id
     */
    void onDialogUpdated(String chatDialog);

    /**
     * A new dialog was created.
     */
    void onNewDialog();
}
