package me.muapp.android.UI.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.MatchingUsersHandler;
import me.muapp.android.Classes.Internal.MatchingResult;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.MainActivity;
import me.muapp.android.UI.Fragment.Interface.OnAllUsersLoadedListener;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;


public class MatchingFragment extends Fragment implements OnFragmentInteractionListener, MatchingUsersHandler, OnMatchingInteractionListener, OnAllUsersLoadedListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    private User user;
    private OnFragmentInteractionListener mListener;
    Handler handler;
    int matchingUsersPage = 1;
    int waitTime = 10;
    TextView textView2;
    List<MatchingUserProfile> matchingFragmentList = new ArrayList<>();
    int fragmentPos = 0;

    public MatchingFragment() {

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
        textView2 = (TextView) v.findViewById(R.id.textView2);
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
    }

    private void getMatchingUsers() {
        new APIService(getContext()).getMatchingUsers(matchingUsersPage, ((MainActivity) getContext()).getCurrentLocation(), this);
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
                Log.i("getMatchingUsers", user.toString());
                MatchingUserProfile fragment = MatchingUserProfile.newInstance(user);
                fragment.setOnMatchingInteractionListener(this);
                matchingFragmentList.add(fragment);
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView2.append(user.toString());
                        textView2.append("\n\n");
                    }
                });
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
    public void onAllUsersLoaded() {
        Log.wtf("AllUsersLoaded", "triggered");
        Runnable changeFragments = new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.replace(R.id.content_matching_profiles, matchingFragmentList.get(fragmentPos));
                ft.commit();
                fragmentPos++;
                if (fragmentPos < matchingFragmentList.size())
                    handler.postDelayed(this, waitTime * 100);

            }
        };
        handler.postDelayed(changeFragments, waitTime);

    }
}
