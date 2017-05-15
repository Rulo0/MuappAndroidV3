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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import me.muapp.android.Classes.Internal.MuappQualifications.Qualification;
import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.MatchingUserContentAdapter;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;
import me.muapp.android.UI.Fragment.Interface.OnUserRatedListener;
import me.muapp.android.UI.Fragment.Interface.OnUserReportedListener;

public class MatchingUserProfileFragment extends Fragment implements ChildEventListener, OnUserRatedListener, OnUserReportedListener {
    private static final String ARG_MATCHING_USER = "MATCHING_USER";
    private MatchingUser matchingUser;
    private OnMatchingInteractionListener onMatchingInteractionListener;
    private OnProfileScrollListener onProfileScrollListener;
    RecyclerView recycler_user_content;
    MatchingUserContentAdapter adapter;
    DatabaseReference userReference;
    Toolbar toolbar_matching;
    TextView toolbar_matching_name;
    ImageButton btn_matching_rate;
    ImageButton btn_matching_report;
    Boolean imFemale;

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
        imFemale = (User.Gender.getGender(new UserHelper(getContext()).getLoggedUser().getGender()) == User.Gender.Female);
        adapter = new MatchingUserContentAdapter(getContext(), matchingUser);
        adapter.setShowMenuButton(false);
        adapter.setFragmentManager(getChildFragmentManager());
        adapter.setOnProfileScrollListener(onProfileScrollListener);
        userReference = FirebaseDatabase.getInstance().getReference("content").child(String.valueOf(matchingUser.getId()));
        FirebaseDatabase.getInstance().getReference("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
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
                adapter.setQualifications(qualifications.getQualifications(), false);
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
        btn_matching_rate = (ImageButton) v.findViewById(R.id.btn_matching_rate);
        btn_matching_report = (ImageButton) v.findViewById(R.id.btn_matching_report);
        toolbar_matching_name = (TextView) v.findViewById(R.id.toolbar_matching_name);
        recycler_user_content = (RecyclerView) v.findViewById(R.id.recycler_user_content);
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recycler_user_content.setLayoutManager(llm);
        recycler_user_content.setHasFixedSize(true);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar_matching_name.setText(matchingUser.getFullName());
        if (matchingUser.getFakeAccount())
            toolbar_matching_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified_profile, 0, 0, 0);
        recycler_user_content.setAdapter(adapter);
        if (imFemale && matchingUser.getIsFbFriend() && !matchingUser.getIsQualificationed()) {
            btn_matching_rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingFriendDialogFragment ratingFriendDialogFragment = RatingFriendDialogFragment.newInstance(matchingUser);
                    ratingFriendDialogFragment.setOnUserRatedListener(MatchingUserProfileFragment.this);
                    ratingFriendDialogFragment.show(getChildFragmentManager(), "");
                }
            });
        } else {
            btn_matching_rate.setVisibility(View.GONE);
        }
        btn_matching_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportUserDialogFragment reportUserDialogFragment = ReportUserDialogFragment.newInstance(matchingUser);
                reportUserDialogFragment.setOnUserReportedListener(MatchingUserProfileFragment.this);
                reportUserDialogFragment.show(getChildFragmentManager(), "");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.removeAllDescriptions();
        adapter.setUser(matchingUser);
        userReference.addChildEventListener(this);
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

    @Override
    public void onRate(int rating) {
        List<Qualification> qualifications = adapter.getQualificationsList();
        if (qualifications == null) {
            qualifications = new ArrayList<>();
        }
        Qualification q = new Qualification();
        q.setUserName(new UserHelper(getContext()).getLoggedUser().getFullName());
        q.setStars(rating);
        qualifications.add(0, q);
        adapter.setQualifications(qualifications, true);
        btn_matching_rate.setVisibility(View.GONE);
        recycler_user_content.scrollToPosition(1);
        Toast.makeText(getContext(), getString(R.string.lbl_rating_sent), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReport() {
        onMatchingInteractionListener.onReportedUser();
    }
}
