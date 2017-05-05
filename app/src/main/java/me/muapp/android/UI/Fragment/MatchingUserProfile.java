package me.muapp.android.UI.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;

public class MatchingUserProfile extends Fragment {
    private static final String ARG_MATCHING_USER = "MATCHING_USER";
    private MatchingUser matchingUser;
    private OnMatchingInteractionListener onMatchingInteractionListener;
    TextView txt_matching_name;

    public void setOnMatchingInteractionListener(OnMatchingInteractionListener onMatchingInteractionListener) {
        this.onMatchingInteractionListener = onMatchingInteractionListener;
    }

    public MatchingUserProfile() {
        // Required empty public constructor
    }

    public static MatchingUserProfile newInstance(MatchingUser user) {
        MatchingUserProfile fragment = new MatchingUserProfile();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matching_user_profile, container, false);
        txt_matching_name = (TextView) v.findViewById(R.id.txt_matching_name);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        txt_matching_name.setText(matchingUser.getFirstName() + " " + matchingUser.getLastName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.onMatchingInteractionListener = null;
    }
}
