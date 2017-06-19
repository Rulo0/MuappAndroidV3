package me.muapp.android.UI.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.SpotifyData;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Spotify.Data.Song;
import me.muapp.android.R;

import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;

public class AddSpotifyDetailActivity extends BaseActivity implements MediaPlayer.OnPreparedListener {
    public static final String CURRENT_SONG = "CURRENT_SONG";
    public static final int SPOTIFY_REQUEST_CODE = 488;
    Song currentSong;
    ImageView img_detail_album_blurred, img_detail_album;
    TextView txt_detail_name, txt_detail_artist;
    MediaPlayer mp;
    ImageButton btn_play_detail;
    EditText et_spotify_about;
    ChatReferences chatReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chatReferences = getIntent().getParcelableExtra(CONTENT_FROM_CHAT);
        et_spotify_about = (EditText) findViewById(R.id.et_spotify_about);
        btn_play_detail = (ImageButton) findViewById(R.id.btn_play_detail);
        img_detail_album_blurred = (ImageView) findViewById(R.id.img_detail_album_blurred);
        img_detail_album = (ImageView) findViewById(R.id.img_detail_album);
        txt_detail_name = (TextView) findViewById(R.id.txt_detail_name);
        txt_detail_artist = (TextView) findViewById(R.id.txt_detail_artist);
        mp = new MediaPlayer();
        try {
            currentSong = new Gson().fromJson(getIntent().getStringExtra(CURRENT_SONG), Song.class);
            mp.setDataSource(currentSong.getPreviewUrl());
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
            Glide.with(this).load(currentSong.getAlbum().getHigherImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.IMMEDIATE).centerCrop().into(img_detail_album);
            Glide.with(this).load(currentSong.getAlbum().getHigherImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.IMMEDIATE).bitmapTransform(new CenterCrop(this), new BlurTransformation(this)).into(img_detail_album_blurred);
            txt_detail_name.setText(currentSong.getName());
            txt_detail_artist.setText(currentSong.getAlbum().getArtistNames());
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        btn_play_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    btn_play_detail.setImageDrawable(ContextCompat.getDrawable(AddSpotifyDetailActivity.this, R.drawable.ic_play_circle));
                    mp.pause();
                } else {
                    btn_play_detail.setImageDrawable(ContextCompat.getDrawable(AddSpotifyDetailActivity.this, R.drawable.ic_pause_circle));
                    mp.start();
                }
            }
        });

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
                publishThisSong();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public AddSpotifyDetailActivity() {
    }

    private void publishThisSong() {
        UserContent thisContent = new UserContent();
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
        if (chatReferences == null) {
            Bundle publishBundle = new Bundle();
            if (!TextUtils.isEmpty(et_spotify_about.getText().toString()))
                publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Comment.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Spotify.toString());
            publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Publish.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Spotify.toString());
            mFirebaseAnalytics.logEvent(Analytics.My_Profile_Add.MY_PROFILE_ADD_EVENT.My_Profile_Add_Type.toString(), publishBundle);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(MuappApplication.DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
            String key = ref.push().getKey();
            ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    preferenceHelper.putAddedContentEnabled();
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else {
            DatabaseReference myConversation = FirebaseDatabase.getInstance().getReferenceFromUrl(chatReferences.getMyConversationRef());
            DatabaseReference yourConversation = FirebaseDatabase.getInstance().getReferenceFromUrl(chatReferences.getYourConversationRef());
            Message m = new Message();
            m.setTimeStamp(new Date().getTime());
            m.setSenderId(loggedUser.getId());
            m.setContent(thisContent.getComment());
            m.setAttachment(thisContent);
            myConversation.child("conversation").child(myConversation.push().getKey()).setValue(m);
            yourConversation.child("conversation").child(yourConversation.push().getKey()).setValue(m);
            myConversation.child("lastMessage").setValue(m);
            yourConversation.child("lastMessage").setValue(m);
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
    }
}
