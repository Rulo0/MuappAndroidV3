package me.muapp.android.UI.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.MutualFriendsHandler;
import me.muapp.android.Classes.API.Handlers.UserQualificationsHandler;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.MuappQualifications.Qualification;
import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.MutualFriends;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.Log;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.Tutorials;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.SplashActivity;
import me.muapp.android.UI.Adapter.MatchingUserContentAdapter;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;
import me.muapp.android.UI.Fragment.Interface.OnUserRatedListener;
import me.muapp.android.UI.Fragment.Interface.OnUserReportedListener;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class MatchingUserProfileFragment extends Fragment implements ChildEventListener, OnUserRatedListener, OnUserReportedListener, ValueEventListener {
    private static final String ARG_MATCHING_USER = "MATCHING_USER";
    private MatchingUser matchingUser;
    private OnMatchingInteractionListener onMatchingInteractionListener;
    private OnProfileScrollListener onProfileScrollListener = null;
    RecyclerView recycler_user_content;
    MatchingUserContentAdapter adapter;
    DatabaseReference userReference;
    Toolbar toolbar_matching;
    TextView toolbar_matching_name;
    ImageButton btn_matching_rate;
    ImageButton btn_matching_report;
    Boolean imFemale;
    TapTargetView tutorialQualification;

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
        Log.wtf("Creating", "MatchingUserProfileFragment");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                matchingUser = getArguments().getParcelable(ARG_MATCHING_USER);
                int id = matchingUser.getId();
            } catch (Exception x) {
                startActivity(new Intent(getContext(), SplashActivity.class));
                getActivity().finish();
            }
        }
        imFemale = (User.Gender.getGender(new UserHelper(getContext()).getLoggedUser().getGender()) == User.Gender.Female);
        Log.wtf("imFemale?", "" + imFemale);
        adapter = new MatchingUserContentAdapter(getContext(), matchingUser);
        adapter.setShowMenuButton(false);
        adapter.setFragmentManager(getChildFragmentManager());
        adapter.setOnProfileScrollListener(onProfileScrollListener);
        try {
            userReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("content").child(String.valueOf(matchingUser.getId()));
        } catch (Exception x) {
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
            return;
        }
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
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
        if (matchingUser.getCommonFriendships() > 0) {
            new APIService(getContext()).getMutualFriends(matchingUser.getId(), new MutualFriendsHandler() {
                @Override
                public void onSuccess(int responseCode, MutualFriends mutualFriends) {
                    adapter.setMutualFriends(mutualFriends.getMutualFriends());
                }

                @Override
                public void onFailure(boolean isSuccessful, String responseString) {

                }
            });
        }


        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("profilePicture").setValue(matchingUser.getAlbum().get(0));
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("name").setValue(matchingUser.getFirstName());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("lastName").setValue(matchingUser.getLastName());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("online").setValue(false);
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
        //if (!imFemale)
        //  btn_matching_report.setVisibility(View.GONE);
        toolbar_matching_name.setText(matchingUser.getFullName());
        if (matchingUser.getFakeAccount() != null && matchingUser.getFakeAccount())
            toolbar_matching_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified_profile, 0, 0, 0);
        recycler_user_content.setAdapter(adapter);
        if (imFemale && matchingUser.getIsFbFriend() && !matchingUser.getIsQualificationed()) {
            btn_matching_rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateFriend();
                }
            });
            if (new PreferenceHelper(getContext()).getTutorialRate() && !isHidden() && !new PreferenceHelper(getContext()).getTutorialCrush()) {
                tutorialQualification = new Tutorials(getActivity()).showTutorialForView(btn_matching_rate, false, getString(R.string.lbl_tutorial_rate_title), getString(R.string.lbl_tutorial_rate_content), 23, new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        rateFriend();
                        new PreferenceHelper(getContext()).putTutorialRateDisabled();
                    }
                });
            } else {
                if (!new PreferenceHelper(getContext()).getTutorialCrush())
                    rateFriend();
            }
        } else {
            btn_matching_rate.setVisibility(View.GONE);
        }
        btn_matching_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportUser();
            }
        });
    }

    public void scrollProfileToTop() {
        ((LinearLayoutManager) recycler_user_content.getLayoutManager()).scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden)
            stopPlayer();
    }

    private void reportUser() {
        ReportUserDialogFragment reportUserDialogFragment = ReportUserDialogFragment.newInstance(matchingUser.getId());
        reportUserDialogFragment.setOnUserReportedListener(this);
        reportUserDialogFragment.show(getChildFragmentManager(), matchingUser.getLastName());
    }

    private void rateFriend() {
        RatingFriendDialogFragment ratingFriendDialogFragment = RatingFriendDialogFragment.newInstance(matchingUser);
        ratingFriendDialogFragment.setOnUserRatedListener(MatchingUserProfileFragment.this);
        ratingFriendDialogFragment.show(getChildFragmentManager(), matchingUser.getFirstName());
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.removeAllDescriptions();
        adapter.setUser(matchingUser);
        if (!TextUtils.isEmpty(matchingUser.getDescription())) {
            Log.wtf("OnStart", matchingUser.getDescription());
            UserContent content = new UserContent();
            content.setCreatedAt(32535237599000L);
            content.setCatContent("contentDesc");
            content.setKey(String.valueOf(new Date().getTime()));
            content.setComment(matchingUser.getDescription());
            content.setLikes(0);
            adapter.addContent(content);
            adapter.notifyItemChanged(2);
        }
        userReference.addChildEventListener(this);
        userReference.addValueEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopMediaPlayer();
        userReference.removeEventListener((ChildEventListener) this);
        userReference.removeEventListener((ValueEventListener) this);
    }


    public void stopPlayer() {
        try {
            adapter.stopMediaPlayer();
        } catch (Exception x) {
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            adapter.releaseMediaPlayer();
            if (tutorialQualification != null && tutorialQualification.isVisible())
                tutorialQualification.dismiss(false);
        } catch (Exception x) {
        }
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
        if (conversationItem != null) {
            conversationItem.getFullName();
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
