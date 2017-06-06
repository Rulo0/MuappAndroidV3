package me.muapp.android.UI.Fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.muapp.android.Classes.Internal.MuappDialog;
import me.muapp.android.R;

/**
 * Created by rulo on 9/05/17.
 */

public class MuappPopupDialogFragment extends DialogFragment {
    MuappDialog muappDialog;
    static final String ARG_MUAPP_DIALOG = "ARG_MUAPP_DIALOG";
    ImageView imb_dialog_icon;
    TextView txt_dialog_title;
    ImageView img_dialog_content;
    TextView txt_dialog_content;
    Button btn_dialog_more;
    ImageButton btn_dismiss_muapp_dialog;

    public MuappPopupDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            muappDialog = getArguments().getParcelable(ARG_MUAPP_DIALOG);
        }
    }


    public static MuappPopupDialogFragment newInstance(MuappDialog dialog) {
        MuappPopupDialogFragment fragment = new MuappPopupDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MUAPP_DIALOG, dialog);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_muapp_dialog, container, false);
        txt_dialog_title = (TextView) v.findViewById(R.id.txt_dialog_title);
        txt_dialog_content = (TextView) v.findViewById(R.id.txt_dialog_content);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txt_dialog_title.setText(muappDialog.getTitle());
        txt_dialog_content.setText(muappDialog.getContentText());
    }

    private void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
    }
}
