package me.muapp.android.Classes.FirebaseMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import me.muapp.android.Classes.Util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import me.muapp.android.Classes.Chat.Conversation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ChatActivity;
import me.muapp.android.UI.Activity.MainActivity;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;
import static me.muapp.android.UI.Activity.ChatActivity.CONVERSATION_EXTRA;


/**
 * Created by rulo on 07/06/17.
 */

public class MuappMessagingService extends FirebaseMessagingService {
    private static final String CRUSH_KEY = "crush_dialog_key";
    private static final String MATCH_KEY = "dialog_key";
    private static final String MESSAGE_KEY = "message_dialog_key";
    private static final String TAG = "MuappMessagingService";
    User loggedUser;
    PreferenceHelper preferenceHelper;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        loggedUser = new UserHelper(this).getLoggedUser();
        preferenceHelper = new PreferenceHelper(this);
        // Check if message contains a data payload.
        Log.wtf(TAG, "MessageReceived");
        Log.wtf(TAG, "Data: " + remoteMessage.getData().toString());
        Log.wtf(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey(CRUSH_KEY)) {
                processCrush(remoteMessage);
            } else if (remoteMessage.getData().containsKey(MATCH_KEY)) {
                processMatch(remoteMessage);
            } else if (remoteMessage.getData().containsKey(MESSAGE_KEY)) {
                processChat(remoteMessage);
            }
        }

    }

    private void processCrush(RemoteMessage remoteMessage) {
        final String crushKey = remoteMessage.getData().get(CRUSH_KEY);
        DatabaseReference crushReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId())).child(crushKey);
        Log.wtf(TAG, "Crush " + crushReference.getRef().toString());
        crushReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Conversation c = dataSnapshot.getValue(Conversation.class);
                if (c != null) {
                    FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("users").child(String.valueOf(c.getOpponentId())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
                            if (conversationItem != null) {
                                conversationItem.setKey(crushKey);
                                conversationItem.setConversation(c);
                                Intent intent = new Intent(MuappMessagingService.this, ChatActivity.class);
                                intent.putExtra(CONVERSATION_EXTRA, conversationItem);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MuappMessagingService.this, 789, intent,
                                        PendingIntent.FLAG_ONE_SHOT);
                                sendNotification(String.format(getString(R.string.notif_crush), conversationItem.getName()), pendingIntent);
                            } else {
                                Log.wtf("ConversationItem", "isNull");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void processMatch(RemoteMessage remoteMessage) {
        final String matchKey = remoteMessage.getData().get(MATCH_KEY);
        DatabaseReference matchReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId())).child(matchKey);
        Log.wtf(TAG, "Match " + matchReference.getRef().toString());
        matchReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Conversation c = dataSnapshot.getValue(Conversation.class);
                if (c != null) {
                    FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("users").child(String.valueOf(c.getOpponentId())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
                            if (conversationItem != null) {
                                conversationItem.setKey(matchKey);
                                conversationItem.setConversation(c);
                                Intent intent = new Intent(MuappMessagingService.this, ChatActivity.class);
                                intent.putExtra(CONVERSATION_EXTRA, conversationItem);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MuappMessagingService.this, 987, intent,
                                        PendingIntent.FLAG_ONE_SHOT);
                                sendNotification(String.format(getString(R.string.format_match_names), conversationItem.getName()), pendingIntent);
                            } else {
                                Log.wtf(TAG, "isNull");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void processChat(RemoteMessage remoteMessage) {
        final String chatKey = remoteMessage.getData().get(MESSAGE_KEY);
        if (TextUtils.isEmpty(preferenceHelper.getCurrentActiveChat()) || !chatKey.equals(preferenceHelper.getCurrentActiveChat())) {
            DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                    .child("conversations")
                    .child(String.valueOf(loggedUser.getId())).child(chatKey);
            Log.wtf(TAG, "Chat " + chatReference.getRef().toString());
            chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Conversation c = dataSnapshot.getValue(Conversation.class);
                    if (c != null) {
                        FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("users").child(String.valueOf(c.getOpponentId())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
                                if (conversationItem != null) {
                                    conversationItem.setKey(chatKey);
                                    conversationItem.setConversation(c);
                                    Intent intent = new Intent(MuappMessagingService.this, ChatActivity.class);
                                    intent.putExtra(CONVERSATION_EXTRA, conversationItem);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MuappMessagingService.this, 897, intent,
                                            PendingIntent.FLAG_ONE_SHOT);
                                    sendNotification(String.format(getString(R.string.notif_sent_message), conversationItem.getName()), pendingIntent);
                                } else {
                                    Log.wtf(TAG, "ConversationItem isNull");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Log.wtf(TAG, "Conversation isNull");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendNotification(String messageBody, PendingIntent pendingIntent) {
        Log.wtf(TAG,messageBody);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent)
                ;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(100), notificationBuilder.build());
    }
}