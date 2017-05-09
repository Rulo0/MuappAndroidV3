package me.muapp.android.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserQualificationsHandler;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.MatchingUserContentAdapter;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;

public class MatchingUserProfileFragment extends Fragment implements ChildEventListener {
    private static final String ARG_MATCHING_USER = "MATCHING_USER";
    private MatchingUser matchingUser;
    private OnMatchingInteractionListener onMatchingInteractionListener;
    private OnProfileScrollListener onProfileScrollListener;
    RecyclerView recycler_user_content;
    MatchingUserContentAdapter adapter;
    DatabaseReference userReference;
    Toolbar toolbar_matching;
    TextView toolbar_matching_name;

    public MatchingUser getMatchingUser() {
        return matchingUser;
    }

    public void setOnMatchingInteractionListener(OnMatchingInteractionListener onMatchingInteractionListener) {
        this.onMatchingInteractionListener = onMatchingInteractionListener;
    }

    public void setOnProfileScrollListener(OnProfileScrollListener onProfileScrollListener) {
        this.onProfileScrollListener = onProfileScrollListener;
    }

    public MatchingUserProfileFragment() {
        // Required empty public constructor
    }

    public static MatchingUserProfileFragment newInstance(MatchingUser user) {
        MatchingUserProfileFragment fragment = new MatchingUserProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MATCHING_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            matchingUser = getArguments().getParcelable(ARG_MATCHING_USER);
        }
        adapter = new MatchingUserContentAdapter(getContext(), matchingUser);
        adapter.setShowMenuButton(false);
        adapter.setFragmentManager(getChildFragmentManager());
        userReference = FirebaseDatabase.getInstance().getReference().child("content").child(String.valueOf(matchingUser.getId()));
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

        new APIService(getContext()).getUserQualifications(matchingUser.getId(), new UserQualificationsHandler() {
            @Override
            public void onSuccess(int responseCode, UserQualifications qualifications) {
                adapter.setQualifications(qualifications.getQualifications());
            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matching_user_profile, container, false);
        toolbar_matching = (Toolbar) v.findViewById(R.id.toolbar_matching);
        toolbar_matching_name = (TextView) v.findViewById(R.id.toolbar_matching_name);
        recycler_user_content = (RecyclerView) v.findViewById(R.id.recycler_user_content);
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recycler_user_content.setLayoutManager(llm);
        recycler_user_content.setHasFixedSize(true);
        recycler_user_content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pos = llm.findFirstVisibleItemPosition();
                if (llm.findViewByPosition(pos).getTop() == 0 && pos == 0) {
                    onProfileScrollListener.onScrollToTop();
                } else {
                    onProfileScrollListener.onScroll();
                }
            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar_matching_name.setText(matchingUser.getFullName());
        if (matchingUser.getFakeAccount())
            toolbar_matching_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_profile, 0, 0, 0);
        recycler_user_content.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.removeAllDescriptions();
        adapter.setUser(matchingUser);
        userReference.addChildEventListener(this);
        new RatingFriendDialog().show(getChildFragmentManager(), "");

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopMediaPlayer();
        userReference.removeEventListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.releaseMediaPlayer();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.onMatchingInteractionListener = null;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.addContent(c);
            ((LinearLayoutManager) recycler_user_content.getLayoutManager()).scrollToPositionWithOffset(0, 0);
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
