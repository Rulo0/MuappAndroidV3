package me.muapp.android.Classes.Quickblox.Chats;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.muapp.android.Classes.Quickblox.Login.QuickBloxLoginHelper;
import me.muapp.android.Classes.Quickblox.Login.QuickBloxLoginListener;
import me.muapp.android.Classes.Quickblox.dialog.QuickBloxDialogHelper;
import me.muapp.android.Classes.Quickblox.dialog.VerboseQbChatConnectionListener;
import me.muapp.android.Classes.Util.UserHelper;

/**
 * Created by Seba on 22/11/2016.
 * Helper methods for managing chats Quickblox communication
 */


public class QuickBloxChatHelper {

    public static final int DIALOG_ITEMS_PER_PAGE = 100;
    public static final String NAME1 = "name_1";
    public static final String LASTNAME1 = "last_name_1";
    public static final String PHOTO1 = "photo_1";
    public static final String EXTERNALID1 = "external_id_1";
    public static final String SEEN1 = "seen_1";
    public static final String DELETEDAT1 = "deleted_at_1";
    public static final String NAME2 = "name_2";
    public static final String LASTNAME2 = "last_name_2";
    public static final String PHOTO2 = "photo_2";
    public static final String EXTERNALID2 = "external_id_2";
    public static final String SEEN2 = "seen_2";
    public static final String DELETEDAT2 = "deleted_at_2";
    public static final String CRUSH = "crush";
    public static final String LIKE1 = "like_1";
    public static final String LIKE2 = "like_2";
    private static QuickBloxChatHelper instance;

    private QBChatService qbChatService;
    private ArrayList<QuickBloxChatLoginListener> loginListeners = new ArrayList<>();
    private ConnectionListener chatConnectionListener;
    private Integer currentUserId = null;

    private QBRoster chatRoster;
    private QBSubscriptionListener subscriptionListener;
    private QBRosterListener rosterListener;
    private ArrayList<QuickbloxPresenceListener> presenceListeners = new ArrayList<>();

