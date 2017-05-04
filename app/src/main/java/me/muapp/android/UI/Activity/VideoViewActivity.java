package me.muapp.android.UI.Activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

import static android.media.MediaPlayer.OnErrorListener;
import static android.media.MediaPlayer.OnPreparedListener;

public class VideoViewActivity extends BaseActivity implements OnPreparedListener, OnErrorListener {
    VideoView vv_video_viewer;
    ProgressBar progress_video_view;
    TextView txt_problem_video;
    private UserContent thisContent;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        thisContent = getIntent().getParcelableExtra("itemContent");
        if (thisContent != null) {
            Log.wtf("VideoView", thisContent.toString());
            vv_video_viewer = (VideoView) findViewById(R.id.vv_video_viewer);
            progress_video_view = (ProgressBar) findViewById(R.id.progress_video_view);
            txt_problem_video = (TextView) findViewById(R.id.txt_problem_video);
        } else {
            Log.wtf("VideoView", "thisContent is null");
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
            Uri video = Uri.parse(thisContent.getContentUrl());
            Log.wtf("VideoView video uri", video.toString());
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
        MediaController mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(vv_video_viewer);
        vv_video_viewer.setMediaController(mediacontroller);
        progress_video_view.setVisibility(View.GONE);
        txt_problem_video.setVisibility(View.GONE);
        vv_video_viewer.start();

    }
}
