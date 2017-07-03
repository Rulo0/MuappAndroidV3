package me.muapp.android.UI.Fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Internal.MuappDialog;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;

/**
 * Created by rulo on 9/05/17.
 */

public class MuappPopupDialogFragment extends DialogFragment implements View.OnClickListener {
    MuappDialog muappDialog;
    static final String ARG_MUAPP_DIALOG = "ARG_MUAPP_DIALOG";
    ImageView imb_dialog_icon;
    TextView txt_dialog_title;
    ImageView img_dialog_content;
    TextView txt_dialog_content;
    Button btn_dialog_more_info;
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
        imb_dialog_icon = (ImageView) v.findViewById(R.id.imb_dialog_icon);
        img_dialog_content = (ImageView) v.findViewById(R.id.img_dialog_content);
        btn_dialog_more_info = (Button) v.findViewById(R.id.btn_dialog_more_info);
        btn_dismiss_muapp_dialog = (ImageButton) v.findViewById(R.id.btn_dismiss_muapp_dialog);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txt_dialog_title.setText(muappDialog.getTitle());
        txt_dialog_content.setText(muappDialog.getContentText());
        btn_dialog_more_info.setText(muappDialog.getExtraButtonTitle());
        Glide.with(this).load(muappDialog.getHeaderIconUrl()).placeholder(R.drawable.ic_placeholder_white).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(getContext())).into(imb_dialog_icon);
        Glide.with(this).load(muappDialog.getContentImageUrl()).placeholder(R.drawable.ic_placeholder_white).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(img_dialog_content);
        if (muappDialog.getShowCancelButton() != null && !muappDialog.getShowCancelButton())
            btn_dismiss_muapp_dialog.setVisibility(View.GONE);
        btn_dismiss_muapp_dialog.setOnClickListener(this);
        btn_dialog_more_info.setOnClickListener(this);
    }

    private void dismissDialog() {
        new PreferenceHelper(getContext()).putDialogAsSeen(muappDialog.getKey());
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dismiss_muapp_dialog:
                dismissDialog();
                break;
            case R.id.btn_dialog_more_info:
                goToAction();
                break;
        }
    }

    private void goToAction() {
        if (!TextUtils.isEmpty(muappDialog.getDialogExternalUrl())) {
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            intentBuilder.setShowTitle(true);
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), android.R.color.white));
            intentBuilder.setStartAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            intentBuilder.setExitAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.launchUrl(getContext(), Uri.parse(muappDialog.getDialogExternalUrl()));
            dismissDialog();
        }
    }
}
