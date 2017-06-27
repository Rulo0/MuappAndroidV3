package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Youtube.Data.YoutubeVideo;
import me.muapp.android.R;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;
import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;

public class AddYoutubeDetailActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String CURRENT_VIDEO = "CURRENT_VIDEO";
    public static final int YOUTUBE_REQUEST_CODE = 511;
    YoutubeVideo currentVideo;
    EditText et_youtube_about;
    YouTubePlayerFragment youtube_fragment;
    ChatReferences chatReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_add_youtube_detail);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            et_youtube_about = (EditText) findViewById(R.id.et_youtube_about);
            youtube_fragment = (YouTubePlayerFragment) getFragmentManager()
                    .findFragmentById(R.id.youtube_fragment);
            youtube_fragment.initialize(getYoutubeApiKey(), this);
            currentVideo = getIntent().getParcelableExtra(CURRENT_VIDEO);
            chatReferences = getIntent().getParcelableExtra(CONTENT_FROM_CHAT);
        } catch (Exception x) {
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
        Bundle publishBundle = new Bundle();
        if (!TextUtils.isEmpty(et_youtube_about.getText().toString()))
            publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Comment.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Youtube.toString());
        publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Publish.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Youtube.toString());
        mFirebaseAnalytics.logEvent(Analytics.My_Profile_Add.MY_PROFILE_ADD_EVENT.My_Profile_Add_Type.toString(), publishBundle);
        UserContent thisContent = new UserContent();
        thisContent.setComment(et_youtube_about.getText().toString());
        thisContent.setCreatedAt(new Date().getTime());
        thisContent.setLikes(0);
        thisContent.setVideoTitle(currentVideo.getSnippet().getTitle());
        thisContent.setCatContent("contentYtv");
        thisContent.setThumbUrl(currentVideo.getSnippet().getThumbnails().getHigh().getUrl());
        thisContent.setVideoId(currentVideo.getId().getVideoId());
        if (chatReferences == null) {
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
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        YouTubePlayer.PlayerStyle style = YouTubePlayer.PlayerStyle.MINIMAL;
        youTubePlayer.setPlayerStyle(style);
        if (!wasRestored) {
            youTubePlayer.cueVideo(currentVideo.getId().getVideoId());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
