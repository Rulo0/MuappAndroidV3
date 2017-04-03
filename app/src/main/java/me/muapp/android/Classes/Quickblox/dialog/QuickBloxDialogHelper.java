package me.muapp.android.Classes.Quickblox.dialog;

import android.support.annotation.NonNull;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.QuickbloxHelper;

public class QuickBloxDialogHelper {

    private static final String CHAT_HISTORY_ITEMS_SORT_FIELD = "date_sent";

    private static QuickBloxDialogHelper instance;

    private QuickBloxDialogHelper() {
    }

    /**
     * Gets singleton instance
     * @return
     */
    public static synchronized QuickBloxDialogHelper getInstance() {
        if (instance == null) {
            instance = new QuickBloxDialogHelper();
        }
        return instance;
    }

    /**
     * Get messages from a given dialog ordered by date
     * @param dialog Dialog to get messages from.
     * @param fromDate Get messages previous to this date.
     * @param callback Listener to be notified when messages are loaded
     */
    public void loadChatHistory(QBChatDialog dialog, long fromDate,
                                final QBEntityCallback<ArrayList<QBChatMessage>> callback) {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder()
                .lt("date_sent", fromDate)
                .sortDesc(CHAT_HISTORY_ITEMS_SORT_FIELD);
        QBRestChatService.getDialogMessages(dialog, customObjectRequestBuilder).performAsync(callback);

    }

    /**
     * Get all messages from a given dialog.
     * @param dialog Dialog to get messages from.
     * return List of all messages of the given dialog
     * @throws QBResponseException
     */
    public ArrayList<QBChatMessage> loadAllChatHistorySync(QBChatDialog dialog, @NonNull ArrayList<QBChatMessage> currentResults) throws QBResponseException {
        long firstDateSent = Long.MAX_VALUE;
        if (currentResults != null && currentResults.size() > 0) {
            firstDateSent = currentResults.get(currentResults.size() - 1).getDateSent();
        }

        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder()
                .lt("date_sent", firstDateSent)
                .sortDesc(CHAT_HISTORY_ITEMS_SORT_FIELD);

        ArrayList<QBChatMessage> results = QBRestChatService.getDialogMessages(dialog, customObjectRequestBuilder).perform();
        if (results.size() == 0) {
            return currentResults;
        } else {
            currentResults.addAll(results);
            return loadAllChatHistorySync(dialog, currentResults);
        }

    }

    /**
     * Send a text message to Quickblox
     * @param qbChatDialog Current dialog
     * @param message Message to send
     * @return Message sent
     * @throws SmackException.NotConnectedException
     * @throws XMPPException
     */
    public QBChatMessage sendTextMessage(QBChatDialog qbChatDialog, String message) throws SmackException.NotConnectedException, XMPPException {

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(message);
        chatMessage.setDialogId(qbChatDialog.getDialogId());
        chatMessage.setSaveToHistory(true);
        chatMessage.setMarkable(true);
        chatMessage.setDateSent(new Date().getTime() / 1000);
        chatMessage.setSenderId(QuickBloxChatHelper.getInstance().getCurrentUserId());

        qbChatDialog.sendMessage(chatMessage);
        return chatMessage;
    }

    /**
     * Send sticker message to Quickblox
     * @param qbChatDialog Current dialog
     * @param stickerUrl Sticker url to send
     * @return Message sent
     * @throws SmackException.NotConnectedException
     * @throws XMPPException
     */
    public QBChatMessage sendStickerMessage(QBChatDialog qbChatDialog, String stickerUrl) throws SmackException.NotConnectedException, XMPPException {

        QBAttachment attachment = new QBAttachment(QuickbloxHelper.POKE_STICKER);
        attachment.setUrl(stickerUrl);

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(QuickbloxHelper.POKE_STICKER);
        chatMessage.setDialogId(qbChatDialog.getDialogId());
        chatMessage.addAttachment(attachment);
        chatMessage.setSaveToHistory(true);
        chatMessage.setMarkable(true);
        chatMessage.setDateSent(new Date().getTime() / 1000);
        chatMessage.setSenderId(QuickBloxChatHelper.getInstance().getCurrentUserId());

        qbChatDialog.sendMessage(chatMessage);
        return chatMessage;
    }

