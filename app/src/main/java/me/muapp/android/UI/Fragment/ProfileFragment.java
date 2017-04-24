package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.SectionedProfileAdapter;
import me.muapp.android.UI.Adapter.UserContentAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;


public class ProfileFragment extends Fragment implements OnFragmentInteractionListener, ChildEventListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    User user;
    private OnFragmentInteractionListener mListener;
    DatabaseReference myUserReference;
    UserContentAdapter adapter;
    RecyclerView recycler_my_content;
    SectionedProfileAdapter mSectionedAdapter;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_CURRENT_USER);
        }
        adapter = new UserContentAdapter(getContext());
        adapter.setFragmentTransaction(getChildFragmentManager().beginTransaction());
        List<SectionedProfileAdapter.Section> sections = new ArrayList<>();
        sections.add(new SectionedProfileAdapter.Section(0, user));
        SectionedProfileAdapter.Section[] dummy = new SectionedProfileAdapter.Section[sections.size()];
        mSectionedAdapter = new
                SectionedProfileAdapter(getContext(), R.layout.profile_header_layout, R.id.pillbox_section_text, R.id.pillbox_divider_icon, adapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        myUserReference = FirebaseDatabase.getInstance().getReference().child("content").child(String.valueOf(user.getId()));
        FirebaseDatabase.getInstance().getReference().child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MuappQuote> quoteList = new ArrayList<>();
                try {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        MuappQuote q = s.getValue(MuappQuote.class);
                        if (q != null) {
                            q.setKey(s.getKey());
                            quoteList.add(q);
                        }
                    }
                    adapter.setQuoteList(quoteList);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        recycler_my_content = (RecyclerView) v.findViewById(R.id.recycler_my_content);
        recycler_my_content.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_my_content.setNestedScrollingEnabled(false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_my_content.setAdapter(mSectionedAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        myUserReference.addChildEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopMediaPlayer();
        myUserReference.removeEventListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.releaseMediaPlayer();
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

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.addContent(c);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.removeContent(dataSnapshot.getKey());
            adapter.addContent(c);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            adapter.removeContent(dataSnapshot.getKey());
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.addContent(c);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
