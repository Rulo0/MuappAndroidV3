package me.muapp.android.Classes.Quickblox.cache;

import android.support.annotation.NonNull;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.QuickbloxHelper;

/**
 * Created by Seba on 24/01/2017.
 * Helper methods to manage Realm MessageCacheObject table
 */
public class MessageCacheHelper {

    public static final String COLLECTION_SEPARATOR = ",";

    /**
     * Converts QBChatMessage to cache object
     * @param qbChatMessage Message to convert
     * @return Cache database object
     */
    public static MessageCacheObject messageToCache(QBChatMessage qbChatMessage) {
        MessageCacheObject m = new MessageCacheObject();
        m.setId(qbChatMessage.getId());
        m.setDialogId(qbChatMessage.getDialogId());
        m.setDateSent(qbChatMessage.getDateSent());
        m.setBody(qbChatMessage.getBody());
        m.setReadByMe(qbChatMessage.getReadIds() != null && qbChatMessage.getReadIds().contains(QuickBloxChatHelper.getInstance().getCurrentUserId()));
        m.setReadIds(collectionToString(qbChatMessage.getReadIds()));
        m.setDeliveredIds(collectionToString(qbChatMessage.getDeliveredIds()));
        m.setSenderId(qbChatMessage.getSenderId());
        if (qbChatMessage.getAttachments() != null &&
                qbChatMessage.getAttachments().size() > 0) {
            QBAttachment attachment = qbChatMessage.getAttachments().iterator().next();
            m.setAttachmentId(attachment.getId());
            m.setAttachmentUrl(attachment.getUrl());
            m.setAttachmentType(attachment.getType());
        } else {
            m.setAttachmentId(null);
            m.setAttachmentUrl(null);
            m.setAttachmentType(null);
        }

        if( qbChatMessage.getProperties() != null) {
            String size = qbChatMessage.getProperties().get(QuickbloxHelper.POKE_VOICE_SIZE);
            if (size != null) {
                try {
                    m.setAttachmentSize(Integer.parseInt(size));
                } catch (Exception e) {
                }
            }
        }
        return m;
    }//messageToCache

    /**
     * Converts a cache message object to Quickblox format
     * @param message
     * @return
     */
    public static QBChatMessage cacheToMessage(MessageCacheObject message) {

        QBChatMessage m = new QBChatMessage();
        m.setId(message.getId());
        m.setDialogId(message.getDialogId());
        m.setDateSent(message.getDateSent());
        m.setBody(message.getBody());
        m.setReadIds(stringToCollection(message.getReadIds()));
        m.setDeliveredIds(stringToCollection(message.getDeliveredIds()));
        m.setSenderId(message.getSenderId());
        if( message.getAttachmentType() != null){
            QBAttachment attachment = new QBAttachment(message.getAttachmentType());
            attachment.setId(message.getAttachmentId());
            attachment.setUrl(message.getAttachmentUrl());
            m.addAttachment(attachment);
        }
        m.setProperty(QuickbloxHelper.POKE_VOICE_SIZE, String.valueOf(message.getAttachmentSize()));
        return m;

    }

