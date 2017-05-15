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
import com.google.firebase.database.FirebaseDatabase;

import me.muapp.android.Classes.Chat.Conversation;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.CrushesAdapter;
import me.muapp.android.UI.Adapter.MatchesAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;


public class ChatFragment extends Fragment implements OnFragmentInteractionListener {
    private static final String TAG = "ChatFragment";
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    ProgressUtil progressUtil;
    private User user;
    private OnFragmentInteractionListener mListener;
    MatchesAdapter matchesAdapter;
    CrushesAdapter crushesAdapter;
    View progress_chats, content_chats;
    RecyclerView recycler_matches, recycler_crushes;

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
        recycler_crushes.setLayoutManager(linearLayoutManagerHorizontal);
        recycler_crushes.setAdapter(crushesAdapter);
        recycler_matches.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_matches.setAdapter(matchesAdapter);
        progressUtil = new ProgressUtil(getContext(), content_chats, progress_chats);
        progressUtil.showProgress(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("conversations").child("45430").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                Log.wtf("CHAT", conversation.toString());
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
        });
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
}
