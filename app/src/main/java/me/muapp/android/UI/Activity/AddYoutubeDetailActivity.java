package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.youtube.player.YouTubePlayer;

import me.muapp.android.Classes.Youtube.Data.YoutubeVideo;
import me.muapp.android.R;

public class AddYoutubeDetailActivity extends BaseActivity {
    public static final String CURRENT_VIDEO = "CURRENT_VIDEO";
    public static final int YOUTUBE_REQUEST_CODE = 488;
    YoutubeVideo currentVideo;
    EditText et_youtube_about;
    YouTubePlayer ytpv_youtube_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_youtube_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        et_youtube_about = (EditText) findViewById(R.id.et_youtube_about);
        //ytpv_youtube_view = (YouTubePlayer) findViewById(R.id.ytpv_youtube_view);
        try {
            currentVideo = getIntent().getParcelableExtra(CURRENT_VIDEO);
        } catch (Exception x) {
            Log.wtf("currentVideo", x.getMessage());
            x.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_publish:
                publishThisVIdeo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public AddYoutubeDetailActivity() {
    }

    private void publishThisVIdeo() {
     /*   UserContent thisContent = new UserContent();
        thisContent.setComment(et_spotify_about.getText().toString());
        thisContent.setCreatedAt(new Date().getTime());
        thisContent.setLikes(0);
        thisContent.setCatContent("contentSpt");
        SpotifyData spotifyData = new SpotifyData();
        spotifyData.setArtistName(currentSong.getAlbum().getArtistNames());
        spotifyData.setPreviewUrl(currentSong.getPreviewUrl());
        spotifyData.setId(currentSong.getId());
        spotifyData.setName(currentSong.getName());
        spotifyData.setThumb(currentSong.getAlbum().getHigherImage());
        thisContent.setSpotifyData(spotifyData);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("content").child(String.valueOf(loggedUser.getId()));
        String key = ref.push().getKey();
        ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        });*/
    }
}
