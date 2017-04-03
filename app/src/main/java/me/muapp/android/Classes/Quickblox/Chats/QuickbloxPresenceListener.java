package me.muapp.android.Classes.Quickblox.Chats;

import com.quickblox.chat.model.QBPresence;

/**
 * Created by Seba on 07/02/2017.
 * Notify about users presence changes
 */

public interface QuickbloxPresenceListener {
    /**
     * A user presence has change
     * @param qbPresence New presence status
     */
    void presenceChanged(QBPresence qbPresence);
}