    /**
     * Mark the given message as read
     * @param qbChatDialog Current dialog
     * @param chatMessage Message to update
     * @return
     */
    public boolean readMessage(QBChatDialog qbChatDialog, final QBChatMessage chatMessage) {
        if (!isReaded(chatMessage)) {
            try {
                qbChatDialog.readMessage(chatMessage);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Mark a list of messages as read
     * @param qbChatDialog Current dialog
     * @param chatMessages Messages to updated
     */
    public void readMessages(QBChatDialog qbChatDialog, final ArrayList<QBChatMessage> chatMessages) {
        for (QBChatMessage msg : chatMessages) {
            readMessage(qbChatDialog, msg);
        }

    }

    /**
     * Check if a message was read by current user
     * @param message Message to check
     * @return True if the messages was read by the current user. False otherwise.
     */
    public boolean isReaded(QBChatMessage message) {
        return QuickBloxChatHelper.getInstance().getCurrentUser() != null &&
                message.getReadIds() != null &&
                message.getReadIds().contains(QuickBloxChatHelper.getInstance().getCurrentUserId());

    }

    /**
     * Create a image message to be sent to Quickblox
     * @param dialogId Current dialog
     * @param path Local path of image to send
     * @return Quickblox message object.
     */
    public QBChatMessage generateImageMessage(String dialogId, String path) {
        //generate message
        QBAttachment attachment = new QBAttachment(QuickbloxHelper.POKE_IMAGE);
        attachment.setUrl("file://" + path);
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(QuickbloxHelper.POKE_IMAGE);
        chatMessage.setDialogId(dialogId);
        chatMessage.addAttachment(attachment);
        chatMessage.setSaveToHistory(true);
        chatMessage.setMarkable(true);
        chatMessage.setDateSent(new Date().getTime() / 1000);
        chatMessage.setSenderId(QuickBloxChatHelper.getInstance().getCurrentUserId());
        return chatMessage;
    }

    /**
     * Send the given message to Quickblox
     * @param qbChatDialog Current dialog
     * @param message Message to send
     * @param qbFile Uploaded image file to send
     * @return Message sent
     * @throws SmackException.NotConnectedException
     * @throws XMPPException
     */
    public QBChatMessage sendImageMessage(QBChatDialog qbChatDialog, QBChatMessage message, QBFile qbFile) throws SmackException.NotConnectedException, XMPPException {
        QBAttachment att = message.getAttachments().iterator().next();
        att.setId(qbFile.getUid().toString());
        att.setUrl(null);
        qbChatDialog.sendMessage(message);
        return message;
    }

    /**
     * Create a voice message to be sent to Quickblox
     * @param dialogId Current dialog
     * @param path Local path of voice note to send
     * @return Quickblox message object.
     */
    public QBChatMessage generateVoiceMessage(String dialogId, String path, int duration) {
        QBAttachment attachment = new QBAttachment(QuickbloxHelper.POKE_VOICE);
        attachment.setSize(duration);
        attachment.setUrl(path);

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(QuickbloxHelper.POKE_VOICE);
        chatMessage.setDialogId(dialogId);
        chatMessage.addAttachment(attachment);
        chatMessage.setProperty(QuickbloxHelper.POKE_VOICE_SIZE, String.valueOf(duration));
        chatMessage.setSaveToHistory(true);
        chatMessage.setMarkable(true);
        chatMessage.setDateSent(new Date().getTime() / 1000);
        chatMessage.setSenderId(QuickBloxChatHelper.getInstance().getCurrentUserId());
        return chatMessage;
    }

    /**
     * Send the given message to Quickblox
     * @param qbChatDialog Current dialog
     * @param message Message to send
     * @param qbFile Uploaded voice note file to send
     * @return Message sent
     * @throws SmackException.NotConnectedException
     * @throws XMPPException
     */
    public QBChatMessage sendVoiceMessage(QBChatDialog qbChatDialog, QBChatMessage message, QBFile qbFile) throws SmackException.NotConnectedException, XMPPException {
        QBAttachment att = message.getAttachments().iterator().next();
        att.setId(qbFile.getUid().toString());
        att.setUrl(null);
        qbChatDialog.sendMessage(message);
        return message;
    }

    /**
     * Uploads an attachment file to Quickblox
     * @param path Local file path
     * @param listener Listener to be notified when attachment is uploaded.
     */
    public void uploadAttachment(String path, QBEntityCallback<QBFile> listener) {
        File attachment = new File(path);
        QBContent.uploadFileTask(attachment, true, null, null)
                .performAsync(listener);
    }

}
