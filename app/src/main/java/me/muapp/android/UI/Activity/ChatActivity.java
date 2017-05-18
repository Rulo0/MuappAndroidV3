package me.muapp.android.UI.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.MessagesAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class ChatActivity extends BaseActivity implements ChildEventListener {
    public static final String CONVERSATION_EXTRA = "CONVERSATION_EXTRA";
    ConversationItem conversationItem;
    Toolbar toolbar;
    TextView toolbar_opponent_fullname;
    RecyclerView recycler_conversation;
    DatabaseReference conversationReference;
    MessagesAdapter messagesAdapter;
    EditText etMessage;
    ImageButton chatSendButton;
    DatabaseReference myConversation, yourConversation, yourPresence;
    ValueEventListener presenceListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            boolean imOnline = dataSnapshot.getValue(Boolean.class);
            Drawable img = ContextCompat.getDrawable(ChatActivity.this, imOnline ? R.drawable.ic_chat_user_online : R.drawable.ic_chat_user_offline);
            toolbar_opponent_fullname.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        conversationItem = getIntent().getParcelableExtra(CONVERSATION_EXTRA);
        if (conversationItem == null)
            finish();
        Log.wtf("convesration", conversationItem.toString());
        yourPresence = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(conversationItem.getConversation().getOpponentId())).child("online");
        messagesAdapter = new MessagesAdapter(this);
        messagesAdapter.setLoggedUserId(loggedUser.getId());
        conversationReference = /*FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getKey())
                .child("conversation");*/
                FirebaseDatabase.getInstance().getReference().child("JW")
                        .child(String.valueOf(loggedUser.getId()))
                        .child(conversationItem.getKey())
                        .child("conversation");


        myConversation = /*FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getKey());
*/
                FirebaseDatabase.getInstance().getReference().child("JW")
                        .child(String.valueOf(loggedUser.getId()))
                        .child(conversationItem.getKey());

        Log.wtf("convesration", "mine " + myConversation.getRef().toString());

        yourConversation = /*FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getConversation().getOpponentConversationId());*/
                FirebaseDatabase.getInstance().getReference().child("JW")
                        .child(String.valueOf(conversationItem.getConversation().getOpponentId()))
                        .child(conversationItem.getConversation().getOpponentConversationId());

        Log.wtf("convesration", "yours " + yourConversation.getRef().toString());
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        etMessage = (EditText) findViewById(R.id.etMessage);
        chatSendButton = (ImageButton) findViewById(R.id.chatSendButton);
        toolbar_opponent_fullname = (TextView) findViewById(R.id.toolbar_opponent_fullname);
        toolbar_opponent_fullname.setText(conversationItem.getFullName());
        toolbar.findViewById(R.id.toolbar_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recycler_conversation = (RecyclerView) findViewById(R.id.recycler_conversation);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycler_conversation.setLayoutManager(llm);
        recycler_conversation.setAdapter(messagesAdapter);
        Glide.with(this).load(conversationItem.getProfilePicture()).placeholder(R.drawable.ic_logo_muapp_no_caption).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) toolbar.findViewById(R.id.toolbar_opponent_photo));
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSend();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        conversationReference.addChildEventListener(this);
        yourPresence.addValueEventListener(presenceListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversationReference.removeEventListener(this);
        yourPresence.removeEventListener(presenceListener);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message m = dataSnapshot.getValue(Message.class);
        if (m != null)
            m.setKey(dataSnapshot.getKey());
        Log.wtf("chat", m.toString());
        messagesAdapter.addMessage(m);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void attempSend() {
        if (!TextUtils.isEmpty(etMessage.getText().toString())) {
            Map<String, String> map = ServerValue.TIMESTAMP;

            Message m = new Message();
            m.setTimeStamp(new Date().getTime());
            m.setSenderId(loggedUser.getId());
            m.setContent(etMessage.getText().toString());
            etMessage.setText("");
            conversationReference.child(conversationReference.push().getKey()).setValue(m);
            yourConversation.child("conversation").child(yourConversation.push().getKey()).setValue(m);

            m.setReaded(false);
            myConversation.child("lastMessage").setValue(m);
            yourConversation.child("lastMessage").setValue(m);
            sendPushMessage();
        }
    }

    private void sendPushMessage() {
        if (!TextUtils.isEmpty(conversationItem.getPushToken())) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject sendObject = new JSONObject();
            try {
                sendObject.put("to", conversationItem.getPushToken());
                sendObject.put("collapse_key", conversationItem.getKey());
                sendObject.put("priority", "high");
                sendObject.put("content_available", true);
                JSONObject notification = new JSONObject();
                notification.put("tag", conversationItem.getKey());
                notification.put("title", getString(R.string.app_name));
                notification.put("color", "#ff666e");
                notification.put("sound", "default");
                notification.put("body_loc_key", "notif_sent_message");
                notification.put("body_loc_args", new JSONArray(new String[]{conversationItem.getName()}));
                sendObject.put("notification", notification);
            } catch (Exception x) {

            }
            RequestBody body = RequestBody.create(mediaType, sendObject.toString());
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .addHeader("authorization", "key=AIzaSyCAPtbJ8ZFXLF8ot_hyadW2_zqD9E9fMkE")
                    .addHeader("content-type", "application/json")
                    .build();
            Log.wtf("sendPushMessage", sendObject.toString());
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }
    }
}


