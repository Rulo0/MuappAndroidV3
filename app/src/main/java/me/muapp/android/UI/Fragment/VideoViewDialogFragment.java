package me.muapp.android.UI.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

public class VideoViewDialogFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    UserContent content;
    VideoView vv_video_viewer;
    ProgressBar progress_video_view;
    TextView txt_problem_video;

    public VideoViewDialogFragment() {
    }

    public static VideoViewDialogFragment newInstance(UserContent content) {
        VideoViewDialogFragment frag = new VideoViewDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("USER_CONTENT", content);
        frag.setArguments(args);
        return frag;
    }


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video_view_dialog, container);
        vv_video_viewer = (VideoView) v.findViewById(R.id.vv_video_viewer);
        progress_video_view = (ProgressBar) v.findViewById(R.id.progress_video_view);
        txt_problem_video = (TextView) v.findViewById(R.id.txt_problem_video);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content = getArguments().getParcelable("USER_CONTENT");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        try {
            Uri video = Uri.parse(content.getContentUrl());
            vv_video_viewer.setVideoURI(video);
            vv_video_viewer.setOnPreparedListener(this);
            vv_video_viewer.setOnErrorListener(this);
            vv_video_viewer.requestFocus();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        progress_video_view.setVisibility(View.GONE);
        txt_problem_video.setVisibility(View.VISIBLE);
        vv_video_viewer.setVisibility(View.GONE);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        MediaController mediacontroller = new MediaController(getContext());
        mediacontroller.setAnchorView(vv_video_viewer);
        vv_video_viewer.setMediaController(mediacontroller);
        progress_video_view.setVisibility(View.GONE);
        txt_problem_video.setVisibility(View.GONE);
        vv_video_viewer.start();
    }
}