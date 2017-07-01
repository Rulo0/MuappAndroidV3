package me.muapp.android.UI.Fragment;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.muapp.android.R;

/**
 * Created by rulo on 9/05/17.
 */

public class PictureViewDialogFragment extends DialogFragment {
    String picture_url;
    ImageView img_photo_viewer;
    static final String ARG_PICTURE_URL = "ARG_PICTURE_URL";

    public PictureViewDialogFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            picture_url = getArguments().getString(ARG_PICTURE_URL);
        }
    }

    public static PictureViewDialogFragment newInstance(String pictureUrl) {
        PictureViewDialogFragment fragment = new PictureViewDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PICTURE_URL, pictureUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_picture_view, container, false);
        img_photo_viewer = (ImageView) v.findViewById(R.id.img_photo_viewer);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this).load(picture_url).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(img_photo_viewer);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}
