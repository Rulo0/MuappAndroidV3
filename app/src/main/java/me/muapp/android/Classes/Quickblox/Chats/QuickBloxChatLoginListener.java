package me.muapp.android.Classes.Quickblox.Chats;

/**
 * Created by Seba on 22/11/2016.
 * Notify when a Quickblox session has ben created
 */

public interface QuickBloxChatLoginListener {
    /**
     * A Quickblox session has been created.
     * @param success True if created. False if error creating session.
     */
    void onChatSessionCreated(boolean success);
}
