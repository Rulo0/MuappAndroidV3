package me.muapp.android.Classes.Quickblox.messages;


import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatMessage;

import java.util.HashMap;

import io.realm.Realm;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheHelper;

/**
 * Created by Seba on 25/11/2016.
 * Helper methods to manage Quickblox messages events
 */

public class QuickBloxMessagesHelper {

    private static QuickBloxMessagesHelper instance = null;

    /**
     * Gets singleton instance
     * @return
     */
    public static QuickBloxMessagesHelper getInstance(){
        if( instance == null){
            instance = new QuickBloxMessagesHelper();
        }
        return instance;
    }

    private QuickBloxMessagesHelper(){}

    private QBIncomingMessagesManager incomingMessagesManager;
    private QBSystemMessagesManager systemMessagesManager;
    private HashMap<QuickBloxMessagesListener, QBChatDialogMessageListener> incomingMessageListenerList = new HashMap<>();
    private HashMap<QuickBloxMessagesListener, QBSystemMessageListener> systemMessageListenerList = new HashMap<>();


    /**
     * Adds a listener to received messages events
     * @param realm
     * @param listener Listener to receive messages events.
     */
    public void registerQbChatListeners(Realm realm, QuickBloxMessagesListener listener) {
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        QBChatDialogMessageListener incomingMessageListener = getIncomingMessageListener(realm, listener);
        QBSystemMessageListener systemMessageListener = getSystemMessageListener(listener);
        incomingMessagesManager.addDialogMessageListener(incomingMessageListener);
        systemMessagesManager.addSystemMessageListener(systemMessageListener);
        incomingMessageListenerList.put(listener, incomingMessageListener);
        systemMessageListenerList.put(listener, systemMessageListener);
    }


    public void unregisterQbChatListeners(QuickBloxMessagesListener listener) {
        if (incomingMessagesManager != null) {
            QBChatDialogMessageListener l = incomingMessageListenerList.remove(listener);
            if( l != null) {
                incomingMessagesManager.removeDialogMessageListrener(l);
            }
        }
        if (systemMessagesManager != null) {
            QBSystemMessageListener l = systemMessageListenerList.remove(listener);
            if( l != null) {
                systemMessagesManager.removeSystemMessageListener(l);
            }
        }

    }

    private QBChatDialogMessageListener getIncomingMessageListener(final Realm realm, final QuickBloxMessagesListener listener){
        QBChatDialogMessageListener incomingMessageListener = new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogId, QBChatMessage chatMessage, Integer integer) {
                DialogCacheHelper.onNewMessageReceived(realm, dialogId, chatMessage, listener);
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
            }
        };
        return incomingMessageListener;
    }

    private QBSystemMessageListener getSystemMessageListener(final QuickBloxMessagesListener listener){
        QBSystemMessageListener systemMessagesListener = new QBSystemMessageListener(){
            @Override
            public void processMessage(final QBChatMessage qbChatMessage) {
                DialogCacheHelper.onSystemMessageReceived(qbChatMessage, listener);
            }

            @Override
            public void processError(QBChatException e, QBChatMessage qbChatMessage) {
            }
        };
        return systemMessagesListener;
    }

}
