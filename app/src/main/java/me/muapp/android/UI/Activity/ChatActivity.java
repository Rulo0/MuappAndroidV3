package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatDialogListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatDialogUpdateListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatLoginListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickbloxPresenceListener;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheHelper;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.Classes.Quickblox.cache.MessageCacheHelper;
import me.muapp.android.Classes.Quickblox.cache.MessageCacheObject;
import me.muapp.android.Classes.Quickblox.dialog.QuickBloxDialogHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.PokeAdapter;

public class ChatActivity extends BaseActivity implements QuickbloxPresenceListener, QuickBloxChatLoginListener, QuickBloxChatDialogListener, PokeAdapter.PokeAdapterListener {
    public static final String DIALOG_EXTRA = "DIALOG_EXTRA";
    Toolbar toolbar;
    TextView toolbar_opponent_fullname;
    DialogCacheObject thisDialog;
    int opponentId;
    private QBChatDialog qbChatDialog;
    private CountDownLatch connectionLatch;
    private ChatMessageListener chatMessageListener;
    private ChatMessageSentListener chatMessageSentListener;
    private ChatMessageStatusListener chatMessageStatusListener;
    Realm realm;
    PokeAdapter pokeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        toolbar_opponent_fullname = (TextView) findViewById(R.id.toolbar_opponent_fullname);
        if (getIntent().hasExtra(DIALOG_EXTRA)) {
            realm = CacheUtils.getInstance(loggedUser);
            chatMessageListener = new ChatMessageListener();
            chatMessageSentListener = new ChatMessageSentListener();
            chatMessageStatusListener = new ChatMessageStatusListener();
            setupLayout((DialogCacheObject) getIntent().getParcelableExtra(DIALOG_EXTRA));
        } else
            finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        QuickBloxChatHelper.getInstance().addLoginListener(this);
    }

    public void setupLayout(final DialogCacheObject dialog) {
        thisDialog = dialog;
        pokeAdapter = new PokeAdapter((MuappApplication) getApplication(), this, this);
        setOpponentId();
        ImageButton btn_back = (ImageButton) findViewById(R.id.toolbar_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar_opponent_fullname.setText(dialog.getOpponentName());
        ImageView toolbar_opponent_photo = (ImageView) findViewById(R.id.toolbar_opponent_photo);
        Glide.with(this).load(dialog.getOpponentPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption_white).bitmapTransform(new CropCircleTransformation(this)).into(toolbar_opponent_photo);
        toolbar_opponent_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, dialog.getOpponentName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOpponentId() {
        if (thisDialog != null) {
            Integer currentUserId = QuickBloxChatHelper.getInstance().getCurrentUserId();
            Integer op1 = thisDialog.getOccupantId1();
            if (!op1.equals(currentUserId)) {
                opponentId = op1;
            } else {
                opponentId = thisDialog.getOccupantId2();
            }
        }
    }


    @Override
    public void presenceChanged(QBPresence qbPresence) {
        if (qbPresence != null && qbPresence.getUserId() == opponentId) {
            if (qbPresence.getType() != QBPresence.Type.online) {
                toolbar_opponent_fullname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chat_user_offline, 0, 0, 0);
            } else {
                toolbar_opponent_fullname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chat_user_online, 0, 0, 0);
            }
        }
    }

    @Override
    public void onChatSessionCreated(boolean success) {
        Log.wtf(TAG, "onChatSessionCreated " + success);
        if (success) {
            QuickBloxChatHelper.getInstance().getDialogById(thisDialog.getDialogId(), this);
        } else {
            if (connectionLatch != null) {
                connectionLatch.countDown();
            }
        }
    }

    @Override
    public void onDialogLoaded(QBChatDialog dialog) {
        if (dialog != null) {
            qbChatDialog = dialog;
            qbChatDialog.addMessageListener(chatMessageListener);
            qbChatDialog.addMessageSentListener(chatMessageSentListener);
            QBChatService.getInstance().getMessageStatusesManager().addMessageStatusListener(chatMessageStatusListener);
            //enableControls(true);
            loadChatMessagesFromQuickblox(false);
            setSeenDialog();
            if (connectionLatch != null) {
                connectionLatch.countDown();
            }
        } else {
            if (connectionLatch != null) {
                connectionLatch.countDown();
            }
        }
    }

    private void loadChatMessagesFromQuickblox(boolean pagination) {
        long lastSent = Long.MAX_VALUE;
        if (pagination) {
            lastSent = pokeAdapter.getLastPokeSentTime();
        }
        QuickBloxDialogHelper.getInstance().loadChatHistory(qbChatDialog, lastSent, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                if ((messages == null || messages.size() == 0) && pokeAdapter.getItemCount() == 0) {
                    if (User.Gender.getGender(loggedUser.getGender()) == User.Gender.Female) {
                        //   chatEmpty.setVisibility(View.VISIBLE);
                    } else {
                        //   chatEmpty.setVisibility(GONE);
                    }
                } else {
                    //    chatEmpty.setVisibility(GONE);
                    QuickBloxDialogHelper.getInstance().readMessages(qbChatDialog, messages);
                    DialogCacheHelper.readAllMessages(realm, thisDialog.getDialogId());
                    ArrayList<MessageCacheObject> cache = new ArrayList<MessageCacheObject>();
                    for (QBChatMessage m : messages) {
                        cache.add(MessageCacheHelper.messageToCache(m));
                        Log.wtf("loadChat", m.getBody());
                    }
                    MessageCacheHelper.setMesssages(realm, cache);
                }
                //hideRefreshing();
            }

            @Override
            public void onError(QBResponseException e) {
                // hideRefreshing();
                //  showErrorMessage(getString(R.string.connection_error));
            }
        });
    }


    protected void setSeenDialog() {
        if (!realm.isClosed()) {
            if (!thisDialog.isSeen()) {
                QuickBloxChatHelper.getInstance().setSeen(qbChatDialog, loggedUser.getId(),
                        new QuickBloxChatDialogUpdateListener() {
                            @Override
                            public void onDialogUpdated(QBChatDialog dialog) {
                                if (dialog != null) {
                                    setSeenDialogCache();
                                }
                            }
                        });
            }
        }
    }//setSeenDialog

    protected void setSeenDialogCache() {
        DialogCacheHelper.setSeen(realm, thisDialog.getDialogId());
    }

    @Override
    public void onPokeReaded(MessageCacheObject poke) {

    }

    public class ChatMessageListener implements QBChatDialogMessageListener {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            QuickBloxDialogHelper.getInstance().readMessage(qbChatDialog, qbChatMessage);
            //  chatEmpty.setVisibility(GONE);
            // showMessage(qbChatMessage, false);
        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

        }
    }

    public class ChatMessageSentListener implements QBChatDialogMessageSentListener {

        @Override
        public void processMessageSent(String s, QBChatMessage qbChatMessage) {
            MessageCacheHelper.setMessageAsSent(realm, qbChatMessage.getId(), qbChatMessage.getDialogId());
        }

        @Override
        public void processMessageFailed(String s, QBChatMessage qbChatMessage) {
        }
    }

    public class ChatMessageStatusListener implements QBMessageStatusListener {
        @Override
        public void processMessageDelivered(String messageId, String dialogId, Integer userId) {
            if (dialogId.equalsIgnoreCase(String.valueOf(qbChatDialog.getDialogId()))) {
                MessageCacheHelper.setMessageDeliveredTo(realm, messageId, dialogId, userId);
            }
        }

        @Override
        public void processMessageRead(String messageId, String dialogId, Integer userId) {
            if (dialogId.equalsIgnoreCase(String.valueOf(qbChatDialog.getDialogId()))) {
                MessageCacheHelper.setMessageReadedBy(realm, messageId, dialogId, userId);
            }
        }
    }//ChatMessageStatusListener

}


