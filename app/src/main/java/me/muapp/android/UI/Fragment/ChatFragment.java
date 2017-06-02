package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import me.muapp.android.Classes.Chat.Conversation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.CrushesAdapter;
import me.muapp.android.UI.Adapter.MatchesAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;


public class ChatFragment extends Fragment implements OnFragmentInteractionListener, ChildEventListener {
    private static final String TAG = "ChatFragment";
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    ProgressUtil progressUtil;
    private User user;
    private OnFragmentInteractionListener mListener;
    MatchesAdapter matchesAdapter;
    CrushesAdapter crushesAdapter;
    // ChatsAdapter chatsAdapter;
    View progress_chats, content_chats;
    RecyclerView recycler_matches, recycler_crushes, recycler_chats;
    DatabaseReference chatReference;
    HashMap<String, ChatItemObject> listenerHashMap = new HashMap();

    public class ChatItemObject {
        String itemKey;
        boolean isCrush;
        DatabaseReference reference;
        ValueEventListener listener;

        public ChatItemObject(final String userProfilePicture, final String itemKey, final boolean isCrush, DatabaseReference reference) {
            this.itemKey = itemKey;
            this.isCrush = isCrush;
            this.reference = reference.child("profilePicture");
            this.listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.getValue(String.class).equals(userProfilePicture))
                        if (!isCrush) {
                            matchesAdapter.updateConversationUser(itemKey, dataSnapshot.getValue(String.class));
                        } else {
                            crushesAdapter.updateConversationUser(itemKey, dataSnapshot.getValue(String.class));
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            this.reference.addValueEventListener(this.listener);
        }

        public String getItemKey() {
            return itemKey;
        }

        public boolean isCrush() {
            return isCrush;
        }

        public DatabaseReference getReference() {
            return reference;
        }

        public ValueEventListener getListener() {
            return listener;
        }
    }

    public ChatFragment() {

    }

    public static ChatFragment newInstance(User user) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_CURRENT_USER);
        }
        matchesAdapter = new MatchesAdapter(getContext());
        crushesAdapter = new CrushesAdapter(getContext());
        chatReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("conversations").child(String.valueOf(user.getId()));
        //chatReference = FirebaseDatabase.getInstance().getReference().child("JW").child(String.valueOf(user.getId()));
        Log.wtf("chatReference", chatReference.getRef().toString());
        chatReference.keepSynced(true);


    }

    @Override
    public void onStart() {
        super.onStart();
        clearRecyclers();
        Log.wtf("chatReference", "Listener added");
        chatReference.addChildEventListener(this);
        if (listenerHashMap.size() > 0) {
            for (Map.Entry entry : listenerHashMap.entrySet()) {
                ChatItemObject object = ((ChatItemObject) entry.getValue());
                object.reference.addValueEventListener(object.getListener());
            }
        }
    }

    private void clearRecyclers() {
        matchesAdapter.clearConversations();
        crushesAdapter.clearConversations();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatReference.removeEventListener(this);
        if (listenerHashMap.size() > 0) {
            for (Map.Entry entry : listenerHashMap.entrySet()) {
                ChatItemObject object = ((ChatItemObject) entry.getValue());
                object.reference.removeEventListener(object.getListener());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        progress_chats = v.findViewById(R.id.progress_chats);
        content_chats = v.findViewById(R.id.content_chats);
        recycler_matches = (RecyclerView) v.findViewById(R.id.recycler_matches);
        recycler_crushes = (RecyclerView) v.findViewById(R.id.recycler_crushes);
        LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getContext());
        linearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManagerHorizontal.setStackFromEnd(true);
        linearLayoutManagerHorizontal.setReverseLayout(true);
        recycler_crushes.setLayoutManager(linearLayoutManagerHorizontal);
        recycler_crushes.setAdapter(crushesAdapter);
        LinearLayoutManager verticalLLM = new LinearLayoutManager(getContext());
        verticalLLM.setStackFromEnd(true);
        verticalLLM.setReverseLayout(true);
        recycler_matches.setLayoutManager(verticalLLM);
        recycler_matches.setAdapter(matchesAdapter);
        progressUtil = new ProgressUtil(getContext(), content_chats, progress_chats);
        progressUtil.showProgress(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }

    private void prepareConversation(final Conversation conversation) {
        final DatabaseReference userInfoReference = FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("users").child(String.valueOf(conversation.getOpponentId()));
        userInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
                if (conversationItem != null) {
                    conversationItem.setKey(conversation.getKey());
                    conversationItem.setConversation(conversation);
                    Log.w("ConversationItem", conversationItem.toString());
                    if (conversationItem.getConversation().getCrush())
                        crushesAdapter.addConversation(conversationItem);
                    else
                        matchesAdapter.addConversation(conversationItem);
                    progressUtil.showProgress(false);
                    listenerHashMap.put(conversation.getKey(), new ChatItemObject(conversationItem.getProfilePicture(), conversation.getKey(), conversation.getCrush(), userInfoReference));

                } else {
                    Log.w("ConversationItem", "IS NULL");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.wtf("Child", "Added");
        Conversation conversation = dataSnapshot.getValue(Conversation.class);
        if (conversation != null) {
            conversation.setKey(dataSnapshot.getKey());
            Log.wtf("CHAT", conversation.toString());
            prepareConversation(conversation);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.wtf("Child", "Changed");
        Conversation conversation = dataSnapshot.getValue(Conversation.class);
        conversation.setKey(dataSnapshot.getKey());
        if (crushesAdapter.isConversationCrush(dataSnapshot.getKey()) && !conversation.getCrush())
            crushesAdapter.removeConversation(conversation.getKey());
        Log.wtf("CHAT", conversation.toString());
        prepareConversation(conversation);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.wtf("Child", "Removed");
        Conversation conversation = dataSnapshot.getValue(Conversation.class);
        if (conversation != null) {
            conversation.setKey(dataSnapshot.getKey());
            if (conversation.getCrush() != null && conversation.getCrush()) {
                crushesAdapter.removeConversation(conversation.getKey());
            } else {
                matchesAdapter.removeConversation(conversation.getKey());
            }
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.wtf("Child", "Moved");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
