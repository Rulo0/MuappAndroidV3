package me.muapp.android.Classes.Quickblox.Login;

import com.quickblox.auth.session.QBSession;

/**
 * Created by rulo on 3/04/17.
 */

public interface QuickBloxLoginListener {
    void onSessionCreated(QBSession session);
}
