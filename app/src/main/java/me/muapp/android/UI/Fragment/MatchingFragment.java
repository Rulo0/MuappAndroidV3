package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.MatchingUsersHandler;
import me.muapp.android.Classes.Internal.MatchingResult;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.MainActivity;
import me.muapp.android.UI.Fragment.Interface.OnAllUsersLoadedListener;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;


public class MatchingFragment extends Fragment implements OnFragmentInteractionListener, MatchingUsersHandler, OnMatchingInteractionListener, OnAllUsersLoadedListener, View.OnClickListener, OnProfileScrollListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    private User user;
    private OnFragmentInteractionListener mListener;
    Handler handler;
    int matchingUsersPage = 1;
    int waitTime = 10;
    List<MatchingUserProfileFragment> matchingFragmentList = new ArrayList<>();
    RelativeLayout container_actions_matching;
    ImageButton btn_muapp_matching, btn_crush_matching, btn_no_muapp_matching;
    Boolean b = true;

    public MatchingFragment() {

    }

    public void performUnlike() {
        btn_no_muapp_matching.performClick();
    }

    public static MatchingFragment newInstance(User user) {
        MatchingFragment fragment = new MatchingFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matching, container, false);
        container_actions_matching = (RelativeLayout) v.findViewById(R.id.container_actions_matching);
        btn_muapp_matching = (ImageButton) v.findViewById(R.id.btn_muapp_matching);
        btn_crush_matching = (ImageButton) v.findViewById(R.id.btn_crush_matching);
        btn_no_muapp_matching = (ImageButton) v.findViewById(R.id.btn_no_muapp_matching);

        return v;
    }

    public void onButtonPressed(String name, Object object) {
        if (mListener != null) {
            mListener.onFragmentInteraction(name, object);
        }
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (((MainActivity) getContext()).getCurrentLocation() != null)
                    getMatchingUsers();
                else
                    handler.postDelayed(this, waitTime);
            }
        };
        handler.postDelayed(runnable, waitTime);
        btn_muapp_matching.setOnClickListener(this);
        btn_crush_matching.setOnClickListener(this);
        btn_no_muapp_matching.setOnClickListener(this);
        replaceFragment(GetMatchingUsersFragment.newInstance(user));
    }

    private void getMatchingUsers() {
        new APIService(getContext()).getMatchingUsers(matchingUsersPage, ((MainActivity) getContext()).getCurrentLocation(), this);
        matchingUsersPage++;
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
    public void onFailure(boolean isSuccessful, String responseString) {

    }

    @Override
    public void onSuccess(int responseCode, MatchingResult result) {
        if (result.getMatchingUsers().size() > 0) {
            for (final MatchingUser user : result.getMatchingUsers()) {
                if (!TextUtils.isEmpty(user.getDescription()))
                    uploadDescriptionToFirebase(user.getId(), user.getDescription());
                MatchingUserProfileFragment fragment = MatchingUserProfileFragment.newInstance(user);
                fragment.setOnMatchingInteractionListener(this);
                fragment.setOnProfileScrollListener(this);
                matchingFragmentList.add(fragment);
            }
            onAllUsersLoaded();
            matchingUsersPage++;
        }
    }

    @Override
    public void onLikeClicked(MatchingUser user) {

    }

    @Override
    public void onUnlikeClicked(MatchingUser user) {

    }

    @Override
    public void onCrushCliched(MatchingUser user) {

    }

    @Override
    public void onReportedUser() {
        btn_no_muapp_matching.performClick();
    }

    private void replaceFragment(Fragment frag) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        ft.replace(R.id.content_matching_profiles, frag);
        ft.commit();
    }

    private void showControls(final Boolean show) {
        Handler mainHandler = new Handler(getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Utils.animViewScale(getContext(), container_actions_matching, show);
            }
        };
        mainHandler.post(myRunnable);

    }

    private void uploadDescriptionToFirebase(int matchingUserId, final String description) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("content").child(String.valueOf(matchingUserId));
        reference.orderByChild("catContent").equalTo("contentDesc").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    boolean descriptionFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserContent content = snapshot.getValue(UserContent.class);
                        if (!content.getComment().equals(description)) {
                            snapshot.getRef().removeValue();
                        } else {
                            descriptionFound = true;
                        }
                    }
                    if (!descriptionFound) {
                        UserContent content = new UserContent();
                        content.setCreatedAt(32535237599000L);
                        content.setCatContent("contentDesc");
                        content.setComment(description);
                        content.setLikes(0);
                        reference.child(reference.push().getKey()).setValue(content);
                    }
                } else {
                    UserContent content = new UserContent();
                    content.setCreatedAt(32535237599000L);
                    content.setCatContent("contentDesc");
                    content.setComment(description);
                    content.setLikes(0);
                    reference.child(reference.push().getKey()).setValue(content);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAllUsersLoaded() {
        replaceFragment(matchingFragmentList.get(0));
        showControls(true);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_muapp_matching:
                    new APIService(getContext()).likeUser(matchingFragmentList.get(0).getMatchingUser().getId(), null, null);
                    break;
                case R.id.btn_no_muapp_matching:
                    new APIService(getContext()).dislikeUser(matchingFragmentList.get(0).getMatchingUser().getId(), null);
                    break;
            }
        } catch (Exception x) {
            x.printStackTrace();
        }


        try {
            matchingFragmentList.remove(0);
            if (matchingFragmentList.size() > 0) {
                replaceFragment(matchingFragmentList.get(0));
            } else {
                showControls(false);
                replaceFragment(GetMatchingUsersFragment.newInstance(user));
                getMatchingUsers();
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public void onScrollToTop() {
        showControls(true);
    }

    @Override
    public void onScroll() {
        showControls(false);
    }
}