    private QuickBloxChatHelper() {
        QBChatService.setDebugEnabled(true); // enable chat logging
        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
        QBChatService.setDefaultAutoSendPresenceInterval(60); //enable sending online status every 60 sec to keep connection alive
        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatServiceConfigurationBuilder.setSocketTimeout(60); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        chatServiceConfigurationBuilder.setAutojoinEnabled(false);
        chatServiceConfigurationBuilder.setAutoMarkDelivered(true);
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);
        qbChatService = QBChatService.getInstance();
        qbChatService.setUseStreamManagement(true);
        chatConnectionListener = new VerboseQbChatConnectionListener() {
            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                notifyChatLogin(true);
            }
        };

        subscriptionListener = new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested(int userID) {
                try {
                    chatRoster.confirmSubscription(userID);
                } catch (SmackException.NotConnectedException e) {

                } catch (SmackException.NotLoggedInException e) {

                } catch (XMPPException e) {

                } catch (SmackException.NoResponseException e) {

                }
            }
        };

        rosterListener = new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> collection) {
            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {
            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {
            }

            @Override
            public void presenceChanged(QBPresence qbPresence) {
                for (QuickbloxPresenceListener p : presenceListeners) {
                    p.presenceChanged(qbPresence);
                }
            }
        };
    }

    /**
     * Get singleton instance
     *
     * @return Singleton instance
     */
    public static synchronized QuickBloxChatHelper getInstance() {
        if (instance == null) {
            instance = new QuickBloxChatHelper();
        }
        return instance;
    }


    /**
     * Gets current Quickblox user id
     *
     * @return
     */
    public Integer getCurrentUserId() {
        QBUser user = getCurrentUser();
        if (user != null) {
            return user.getId();
        } else {
            return currentUserId;
        }
    }

    /**
     * Gets current Quickblox user
     *
     * @return
     */
    public QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }

    /**
     * Add a listener to be notified about Quickblox connection events
     *
     * @param listener
     */
    public void addConnectionListener(ConnectionListener listener) {
        qbChatService.addConnectionListener(listener);
    }

    /**
     * Remove connection listener
     *
     * @param listener
     */
    public void removeConnectionListener(ConnectionListener listener) {
        qbChatService.removeConnectionListener(listener);
    }

    /**
     * Adds a listener to be notified about chat login events
     *
     * @param listener
     */
    public void addLoginListener(@NonNull QuickBloxChatLoginListener listener) {
        loginListeners.add(listener);
        if (isSessionActive()) { //Notify directly if session is active
            listener.onChatSessionCreated(true);
        }
    }

    public void removeLoginListener(@NonNull QuickBloxChatLoginListener listener) {
        loginListeners.remove(listener);
    }

    private void notifyChatLogin(boolean success) {
        for (int i = 0; i < loginListeners.size(); i++)
            loginListeners.get(0).onChatSessionCreated(success);
    }

    /**
     * Login to Quickblox chat service
     *
     * @param ctx
     */
    public void loginToChat(final Context ctx) {
        QuickBloxLoginHelper.login(ctx, null, new QuickBloxLoginListener() {
            @Override
            public void onSessionCreated(QBSession qbSession) {
                if (qbSession == null) {
                    notifyChatLogin(false);
                } else {
                    loginToChat(ctx, qbSession);
                }
            }
        });
    }


    private void loginToChat(@NonNull final Context context, @NonNull final QBSession session) {
        addConnectionListener(chatConnectionListener);
        currentUserId = session.getUserId();
        if (isSessionActive()) {
            notifyChatLogin(true);
        } else {
            Integer usrId = new UserHelper(context).getLoggedUser().getId();
            QBUser user = new QBUser("usermuapp-" + usrId, "passMuapp-" + usrId);
            user.setId(session.getUserId());
            try {
                qbChatService.login(user, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        chatRoster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
                        chatRoster.addRosterListener(rosterListener);
                        notifyChatLogin(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        e.printStackTrace();
                        notifyChatLogin(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                notifyChatLogin(false);
            }
        }

    }//loginToChat

    /**
     * Logout from Quickblox chat service
     */
    public void logout() {
        removeConnectionListener(chatConnectionListener);
        if (!isSessionActive()) {
            return;
        }
        qbChatService.logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                //qbChatService.destroy();
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }//logout

    /**
     * Get Quickblox dialogs
     *
     * @param listener Listener to be notified when dialogs are loaded.
     */
    public void getDialogs(final QuickBloxChatDialogsListener listener) {
        getDialogs(listener, 0, new ArrayList<QBChatDialog>());
    }

    private void getDialogs(final QuickBloxChatDialogsListener listener, final int skip, final ArrayList<QBChatDialog> dialogs) {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setLimit(DIALOG_ITEMS_PER_PAGE);
        customObjectRequestBuilder.setSkip(skip);
        QBRestChatService.getChatDialogs(null, customObjectRequestBuilder)
                .performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                        Log.wtf("DialogFound", "Count: " + qbChatDialogs.size());
                        dialogs.addAll(qbChatDialogs);
                        if (qbChatDialogs.size() < DIALOG_ITEMS_PER_PAGE) {
                            listener.onDialogsLoaded(dialogs, true);
                        } else {
                            getDialogs(listener, skip + DIALOG_ITEMS_PER_PAGE, dialogs);
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.wtf("DialogFound", e.getMessage());
                        e.printStackTrace();
                        listener.onDialogsLoaded(dialogs, false);
                    }
                });

    }//getDialogs

    /**
     * Get a dialog by its id
     *
     * @param dialogId Quickblox dialog id to be retrieved
     * @param listener Listener to be notified when dialog loaded
     */
    public void getDialogById(String dialogId, final QuickBloxChatDialogListener listener) {

        QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog dialog, Bundle bundle) {
                if (listener != null) {
                    if (dialog != null)
                        listener.onDialogLoaded(dialog);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                if (listener != null) {
                    listener.onDialogLoaded(null);
                }
            }
        });

    }//getDialogs

    /**
     * Update a dialog in Quickblox
     *
     * @param qbChatDialog Dialog to be updated
     * @param listener     Listener to be notified when updated
     */
    public void updateDialog(QBChatDialog qbChatDialog, final QuickBloxChatDialogUpdateListener listener) {
        QBRestChatService.updateGroupChatDialog(qbChatDialog, null).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog dialog, Bundle bundle) {
                if (listener != null) {
                    listener.onDialogUpdated(dialog);
                }
                Log.wtf("updateDialog", "Success " + dialog.getDialogId());
                Log.wtf("updateDialog", "Success " + dialog.getCustomData().toString());
            }

            @Override
            public void onError(QBResponseException e) {
                Log.wtf("updateDialog", "Error " + e.getMessage());
                e.printStackTrace();
                if (listener != null) {
                    listener.onDialogUpdated(null);
                }
            }
        });
    }

    /**
     * Update a dialog in Quickblox. It will be executed in current thread.
     *
     * @param qbChatDialog Dialog to be updated
     * @param listener     Listener to be notified when updated
     */
    public void updateDialogSync(QBChatDialog qbChatDialog, final QuickBloxChatDialogUpdateListener listener) {
        try {
            QBChatDialog chatDialog = QBRestChatService.updateGroupChatDialog(qbChatDialog, null).perform();
            if (listener != null) {
                listener.onDialogUpdated(chatDialog);
            }
        } catch (QBResponseException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onDialogUpdated(null);
            }
        }
    }

    public void setLikeByUser(QBChatDialog qbDialog, long opponentId,
                              QuickBloxChatDialogUpdateListener listener) {
        if (qbDialog.getCustomData() != null) {
            try {
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID1)) == opponentId) {
                    Log.wtf("performLike", opponentId + " - mod: " + EXTERNALID2);
                    qbDialog.setCustomData(generateCustomData(qbDialog));
                    qbDialog.getCustomData().put(LIKE2, true);
                }
            } catch (Exception e) {
                Log.wtf("performLike", e.getMessage());
                e.printStackTrace();
            }
            try {
                Log.wtf("performLike", qbDialog.getCustomData().getString(EXTERNALID1));
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID2)) == opponentId) {
                    Log.wtf("performLike", opponentId + " - mod: " + EXTERNALID1);
                    qbDialog.setCustomData(generateCustomData(qbDialog));
                    qbDialog.getCustomData().put(LIKE1, true);
                }
            } catch (Exception e) {
                Log.wtf("performLike", e.getMessage());
                e.printStackTrace();
            }
            updateDialog(qbDialog, listener);
        }
    }

    /**
     * Mark a dialog as seen by the user
     *
     * @param qbDialog              Dialog to be updated
     * @param currentUserExternalId Current user Muapp id
     * @param listener              Listener to be notified when updated
     */
    public void setSeen(QBChatDialog qbDialog, long currentUserExternalId,
                        QuickBloxChatDialogUpdateListener listener) {
        if (qbDialog.getCustomData() != null) {
            try {
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID1)) == currentUserExternalId) {
                    qbDialog.setCustomData(generateCustomData(qbDialog));
                    qbDialog.getCustomData().put(SEEN1, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Log.wtf("setSeen", qbDialog.getCustomData().getString(EXTERNALID2));
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID2)) == currentUserExternalId) {
                    qbDialog.setCustomData(generateCustomData(qbDialog));
                    qbDialog.getCustomData().put(SEEN2, true);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
            updateDialog(qbDialog, listener);
        }
    }//setSeen

    /**
     * Mark a dialog as deleted now
     *
     * @param qbDialog              Dialog to be updated
     * @param currentUserExternalId Current user Muapp id
     * @param listener              Listener to be notified when updated
     */
    public void setDialogDeleted(QBChatDialog qbDialog, long currentUserExternalId,
                                 QuickBloxChatDialogUpdateListener listener) {
        if (qbDialog.getCustomData() != null) {
            try {
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID1)) == currentUserExternalId) {
                    qbDialog.setCustomData(generateCustomData(qbDialog));
                    qbDialog.getCustomData().put(DELETEDAT1, new Date());
                }
            } catch (Exception e) {
            }
            try {
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID2)) == currentUserExternalId) {
                    qbDialog.setCustomData(generateCustomData(qbDialog));
                    qbDialog.getCustomData().put(DELETEDAT2, new Date());
                }
            } catch (Exception e) {
            }
            updateDialogSync(qbDialog, listener);
        }
    }//setSeen

    private QBDialogCustomData generateCustomData(QBChatDialog dialog) {
        if (dialog == null || dialog.getCustomData() == null) {
            return null;
        } else {
            QBDialogCustomData customData = new QBDialogCustomData("Dialogs");
            customData.put(NAME1, dialog.getCustomData().getString(NAME1));
            customData.put(LASTNAME1, dialog.getCustomData().getString(LASTNAME1));
            customData.put(PHOTO1, dialog.getCustomData().getString(PHOTO1));
            customData.put(EXTERNALID1, dialog.getCustomData().getString(EXTERNALID1));
            customData.put(LIKE1, dialog.getCustomData().getBoolean(LIKE1));
            customData.put(LIKE2, dialog.getCustomData().getBoolean(LIKE2));
            Boolean seen = dialog.getCustomData().getBoolean(SEEN1);
            customData.put(SEEN1, seen != null && seen);
            try {
                if (dialog.getCustomData().getDate(DELETEDAT1, null) != null) {
                    customData.put(DELETEDAT1, dialog.getCustomData().getDate(DELETEDAT1, null));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            customData.put(NAME2, dialog.getCustomData().getString(NAME2));
            customData.put(LASTNAME2, dialog.getCustomData().getString(LASTNAME2));
            customData.put(PHOTO2, dialog.getCustomData().getString(PHOTO2));
            customData.put(EXTERNALID2, dialog.getCustomData().getString(EXTERNALID2));
            seen = dialog.getCustomData().getBoolean(SEEN2);
            customData.put(SEEN2, seen != null && seen);
            try {
                if (dialog.getCustomData().getDate(DELETEDAT2, null) != null) {
                    customData.put(DELETEDAT2, dialog.getCustomData().getDate(DELETEDAT2, null));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return customData;
        }
    }


    /**
     * Check if Quickblox Chat service seession is active
     *
     * @return True if active. False otherwise
     */
    public boolean isSessionActive() {
        return qbChatService != null &&
                qbChatService.isLoggedIn();
    }

    /**
     * Get total Quickblox messages unread by the user.
     *
     * @param listener Listener to be notified.
     */
    public void getTotalUnreadMessagesCount(@NonNull final QuickBloxUnreadMessagesListener listener) {

        QBRestChatService.getTotalUnreadMessagesCount(null, null).performAsync(new QBEntityCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer, Bundle bundle) {
                listener.onUnreadMessageCount(integer);
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Get user data by id
     *
     * @param userId   Quickblox user id
     * @param listener Listener to be notified
     */
    public void getUser(int userId, @NonNull final QuickbloxUserRequestListener listener) {
        QBUsers.getUser(userId).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                listener.onUserLoaded(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                listener.onUserLoaded(null);
            }
        });
    }

    /**
     * Adds a listener to be notified about users presence changes
     *
     * @param listener
     */
    public void addPresenceListener(QuickbloxPresenceListener listener) {
        presenceListeners.add(listener);
    }

    public void removePresenceListener(QuickbloxPresenceListener listener) {
        presenceListeners.remove(listener);
    }

    /**
     * Checks if a user is online right now
     *
     * @param userID Quickblox user id to check.
     * @return true if user is online. False otherwise.
     */
    public boolean isOnline(int userID) {
        if (chatRoster != null) {
            QBPresence presence = chatRoster.getPresence(userID);
            if (presence == null) {
                // No user in your roster
                return false;
            }
            return presence.getType() == QBPresence.Type.online;
        }
        return false;
    }

    /**
     * Add user to conctact list. It provides access to presence events.
     * The other user has to accept this request.
     *
     * @param userID User to add.
     */
    public void addUserToContactList(int userID) {
        if (chatRoster != null) {
            if (chatRoster.contains(userID)) {
                try {
                    chatRoster.subscribe(userID);
                } catch (SmackException.NotConnectedException e) {

                }
            } else {
                try {
                    chatRoster.createEntry(userID, null);
                } catch (XMPPException e) {
                } catch (SmackException.NotLoggedInException e) {
                } catch (SmackException.NotConnectedException e) {
                } catch (SmackException.NoResponseException e) {
                }
            }
        }
    }//addUserToContactList

    /**
     * Add all the users of the given dialogs to the contact list
     *
     * @param dialogs
     */
    public void addToContactList(List<QBChatDialog> dialogs) {
        if (dialogs != null) {
            Integer userId = QuickBloxChatHelper.getInstance().getCurrentUserId();
            for (QBChatDialog d : dialogs) {
                if (d.getOccupants() != null) {
                    int i = 0;
                    while (i < d.getOccupants().size()) {
                        if (d.getOccupants().get(i).equals(userId)) {
                            addUserToContactList(d.getOccupants().get(i));
                        }
                        i++;
                    }
                }
            }
        }
    }//addToContactList

    /**
     * Execute in background thread
     * Delete all messages from de given dialog
     *
     * @param dialogId Quickblox dialog id
     * @return
     */
    public boolean deleteMessagesFromDialog(String dialogId, long currentUserExternalId, QuickBloxChatDialogUpdateListener listener) {

        try {
            QBChatDialog chatDialog = QBRestChatService.getChatDialogById(dialogId).perform();
            ArrayList<QBChatMessage> chatMessages = QuickBloxDialogHelper.getInstance().loadAllChatHistorySync(chatDialog, new ArrayList<QBChatMessage>());
            if (chatMessages.size() > 0) {
                Set<String> ids = new HashSet<String>();
                for (QBChatMessage m : chatMessages) {
                    ids.add(m.getId());
                }
                QBRestChatService.deleteMessages(ids, false).perform();
            }
            setDialogDeleted(chatDialog, currentUserExternalId, listener);
            return true;
        } catch (QBResponseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