    static String collectionToString(Collection<Integer> collection) {
        String res = "";
        if (collection != null && collection.size() > 0) {
            Iterator<Integer> iterator = collection.iterator();
            while (iterator.hasNext()) {
                res += iterator.next().toString() + COLLECTION_SEPARATOR;
            }
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    static Collection<Integer> stringToCollection(String data) {
        Collection<Integer> collection = new ArrayList<>();
        if (data != null && data.length() > 0) {
            String[] split = data.split(COLLECTION_SEPARATOR);
            for (int i = 0; i < split.length; i++) {
                try {
                    collection.add(Integer.parseInt(split[i]));
                } catch (Exception e) {
                }
            }
        }
        return collection;
    }

    /**
     * Get all messages from a dialog
     * @param realm
     * @param dialogId Quickblox dialog id
     * @return List of messages of the given dialog ordered by date
     */
    public static RealmResults<MessageCacheObject> getMessagesOfDialog(Realm realm, @NonNull String dialogId) {
        RealmQuery<MessageCacheObject> query = realm.where(MessageCacheObject.class);
        query.equalTo("dialogId", dialogId);
        RealmResults<MessageCacheObject> results = query.findAllSortedAsync("dateSent", Sort.ASCENDING);
        return results;
    }

    /**
     * Add or update messages to cache
     * @param realm
     * @param messages Messages to add
     */
    public static void setMesssages(Realm realm, final List<MessageCacheObject> messages) {
        if( !realm.isClosed()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(messages);
                }
            });
        }
    }//setDialogs

    /**
     * Get a single message by its id
     * @param realm
     * @param messageId Quickblox Message id of message to obtain
     * @param dialogId Quickblox dialog id of parent dialog
     * @return
     */
    public static MessageCacheObject getMessageById(Realm realm, String messageId, String dialogId) {
        RealmQuery<MessageCacheObject> query = realm.where(MessageCacheObject.class);
        query.equalTo("id", messageId);
        query.equalTo("dialogId", dialogId);

        return query.findFirst();
    }

    /**
     * Add a single message to cache
     * @param realm
     * @param message Message to add
     */
    public static void addMessage(Realm realm, final MessageCacheObject message) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(message);
            }
        });
    }

    /**
     * Must be executed inside transaction
     * Delete all messages of a given dialog
     * @param dialogId Quickblox dialog ID
     */
    public static void deleteDialogMessages(Realm realm, String dialogId){

        RealmQuery<MessageCacheObject> query = realm.where(MessageCacheObject.class);
        query.equalTo("dialogId", dialogId);
        query.findAll().deleteAllFromRealm();

    }

    /**
     * Must be executed inside transaction
     * Delete oldest messages from a dialog. It keeps 100 most recent messages on cache.
     * It increases chat performance.
     * @param dialogId Quickblox dialog ID
     */
    public static void deleteOldDialogMessages(Realm realm, String dialogId){

        RealmResults<MessageCacheObject> allSorted = realm.where(MessageCacheObject.class)
                .equalTo("dialogId", dialogId)
                .findAllSorted("dateSent", Sort.DESCENDING);
        for( int i = allSorted.size()-1; i>=105; i--){
            allSorted.deleteFromRealm(i);
        }

    }

    /**
     * Delete a false image message. False image message is added while message is being send to Quickblox.
     * It must be deleted after is sended to Quickblox.
     * @param realm
     * @param imagePath Image path of the message to be deleted
     */
    public static void deleteMockImageMessage(Realm realm, final String imagePath){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<MessageCacheObject> query = realm.where(MessageCacheObject.class);
                query.equalTo("attachmentType", QuickbloxHelper.POKE_IMAGE)
                    .equalTo("attachmentUrl", imagePath)
                    .findAll().deleteAllFromRealm();
            }
        });
    }

    public static boolean isMessageReadedByMe(MessageCacheObject obj){
        Collection<Integer> ids = stringToCollection(obj.getReadIds());
        Integer userId = QuickBloxChatHelper.getInstance().getCurrentUserId();
        Iterator<Integer> iterator = ids.iterator();
        while(iterator.hasNext()){
            if( userId.equals(iterator.next())){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the other user has read the given message
     * @param obj Message to check
     * @return True if messages was readed by the opponent. False otherwise.
     */
    public static boolean isMessageReadedByOpponent(MessageCacheObject obj){
        Collection<Integer> ids = stringToCollection(obj.getReadIds());
        Integer userId = QuickBloxChatHelper.getInstance().getCurrentUserId();
        Iterator<Integer> iterator = ids.iterator();
        while(iterator.hasNext()){
            if( !userId.equals(iterator.next())){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given message was delivered to the opponent.
     * @param obj Message to check
     * @return True if messages was delivered. False otherwise.
     */
    public static boolean isMessageDelivered(MessageCacheObject obj){
        Collection<Integer> ids = stringToCollection(obj.getDeliveredIds());
        Integer userId = QuickBloxChatHelper.getInstance().getCurrentUserId();
        Iterator<Integer> iterator = ids.iterator();
        while(iterator.hasNext()){
            if( !userId.equals(iterator.next())){
                return true;
            }
        }
        return false;
    }

    /**
     * Set the given message as sent to Quickblox.
     * @param realm
     * @param messageId Quickblox message id to updated
     * @param dialogId Quickblox dialog parent of the message
     */
    public static void setMessageAsSent(Realm realm, final String messageId, final String dialogId){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MessageCacheObject message = getMessageById(realm, messageId, dialogId);
                if( message != null) {
                    message.setSent(true);
                }
            }
        });
    }//setMessageAsSent

    /**
     * Set the given message as delivered to the given user
     * @param realm
     * @param messageId Quickblox Message ID
     * @param dialogId Quickblox dialog message parent
     * @param userId Delivered Quickblox user id
     */
    public static void setMessageDeliveredTo(Realm realm, final String messageId, final String dialogId, final Integer userId){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MessageCacheObject message = getMessageById(realm, messageId, dialogId);
                if( message != null) {
                    message.addDelivered(userId);
                }
            }
        });
    }

    /**
     * Set the given message as readed by given user
     * @param realm
     * @param messageId Quickblox Message ID
     * @param dialogId Quickblox dialog message parent
     * @param userId Quickblox user id who has read the message
     */
    public static void setMessageReadedBy(Realm realm, final String messageId, final String dialogId, final Integer userId){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MessageCacheObject message = getMessageById(realm, messageId, dialogId);
                if( message != null) {
                    message.addReader(userId);
                }
            }
        });
    }//setMessageAsSent
    

}
