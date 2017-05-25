package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Internal.LikeUserMatchUser;
import me.muapp.android.R;

public class MatchActivity extends BaseActivity {
    public static final String MATCHING_USER = "MATCHING_USER";
    public static final String MATCHING_CONVERSATION = "MATCHING_CONVERSATION";
    ImageView img_my_photo, img_your_photo;
    Button btn_ok_match;
    TextView txt_match_description;
    LikeUserMatchUser likeUserMatchUser;
    ConversationItem conversationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        likeUserMatchUser = getIntent().getParcelableExtra(MATCHING_USER);
        conversationItem = getIntent().getParcelableExtra(MATCHING_CONVERSATION);
        if (likeUserMatchUser == null || conversationItem == null)
            finish();
        Log.wtf("WithConversation", conversationItem.toString());
        img_my_photo = (ImageView) findViewById(R.id.img_my_photo);
        img_your_photo = (ImageView) findViewById(R.id.img_your_photo);
        btn_ok_match = (Button) findViewById(R.id.btn_ok_match);
        txt_match_description = (TextView) findViewById(R.id.txt_match_description);
        txt_match_description.setText(String.format(getString(R.string.format_match_names), likeUserMatchUser.getFirstName()));
        Glide.with(this).load(loggedUser.getPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption_white).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_my_photo);
        Glide.with(this).load(likeUserMatchUser.getPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption_white).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_your_photo);
        btn_ok_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
