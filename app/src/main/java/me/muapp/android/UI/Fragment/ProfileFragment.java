package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;


public class ProfileFragment extends Fragment implements OnFragmentInteractionListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";

    private User user;
    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic, container, false);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }
}
