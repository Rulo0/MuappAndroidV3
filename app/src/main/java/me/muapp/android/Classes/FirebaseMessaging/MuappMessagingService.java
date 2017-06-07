package me.muapp.android.Classes.FirebaseMessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

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

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.wtf(TAG, "From: " + remoteMessage.getFrom());
        loggedUser = new UserHelper(this).getLoggedUser();
        preferenceHelper = new PreferenceHelper(this);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey(CRUSH_KEY)) {
                processCrush(remoteMessage);
            } else if (remoteMessage.getData().containsKey(MATCH_KEY)) {
                processMatch(remoteMessage);
            } else if (remoteMessage.getData().containsKey(MESSAGE_KEY)) {
                processChat(remoteMessage);
            }
            Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());
            Log.wtf(TAG, remoteMessage.getNotification().getBodyLocalizationKey());
        }

    }

    private void processCrush(RemoteMessage remoteMessage) {
        final String crushKey = remoteMessage.getData().get(CRUSH_KEY);
        DatabaseReference crushReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId())).child(crushKey);
        Log.wtf("MuappMessagingService", "Crush " + crushReference.getRef().toString());
        crushReference.addValueEventListener(new ValueEventListener() {
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
        Log.wtf("MuappMessagingService", "Match " + matchReference.getRef().toString());
        matchReference.addValueEventListener(new ValueEventListener() {
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
                                PendingIntent pendingIntent = PendingIntent.getActivity(MuappMessagingService.this, 789, intent,
                                        PendingIntent.FLAG_ONE_SHOT);
                                sendNotification(String.format(getString(R.string.format_match_names), conversationItem.getName()), pendingIntent);
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

    private void processChat(RemoteMessage remoteMessage) {
        final String chatKey = remoteMessage.getData().get(MESSAGE_KEY);
        if (TextUtils.isEmpty(preferenceHelper.getCurrentActiveChat()) || !chatKey.equals(preferenceHelper.getCurrentActiveChat())) {
            DatabaseReference matchReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                    .child("conversations")
                    .child(String.valueOf(loggedUser.getId())).child(chatKey);
            Log.wtf("MuappMessagingService", "Chat " + matchReference.getRef().toString());
            matchReference.addValueEventListener(new ValueEventListener() {
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
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MuappMessagingService.this, 789, intent,
                                            PendingIntent.FLAG_ONE_SHOT);
                                    sendNotification(String.format(getString(R.string.notif_sent_message), conversationItem.getName()), pendingIntent);
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
    }

    private void sendNotification(String messageBody, PendingIntent pendingIntent) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(100), notificationBuilder.build());
    }
}