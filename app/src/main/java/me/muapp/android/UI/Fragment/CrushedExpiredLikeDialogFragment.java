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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Calendar;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnTimeExpiredListener;


/**
 * Created by rulo on 09/06/17.
 */

public class CrushedExpiredLikeDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String ARG_CONVERSATION_CRUSH_LIKE = "ARG_CONVERSATION_CRUSH_LIKE";
    ImageView img_photo_expired_like;
    TextView txt_content_expired_like;
    ConversationItem conversationItem;
    OnTimeExpiredListener onTimeExpiredListener;
    Boolean cancelable = false;

    public void setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
    }

    public static CrushedExpiredLikeDialogFragment newInstance(ConversationItem conversationItem) {
        CrushedExpiredLikeDialogFragment fragment = new CrushedExpiredLikeDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONVERSATION_CRUSH_LIKE, conversationItem);
        fragment.setArguments(args);
        return fragment;
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
            conversationItem = getArguments().getParcelable(ARG_CONVERSATION_CRUSH_LIKE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_crush_expired_like, container, false);
        img_photo_expired_like = (ImageView) v.findViewById(R.id.img_photo_expired_like);
        txt_content_expired_like = (TextView) v.findViewById(R.id.txt_content_expired_like);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this).load(conversationItem.getProfilePicture()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(getContext())).into(img_photo_expired_like);
        final Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(conversationItem.getConversation().getCreationDate());
        expirationDate.add(Calendar.DATE, 1);
        long difference = expirationDate.getTime().getTime() - Calendar.getInstance().getTime().getTime();
        if (difference <= 0) {
            txt_content_expired_like.setText(String.format(getString(R.string.lbl_wait_for_muapp), conversationItem.getName()));
        } else {
            txt_content_expired_like.setText(String.format(getString(R.string.lbl_wait_for_muapp_no_expired), conversationItem.getName()));
        }

        img_photo_expired_like.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onTimeExpiredListener.onPictureClicked();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onTimeExpiredListener.onExpiredCancel();
    }
}
