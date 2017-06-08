package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.LikeUserMatchUser;
import me.muapp.android.R;

import static me.muapp.android.UI.Activity.ChatActivity.CONVERSATION_EXTRA;

public class MatchActivity extends BaseActivity implements View.OnClickListener {
    public static final String MATCHING_USER = "MATCHING_USER";
    public static final String MATCHING_CONVERSATION = "MATCHING_CONVERSATION";
    public static final String FROM_MATCH = "FROM_MATCH";
    ImageView img_my_photo, img_your_photo;
    Button btn_ok_match;
    TextView txt_match_description;
    LikeUserMatchUser likeUserMatchUser;
    ConversationItem conversationItem;
    Boolean fromMatch;
    ImageButton btn_conversation_match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        likeUserMatchUser = getIntent().getParcelableExtra(MATCHING_USER);
        conversationItem = getIntent().getParcelableExtra(MATCHING_CONVERSATION);
        if (likeUserMatchUser == null || conversationItem == null)
            finish();
        //Log.wtf("WithConversation", conversationItem.toString());
        fromMatch = getIntent().getBooleanExtra(FROM_MATCH, false);
        img_my_photo = (ImageView) findViewById(R.id.img_my_photo);
        img_your_photo = (ImageView) findViewById(R.id.img_your_photo);
        btn_ok_match = (Button) findViewById(R.id.btn_ok_match);
        btn_conversation_match = (ImageButton) findViewById(R.id.btn_conversation_match);
        txt_match_description = (TextView) findViewById(R.id.txt_match_description);
        txt_match_description.setText(String.format(getString(R.string.format_match_names), likeUserMatchUser.getFirstName()));
        Glide.with(this).load(loggedUser.getPhoto()).placeholder(R.drawable.ic_placeholder_white).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_my_photo);
        Glide.with(this).load(likeUserMatchUser.getPhoto()).placeholder(R.drawable.ic_placeholder_white).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_your_photo);
        btn_ok_match.setOnClickListener(this);
        btn_conversation_match.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle matchBundle = new Bundle();
        matchBundle.putString(Analytics.Match.MATCH_PROPERTY.Type.toString(), fromMatch ? Analytics.Match.MATCH_TYPE.New.toString() : Analytics.Match.MATCH_TYPE.Crush.toString());
        matchBundle.putString(Analytics.Match.MATCH_PROPERTY.Button.toString(), v.getId() == btn_ok_match.getId() ? Analytics.Match.MATCH_BUTTON.Ok.toString() : Analytics.Match.MATCH_BUTTON.Conversation.toString());
        mFirebaseAnalytics.logEvent(Analytics.Match.MATCH_EVENT.Match_Notification.toString(), matchBundle);

        if (v.getId() == btn_ok_match.getId()) {
            finish();
        } else {
            if (conversationItem != null) {
                Intent conversationIntent = new Intent(this, ChatActivity.class);
                conversationIntent.putExtra(CONVERSATION_EXTRA, conversationItem);
                startActivity(conversationIntent);
                finish();
            }
        }
    }
}
