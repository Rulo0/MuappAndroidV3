package me.muapp.android.UI.Activity;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;

public class YoutubeViewActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
    private YouTubePlayerView youtube_player;
    UserContent thisContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_view);
        youtube_player = (YouTubePlayerView) findViewById(R.id.youtube_player);
        thisContent = getIntent().getParcelableExtra("itemContent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        youtube_player.initialize(getYoutubeApiKey(), this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(thisContent.getVideoId());
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
