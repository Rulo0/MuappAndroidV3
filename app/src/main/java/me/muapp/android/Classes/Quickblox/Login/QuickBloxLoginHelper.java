package me.muapp.android.Classes.Quickblox.Login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.model.QBUser;

import java.util.Date;

import me.muapp.android.Classes.Util.UserHelper;

/**
 * Created by rulo on 3/04/17.
 */

public class QuickBloxLoginHelper {
    public static void login(Context ctx, Bundle savedInstanceState, final QuickBloxLoginListener listener) {
        boolean wasAppRestored = savedInstanceState != null;
        boolean isQbSessionActive = isSessionActive();
        final boolean needToRestoreSession = wasAppRestored || !isQbSessionActive;
        if (needToRestoreSession) {
            performLogin(ctx, listener);
        } else {
            getSession(ctx, listener);
        }
    }

    private static void getSession(final Context ctx, final QuickBloxLoginListener listener) {
        QBAuth.getSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                try {
                    if (qbSession.getUserId() > 0) {
                        listener.onSessionCreated(qbSession);
                    } else {
                        performLogin(ctx, listener);
                    }
                } catch (Exception x) {
                    performLogin(ctx, listener);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                performLogin(ctx, listener);
            }
        });
    }

    private static boolean isSessionActive() {
        try {
            String token = QBAuth.getBaseService().getToken();
            Date expirationDate = QBAuth.getBaseService().getTokenExpirationDate();
            Log.wtf("isSessionActive", token + " " + expirationDate.toString());
            if (TextUtils.isEmpty(token)) {
                return false;
            }
            if (System.currentTimeMillis() >= expirationDate.getTime()) {
                return false;
            }
            return true;
        } catch (Exception x) {
            Log.wtf("isSessionActive", x.getMessage());
        }
        return false;
    }


    private static void performLogin(final Context ctx, final QuickBloxLoginListener listener) {
        if (new UserHelper(ctx).getLoggedUser().getId() != null)
            ((Activity) ctx).runOnUiThread(new Runnable() {
                public void run() {
                    int uId = new UserHelper(ctx).getLoggedUser().getId();
                    QBUser usr = new QBUser("usermuapp-" + uId, "passMuapp-" + uId);
                    QBAuth.createSession(usr).performAsync(new QBEntityCallback<QBSession>() {
                        @Override
                        public void onSuccess(QBSession qbSession, Bundle bundle) {
                            SubscribeService.subscribeToPushes(ctx, false);
                            listener.onSessionCreated(qbSession);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            listener.onSessionCreated(null);
                        }
                    });
                }
            });
    }
}