package me.muapp.android.Classes.Quickblox.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import me.muapp.android.Classes.Quickblox.messages.QuickBloxMessagesListener;

import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.CRUSH;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.DELETEDAT1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.DELETEDAT2;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.EXTERNALID1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.EXTERNALID2;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.LASTNAME1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.LASTNAME2;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.LIKE1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.LIKE2;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.NAME1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.NAME2;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.PHOTO1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.PHOTO2;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.SEEN1;
import static me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper.SEEN2;

/**
 * Created by Seba on 20/01/2017.
 * Helper methods to manage Realm DialogCacheObject table
 */
public class DialogCacheHelper {

    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    public static final String CREATING_DIALOG = "creating_dialog";

    // "deleted_at_1" -> "2017-02-16T11:16:27Z"
    private static SimpleDateFormat deletedAtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * Converts a Quickblox dialog to a cache dialog
     *
     * @param qbDialog              Quickblox dialog to convert
     * @param currentUserExternalId Current user Muapp ID
     * @return
     */
    public static DialogCacheObject dialogToCache(QBChatDialog qbDialog, long currentUserExternalId) {
        DialogCacheObject d = new DialogCacheObject();
        d.setDialogId(qbDialog.getDialogId());
        d.setCreatedAt(qbDialog.getCreatedAt());
        d.setLastMessage(qbDialog.getLastMessage());
        d.setLastMessageDateSent(qbDialog.getLastMessageDateSent());
        d.setLastMessageUserId(qbDialog.getLastMessageUserId());
        if (qbDialog.getOccupants() != null) {
            if (qbDialog.getOccupants().size() > 0) {
                d.setOccupantId1(qbDialog.getOccupants().get(0));
            }
            if (qbDialog.getOccupants().size() > 1) {
                d.setOccupantId2(qbDialog.getOccupants().get(1));
            }
        }
        d.setUnreadMessageCount(qbDialog.getUnreadMessageCount());
        d.setCreatedAt(qbDialog.getCreatedAt());
        if (qbDialog.getCustomData() != null) {
            try {
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID1)) == currentUserExternalId) {
                    d.setOpponentExternalId(Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID2)));
                    Boolean myLike = qbDialog.getCustomData().getBoolean(LIKE1);
                    d.setMyLike(myLike != null && myLike);
                    Boolean opponentLike = qbDialog.getCustomData().getBoolean(LIKE2);
                    d.setOpponnentLike(opponentLike != null && opponentLike);
                    String name = qbDialog.getCustomData().getString(NAME2);
                    String lastName = qbDialog.getCustomData().getString(LASTNAME2);
                    if (!TextUtils.isEmpty(lastName)) {
                        name += " " + lastName;
                    }
                    d.setOpponentName(name);
                    d.setOpponentPhoto(qbDialog.getCustomData().getString(PHOTO2));
                    Boolean seen = qbDialog.getCustomData().getBoolean(SEEN1);
                    d.setSeen(seen != null && seen);
                    Boolean isCrush = qbDialog.getCustomData().getBoolean(CRUSH);
                    d.setCrush(isCrush != null && isCrush);
                    Boolean isLike1 = qbDialog.getCustomData().getBoolean(LIKE1);
                    d.setLike1(isLike1 != null && isLike1);
                    Boolean isLike2 = qbDialog.getCustomData().getBoolean(LIKE2);
                    d.setLike2(isLike2 != null && isLike2);
                    String date = qbDialog.getCustomData().getString(DELETEDAT1);
                    if (date != null) {
                        try {
                            d.setDeletedAt(deletedAtFormat.parse(date.replaceAll("Z$", "+0000")));
                        } catch (Exception x) {
                            d.setDeletedAt(null);
                        }
                    } else {
                        d.setDeletedAt(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID2)) == currentUserExternalId) {
                    d.setOpponentExternalId(Long.parseLong(qbDialog.getCustomData().getString(EXTERNALID1)));
                    Boolean myLike = qbDialog.getCustomData().getBoolean(LIKE2);
                    d.setMyLike(myLike != null && myLike);
                    Boolean opponentLike = qbDialog.getCustomData().getBoolean(LIKE1);
                    d.setOpponnentLike(opponentLike != null && opponentLike);
                    String name = qbDialog.getCustomData().getString(NAME1);
                    String lastName = qbDialog.getCustomData().getString(LASTNAME1);
                    if (!TextUtils.isEmpty(lastName)) {
                        name += " " + lastName;
                    }
                    d.setOpponentName(name);
                    d.setOpponentPhoto(qbDialog.getCustomData().getString(PHOTO1));
                    Boolean seen = qbDialog.getCustomData().getBoolean(SEEN2);
                    d.setSeen(seen != null && seen);
                    Boolean isCrush = qbDialog.getCustomData().getBoolean(CRUSH);
                    d.setCrush(isCrush != null && isCrush);
                    Boolean isLike1 = qbDialog.getCustomData().getBoolean(LIKE1);
                    d.setLike1(isLike1 != null && isLike1);
                    Boolean isLike2 = qbDialog.getCustomData().getBoolean(LIKE2);
                    d.setLike2(isLike2 != null && isLike2);

                    String date = qbDialog.getCustomData().getString(DELETEDAT2);
                    if (date != null) {
                        d.setDeletedAt(deletedAtFormat.parse(date.replaceAll("Z$", "+0000")));
                    } else {
                        d.setDeletedAt(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return d;

    }

    /**
     * Get all user dialog from database cache filtering by name.
     *
     * @param realm          Realm database instance
     * @param name           Username to filter dialogs. Empty for no filtering.
     * @param changeListener Listener to be notified of results changes
     * @return List of dialogs
     */
    public static RealmResults<DialogCacheObject> getDialogs(Realm realm, @NonNull String name, RealmChangeListener<RealmResults<DialogCacheObject>> changeListener) {
        RealmQuery<DialogCacheObject> query = realm.where(DialogCacheObject.class);
        if (!TextUtils.isEmpty(name)) {
            query = query.contains("opponentName", name, Case.INSENSITIVE);
        }

        RealmResults<DialogCacheObject> result = query.findAllAsync();
        result.addChangeListener(changeListener);
        return result;
    }

    public static void deleteAllDialogs(Realm realm, RealmChangeListener<RealmResults<DialogCacheObject>> changeListener) {
        RealmQuery<DialogCacheObject> query = realm.where(DialogCacheObject.class);
        RealmResults<DialogCacheObject> result = query.findAll();
        realm.beginTransaction();
        result.deleteAllFromRealm();
        realm.commitTransaction();
        Log.wtf("Deleting all", "dialogs");
    }

    /**
     * Get all dialogs marked as unseen by the current user
     *
     * @param realm Realm database instance
     * @return List of unseen dialgos
     */
    public static RealmResults<DialogCacheObject> getUnseenDialogs(Realm realm) {
        if (!realm.isClosed()) {
            RealmQuery<DialogCacheObject> query = realm.where(DialogCacheObject.class);
            query = query.equalTo("seen", false).isNull("lastMessageUserId");
            RealmResults<DialogCacheObject> result = query.findAllAsync();
            return result;
        }
        return null;
    }

    /**
     * Delete a dialog from database
     *
     * @param realm    Realm database instance
     * @param dialogId Dialog ID to delete
     */
    public static void removeDialog(Realm realm, final String dialogId) {
        try {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    DialogCacheObject dialogById = getDialogById(realm, dialogId);
                    if (dialogById != null)
                        dialogById.deleteFromRealm();
                }
            });
        } catch (Exception x) {
        }
    }

    /**
     * Set dialog cache with this dialogs. Dialogs not in this list can be deleted.
     *
     * @param realm                 Realm database instance
     * @param dialogs               List of dialogs to be in cache
     * @param currentUserExternalId Current user muapp id
     * @param deleteInNoPresent     Delete dialogs in cache but no in this list
     */
    public static void setDialogs(Realm realm, final List<QBChatDialog> dialogs, final long currentUserExternalId, final boolean deleteInNoPresent) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (deleteInNoPresent) {
                    deleteDialogsNotInList(realm, dialogs);
                }

                for (QBChatDialog d : dialogs) {
                    DialogCacheObject dialog = dialogToCache(d, currentUserExternalId);
                    realm.copyToRealmOrUpdate(dialog);
                    Log.wtf("SetDialogs", d.getDialogId());
                }
                Log.wtf("SetDialogs", "Total " + dialogs.size());
            }
        });

    }//setDialogs

    public static void setDialogsDirectChat(Realm realm, final QBChatDialog dlg, final long currentUserExternalId, final boolean deleteInNoPresent, final Context context) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<QBChatDialog> dialogs = new ArrayList<QBChatDialog>();
                dialogs.add(dlg);
                DialogCacheObject dialog = dialogToCache(dlg, currentUserExternalId);
                Log.wtf("setDialogsDirectChat", dialog.toString());
                realm.copyToRealmOrUpdate(dialog);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
          /*      Intent intent = new Intent(context.getApplicationContext(), ChatActivity_.class);
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dlg.getDialogId());
                (context).startActivity(intent);*/
            }
        });
    }//setDialogs

    public static void setDialogsDirectChatRemoveActivity(Realm realm, final QBChatDialog dlg, final long currentUserExternalId, final boolean deleteInNoPresent, final Context context) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                List<QBChatDialog> dialogs = new ArrayList<QBChatDialog>();
                dialogs.add(dlg);
                DialogCacheObject dialog = dialogToCache(dlg, currentUserExternalId);
                Log.wtf("setDialogsDirectChat", dialog.toString());
                realm.copyToRealmOrUpdate(dialog);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
            /*    Intent intent = new Intent(context.getApplicationContext(), ChatActivity_.class);
                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dlg.getDialogId());
                (context).startActivity(intent);
                ((Activity) context).setResult(ProfileActivity.PROFILE_CRUSH);
                ((Activity) context).finish();*/
            }
        });
    }//setDialogs


    /**
     * Must be executed inside transaction
     * Delete dialogs in cache that are not in the given list
     *
     * @param dialogs List of dialogs
     */
    private static void deleteDialogsNotInList(Realm realm, final List<QBChatDialog> dialogs) {
        if (dialogs != null && dialogs.size() > 0) {

            String[] ids = new String[dialogs.size()];
            for (int i = 0; i < dialogs.size(); i++) {
                ids[i] = dialogs.get(i).getDialogId();
            }

            RealmQuery<DialogCacheObject> query = realm.where(DialogCacheObject.class);
            query = query.beginGroup().not().in("dialogId", ids).endGroup();
            RealmResults<DialogCacheObject> all = query.findAll();
            Iterator<DialogCacheObject> iterator = all.iterator();
            while (iterator.hasNext()) {
                DialogCacheObject d = iterator.next();
                MessageCacheHelper.deleteDialogMessages(realm, d.getDialogId());
                d.deleteFromRealm();
            }
        }

    }

    /**
     * Get a dialog in cache by dialogId
     *
     * @param realm    Realm database instance
     * @param dialogId Quickblox Dialog id
     * @return Cache dialog.
     */
    public static DialogCacheObject getDialogById(Realm realm, String dialogId) {
        RealmQuery<DialogCacheObject> query = realm.where(DialogCacheObject.class);
        query.contains("dialogId", dialogId);

        return query.findFirst();
    }

    /**
     * Check if a dialog with the given id is present in cache
     *
     * @param realm    Realm database instance
     * @param dialogId Quickblox dialog id
     * @return true if dialog exists in cache. False otherwise.
     */
    public static boolean hasDialogWithId(Realm realm, String dialogId) {
        return getDialogById(realm, dialogId) != null;
    }

    /**
     * Get a dialog cache by Muapp user id
     *
     * @param realm      Realm database instance
     * @param externalId Muapp user id
     * @return A cache dialog where the given user is the opponent.
     */
    public static DialogCacheObject getDialogWithExternalUserId(Realm realm, long externalId) {
        RealmQuery<DialogCacheObject> query = realm.where(DialogCacheObject.class);
        query.equalTo("opponentExternalId", externalId);
        return query.findFirst();
    }

    /**
     * Update a dialog in cache with the information of the new message received
     *
     * @param realm         Realm database instance
     * @param dialogId      Quickblox dialog id
     * @param qbChatMessage New message received
     */
    public static void updateDialog(Realm realm, final String dialogId, final QBChatMessage qbChatMessage) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DialogCacheObject dialog = getDialogById(realm, dialogId);
                dialog.setLastMessage(qbChatMessage.getBody());
                dialog.setLastMessageDateSent(qbChatMessage.getDateSent());
                dialog.setLastMessageUserId(qbChatMessage.getSenderId());
            }
        });

    }

    /**
     * Set a dialog as seen by the user
     *
     * @param realm    Realm database instance
     * @param dialogId Quickblox dialog id
     */
    public static void setSeen(Realm realm, final String dialogId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DialogCacheObject dialog = getDialogById(realm, dialogId);
                dialog.setSeen(true);
            }
        });
    }

    public static void setLikedByMe(Realm realm, final String dialogId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DialogCacheObject dialog = getDialogById(realm, dialogId);
                dialog.setMyLike(true);
            }
        });
    }

    /**
     * Update dialog deleted timestamp
     *
     * @param realm    Realm database instance
     * @param dialogId Quickblox dialog id
     */
    public static void setDeleted(Realm realm, final String dialogId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DialogCacheObject dialog = getDialogById(realm, dialogId);
                dialog.setDeletedAt(new Date());
            }
        });
    }

    /**
     * Undo a poke readed operation. It is used when Quickblox read operation fails.
     *
     * @param realm    Realm database instance
     * @param dialogId Quickblox dialog id
     */
    public static void onUnreadPokeRead(Realm realm, final String dialogId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DialogCacheObject dialog = getDialogById(realm, dialogId);
                dialog.setUnreadMessageCount(dialog.getUnreadMessageCount() != null
                        ? dialog.getUnreadMessageCount() - 1 : 0);

            }
        });

    }

    /**
     * Set all dialog messages as readed.
     *
     * @param realm    Realm database instance
     * @param dialogId Quickblox dialog id
     */
    public static void readAllMessages(Realm realm, final String dialogId) {
        if (!realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    DialogCacheObject dialog = getDialogById(realm, dialogId);
                    dialog.setUnreadMessageCount(0);

                }
            });
        }
    }

    /**
     * Process a new Quickblox message received in the given dialog
     *
     * @param realm       Realm database instance
     * @param dialogId    Quickblox dialog id
     * @param chatMessage New received message
     * @param callback    Listener to notify events
     */
    public static void onNewMessageReceived(Realm realm, String dialogId, QBChatMessage chatMessage, QuickBloxMessagesListener callback) {
        if (chatMessage.getBody() != null && chatMessage.isMarkable()) { //for excluding status messages until will be released v.3.1
            if (hasDialogWithId(realm, dialogId)) {
                MessageCacheHelper.addMessage(realm, MessageCacheHelper.messageToCache(chatMessage));
                updateDialog(realm, dialogId, chatMessage);
                callback.onDialogUpdated(dialogId);
            } else {
                callback.onNewDialog();
            }
        }
    }//onNewMessageReceived

    /**
     * Process a new system Quickblox dialog received
     *
     * @param systemMessage Received message
     * @param callback      Listener to notify events
     */
    public static void onSystemMessageReceived(QBChatMessage systemMessage, QuickBloxMessagesListener callback) {
        if (isMessageCreatingDialog(systemMessage)) {
            callback.onNewDialog();
        }
    }

    private static boolean isMessageCreatingDialog(QBChatMessage systemMessage) {
        String notificationType = (String) systemMessage.getProperty(PROPERTY_NOTIFICATION_TYPE);
        return CREATING_DIALOG.equals(notificationType);
    }


    /**
     * Order by lastMessageDateSent then by createdAt
     */
    public static class LastMessageDateSentComparator implements Comparator<DialogCacheObject> {


        @Override
        public int compare(DialogCacheObject lhs, DialogCacheObject rhs) {

            long valueA = lhs.getLastMessageDateSent();
            if (valueA == 0) {
                valueA = (lhs.getUpdatedAt() != null) ? lhs.getUpdatedAt().getTime() / 1000 : lhs.getCreatedAt().getTime() / 1000;
            }
            long valueB = rhs.getLastMessageDateSent();
            if (valueB == 0) {
                valueB = (rhs.getUpdatedAt() != null) ? rhs.getUpdatedAt().getTime() / 1000 : rhs.getCreatedAt().getTime() / 1000;
            }

            if (valueB < valueA) {
                return -1;
            } else {
                return 1;
            }
        }

    }//LastMessageSentComparator
}
