package me.muapp.android.UI.Fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import me.muapp.android.R;

/**
 * Created by rulo on 9/05/17.
 */

public class ProfileVerifiedDialogFragment extends DialogFragment implements View.OnClickListener {
    Button btn_verified_ok;

    public ProfileVerifiedDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    public static ProfileVerifiedDialogFragment newInstance() {
        ProfileVerifiedDialogFragment fragment = new ProfileVerifiedDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_profile_verified, container, false);
        btn_verified_ok = (Button) v.findViewById(R.id.btn_verified_ok);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_verified_ok.setOnClickListener(this);
    }

    private void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getDialog().setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        dismissDialog();
    }
}
