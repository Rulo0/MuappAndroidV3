package me.muapp.android.UI.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnTimeExpiredListener;

/**
 * Created by rulo on 9/05/17.
 */

public class CrushExpiredDialogFragment extends DialogFragment implements View.OnClickListener {
    ConversationItem conversationItem;
    ImageView img_photo_expired;
    TextView txt_content_expired;
    ImageButton imb_expired_no_muapp, imb_expired_muapp;
    static final String ARG_CONVERSATION_CRUSH = "ARG_PICTURE_URL";
    OnTimeExpiredListener onTimeExpiredListener;

    public CrushExpiredDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimeExpiredListener) {
            onTimeExpiredListener = (OnTimeExpiredListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimeExpiredListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            conversationItem = getArguments().getParcelable(ARG_CONVERSATION_CRUSH);
        }
    }

    public static CrushExpiredDialogFragment newInstance(ConversationItem conversationItem) {
        CrushExpiredDialogFragment fragment = new CrushExpiredDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONVERSATION_CRUSH, conversationItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_crush_expired, container, false);
        img_photo_expired = (ImageView) v.findViewById(R.id.img_photo_expired);
        txt_content_expired = (TextView) v.findViewById(R.id.txt_content_expired);
        imb_expired_no_muapp = (ImageButton) v.findViewById(R.id.imb_expired_no_muapp);
        imb_expired_muapp = (ImageButton) v.findViewById(R.id.imb_expired_muapp);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this).load(conversationItem.getProfilePicture()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(getContext())).into(img_photo_expired);
        txt_content_expired.setText(String.format(getString(R.string.format_time_to_choose), conversationItem.getName()));
        imb_expired_no_muapp.setOnClickListener(this);
        imb_expired_muapp.setOnClickListener(this);
        img_photo_expired.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        Bundle params = new Bundle();
        params.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
        params.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.Conversation_Crush.toString());
        switch (v.getId()) {
            case R.id.imb_expired_no_muapp:
                onTimeExpiredListener.onExpiredNoMuapp();
                FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Dismiss.toString(), params);
                this.dismiss();
                break;
            case R.id.imb_expired_muapp:
                onTimeExpiredListener.onExpiredMuapp();
                FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Muapp.toString(), params);
                this.dismiss();
                break;
            case R.id.img_photo_expired:
                onTimeExpiredListener.onPictureClicked();
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onTimeExpiredListener.onExpiredCancel();
    }
}
