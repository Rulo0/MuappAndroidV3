package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.MessagesAdapter;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class ChatActivity extends BaseActivity implements ChildEventListener {
    public static final String CONVERSATION_EXTRA = "CONVERSATION_EXTRA";
    ConversationItem conversationItem;
    Toolbar toolbar;
    TextView toolbar_opponent_fullname;
    RecyclerView recycler_conversation;
    DatabaseReference conversationReference;
    MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        conversationItem = getIntent().getParcelableExtra(CONVERSATION_EXTRA);
        if (conversationItem == null)
            finish();
        messagesAdapter = new MessagesAdapter(this);
        messagesAdapter.setLoggedUserId(loggedUser.getId());
        conversationReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getKey())
                .child("conversation");
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        conversationReference.addChildEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversationReference.removeEventListener(this);
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
}


