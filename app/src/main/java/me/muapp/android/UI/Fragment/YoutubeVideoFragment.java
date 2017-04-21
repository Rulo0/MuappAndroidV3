package me.muapp.android.UI.Fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import me.muapp.android.R;

/**
 * Created by rulo on 12/04/17.
 */
public class YoutubeVideoFragment extends DialogFragment implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerSupportFragment youtube_dialog_fragment;
    String videoId;

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.youtube_dialog_layout, container, false);
        youtube_dialog_fragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_dialog_fragment);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
     //   youtube_dialog_fragment.initialize(getYoutubeApiKey(), this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.MINIMAL;
        youTubePlayer.setPlayerStyle(style);
        if (!wasRestored) {
            youTubePlayer.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}