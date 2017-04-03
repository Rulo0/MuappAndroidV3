package me.muapp.android.Classes.Quickblox.cache;

import java.util.Arrays;
import java.util.Collection;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;

/**
 * Created by Seba on 23/01/2017.
 * Message database cache object
 */

public class MessageCacheObject extends RealmObject{

    @PrimaryKey
    private String id;
    private String dialogId;
    private long dateSent = 0L;
    private String body;
    private boolean readByMe;
    private String readIds; //Collection<Integer>
    private String deliveredIds; //Collection<Integer>
    private Integer senderId;
    private String attachmentId;
    private String attachmentUrl;
    private String attachmentType;
    private int attachmentSize;
    private boolean sent = true;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isReadByMe() {
        return readByMe;
    }

    public void setReadByMe(boolean readByMe) {
        this.readByMe = readByMe;
    }

    public String getReadIds() {
        return readIds;
    }

    public void setReadIds(String readIds) {
        this.readIds = readIds;
    }

    public String getDeliveredIds() {
        return deliveredIds;
    }

    public void setDeliveredIds(String deliveredIds) {
        this.deliveredIds = deliveredIds;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public int getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(int attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        if( this.sent != sent) {
            this.sent = sent;
        }
    }

    @Override
    public boolean equals(Object obj){
        if( obj instanceof MessageCacheObject){
            return this.id.equalsIgnoreCase(((MessageCacheObject) obj).id);
        }
        return false;
    }

    public void addReader(Integer readerId){
        if( !collectionContainsItem(readIds, readerId)) {
            readIds += MessageCacheHelper.COLLECTION_SEPARATOR + readerId;
            if(QuickBloxChatHelper.getInstance().getCurrentUserId() != null &&
                    QuickBloxChatHelper.getInstance().getCurrentUserId() == readerId){
                readByMe = true;
            }
        }

    }

    public void deleteReader(Integer readerId){
        if( collectionContainsItem(readIds, readerId)) {
            Collection<Integer> collection = MessageCacheHelper.stringToCollection(readIds);
            collection.remove(readerId);
            readIds = MessageCacheHelper.collectionToString(collection);
            readByMe = collection.contains(QuickBloxChatHelper.getInstance().getCurrentUserId());
        }
    }

    public void addDelivered(Integer deliveredId){
        if( !collectionContainsItem(deliveredIds, deliveredId)) {
            deliveredIds += MessageCacheHelper.COLLECTION_SEPARATOR + deliveredId;
        }
    }

    private boolean collectionContainsItem(String collection, Integer element){
        if(collection == null || collection.length() == 0){
            return false;
        }else{
            String[] items = collection.split(MessageCacheHelper.COLLECTION_SEPARATOR);
            return Arrays.asList(items).contains(element);
        }
    }
}
