package me.muapp.android.UI.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import me.muapp.android.R;

public class AddContentDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    FrameLayout bottomSheet;
    private static final String ARG_IS_FROM_MAN_GATE = "IS_FROM_MAN_GATE";
    private Listener mListener;
    boolean isFromManGate;
    ImageButton btn_add_quote, btn_add_voice, btn_add_photo, btn_add_giphy, btn_add_spotify, btn_add_youtube;

    public static AddContentDialogFragment newInstance(boolean fromManGate) {
        final AddContentDialogFragment fragment = new AddContentDialogFragment();
        final Bundle args = new Bundle();
        args.putBoolean(ARG_IS_FROM_MAN_GATE, fromManGate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFromManGate = getArguments().getBoolean(ARG_IS_FROM_MAN_GATE, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addcontent_list_dialog, container, false);
        btn_add_quote = (ImageButton) v.findViewById(R.id.btn_add_quote);
        btn_add_voice = (ImageButton) v.findViewById(R.id.btn_add_voice);
        btn_add_photo = (ImageButton) v.findViewById(R.id.btn_add_photo);
        btn_add_giphy = (ImageButton) v.findViewById(R.id.btn_add_giphy);
        btn_add_spotify = (ImageButton) v.findViewById(R.id.btn_add_spotify);
        btn_add_youtube = (ImageButton) v.findViewById(R.id.btn_add_youtube);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFromManGate) {
            btn_add_quote.setEnabled(false);
            btn_add_photo.setEnabled(false);
            btn_add_giphy.setEnabled(false);
            btn_add_spotify.setEnabled(false);
            btn_add_youtube.setEnabled(false);
            btn_add_quote.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_quotes_disabled));
            btn_add_photo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_photo_disabled));
            btn_add_giphy.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_giphy_disabled));
            btn_add_spotify.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_spotify_disabled));
            btn_add_youtube.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_youtube_disabled));
        }
        btn_add_quote.setOnClickListener(this);
        btn_add_voice.setOnClickListener(this);
        btn_add_photo.setOnClickListener(this);
        btn_add_giphy.setOnClickListener(this);
        btn_add_spotify.setOnClickListener(this);
        btn_add_youtube.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        if (mListener != null) {
            mListener.onAddContentClicked(v.getId());
        }
    }

    public interface Listener {
        void onAddContentClicked(int buttonId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }
}
