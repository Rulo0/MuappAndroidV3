package me.muapp.android.Classes.Quickblox.cache;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Seba on 19/01/2017.
 * Dialog database cache object
 */
public class DialogCacheObject extends RealmObject {

    @PrimaryKey
    private String dialogId;
    private String lastMessage;
    private long lastMessageDateSent;
    private Integer lastMessageUserId;
    private Integer unreadMessageCount;
    private Integer occupantId1;
    private Integer occupantId2;
    private String name;
    protected Date createdAt;
    protected Date updatedAt;
    protected String opponentName;
    protected String opponentPhoto;
    protected long opponentExternalId;
    protected boolean seen;
    protected Date deletedAt;
    private Boolean isCrush;
    protected boolean like1;
    protected boolean like2;
    protected boolean myLike;
    protected boolean opponnentLike;

    @Override
    public String toString() {
        return "DialogCacheObject{" +
                "dialogId='" + dialogId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", lastMessageDateSent=" + lastMessageDateSent +
                ", lastMessageUserId=" + lastMessageUserId +
                ", unreadMessageCount=" + unreadMessageCount +
                ", occupantId1=" + occupantId1 +
                ", occupantId2=" + occupantId2 +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", opponentName='" + opponentName + '\'' +
                ", opponentPhoto='" + opponentPhoto + '\'' +
                ", opponentExternalId=" + opponentExternalId +
                ", seen=" + seen +
                ", deletedAt=" + deletedAt +
                ", isCrush=" + isCrush +
                ", like1=" + like1 +
                ", like2=" + like2 +
                ", myLike=" + myLike +
                ", opponnentLike=" + opponnentLike +
                '}';
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageDateSent() {
        return lastMessageDateSent;
    }

    public void setLastMessageDateSent(long lastMessageDateSent) {
        this.lastMessageDateSent = lastMessageDateSent;
    }

    public Integer getLastMessageUserId() {
        return lastMessageUserId;
    }

    public void setLastMessageUserId(Integer lastMessageUserId) {
        this.lastMessageUserId = lastMessageUserId;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public Integer getOccupantId1() {
        return occupantId1;
    }

    public void setOccupantId1(Integer occupantId1) {
        this.occupantId1 = occupantId1;
    }

    public Integer getOccupantId2() {
        return occupantId2;
    }

    public void setOccupantId2(Integer occupantId2) {
        this.occupantId2 = occupantId2;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getOpponentPhoto() {
        return opponentPhoto;
    }

    public void setOpponentPhoto(String opponentPhoto) {
        this.opponentPhoto = opponentPhoto;
    }

    public long getOpponentExternalId() {
        return opponentExternalId;
    }

    public void setOpponentExternalId(long opponentExternalId) {
        this.opponentExternalId = opponentExternalId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getCrush() {
        return isCrush;
    }

    public void setCrush(Boolean crush) {
        isCrush = crush;
    }

    public boolean isLike1() {
        return like1;
    }

    public void setLike1(boolean like1) {
        this.like1 = like1;
    }

    public boolean isLike2() {
        return like2;
    }

    public void setLike2(boolean like2) {
        this.like2 = like2;
    }

    public boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(boolean myLike) {
        this.myLike = myLike;
    }

    public boolean isOpponnentLike() {
        return opponnentLike;
    }

    public void setOpponnentLike(boolean opponnentLike) {
        this.opponnentLike = opponnentLike;
    }
}
