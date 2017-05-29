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

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class ViewUserProfileFragment extends Fragment implements ChildEventListener, OnUserRatedListener, OnUserReportedListener {
    private static final String ARG_USER = "USER";
    private MatchingUser matchingUser;
    private OnMatchingInteractionListener onMatchingInteractionListener;
    private OnProfileScrollListener onProfileScrollListener = null;
    RecyclerView recycler_user_content_view;
    MatchingUserContentAdapter adapter;
    DatabaseReference userReference;
    Boolean imFemale;

    public MatchingUser getMatchingUser() {
        return matchingUser;
    }

    public void setOnMatchingInteractionListener(OnMatchingInteractionListener onMatchingInteractionListener) {
        this.onMatchingInteractionListener = onMatchingInteractionListener;
    }

    public ViewUserProfileFragment() {
        // Required empty public constructor
    }

    public static ViewUserProfileFragment newInstance(MatchingUser user) {
        ViewUserProfileFragment fragment = new ViewUserProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            matchingUser = getArguments().getParcelable(ARG_USER);
        }
        imFemale = (User.Gender.getGender(new UserHelper(getContext()).getLoggedUser().getGender()) == User.Gender.Female);
        adapter = new MatchingUserContentAdapter(getContext(), matchingUser);
        adapter.setShowMenuButton(false);
        adapter.setFragmentManager(getChildFragmentManager());


        adapter.setOnProfileScrollListener(onProfileScrollListener);
        userReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("content").child(String.valueOf(matchingUser.getId()));
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

        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("profilePicture").setValue(matchingUser.getAlbum().get(0));
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("name").setValue(matchingUser.getFirstName());
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(matchingUser.getId())).child("lastName").setValue(matchingUser.getLastName());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_user_profile, container, false);
        recycler_user_content_view = (RecyclerView) v.findViewById(R.id.recycler_user_content_view);
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recycler_user_content_view.setLayoutManager(llm);
        recycler_user_content_view.setHasFixedSize(true);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileScrollListener) {
            onProfileScrollListener = (OnProfileScrollListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProfileScrollListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_user_content_view.setAdapter(adapter);

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
            ((LinearLayoutManager) recycler_user_content_view.getLayoutManager()).scrollToPositionWithOffset(0, 0);
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
        recycler_user_content_view.scrollToPosition(0);
        Toast.makeText(getContext(), getString(R.string.lbl_rating_sent), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReport() {
        onMatchingInteractionListener.onReportedUser();
    }
}
