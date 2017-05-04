package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;

public class YoutubeViewActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener {
    YouTubePlayerFragment youtube_fragment_view;
    UserContent thisContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        thisContent = getIntent().getParcelableExtra("itemContent");
        youtube_fragment_view = (YouTubePlayerFragment) getFragmentManager()
                .findFragmentById(R.id.youtube_fragment_view);
        youtube_fragment_view.initialize(getYoutubeApiKey(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.MINIMAL;
        youTubePlayer.setPlayerStyle(style);
        if (!wasRestored) {
            youTubePlayer.loadVideo(thisContent.getVideoId());
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }


}
