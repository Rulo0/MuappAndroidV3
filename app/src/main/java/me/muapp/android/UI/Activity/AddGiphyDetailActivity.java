package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Giphy.Data.GiphyEntry;
import me.muapp.android.Classes.Internal.GiphyMeasureData;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;


public class AddGiphyDetailActivity extends BaseActivity {
    public static final String CURRENT_GIPHY = "CURRENT_GIPHY";
    public static final int GIPHY_CODE = 44;
    ChatReferences chatReferences;
    EditText et_giphy_comment;
    ImageView img_giphy_detail;
    StorageReference mainReference;
    GiphyEntry currentGiphy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_giphy_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chatReferences = getIntent().getParcelableExtra(CONTENT_FROM_CHAT);
        img_giphy_detail = (ImageView) findViewById(R.id.img_giphy_detail);
        et_giphy_comment = (EditText) findViewById(R.id.et_giphy_comment);
        if ((currentGiphy = getIntent().getParcelableExtra(CURRENT_GIPHY)) != null) {
            Glide.with(this).load(currentGiphy.getImages().getOriginal().getUrl()).asGif().priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.ic_placeholder).fitCenter().dontAnimate().into(img_giphy_detail);
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
                publishThisMedia();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishThisMedia() {
        showProgressDialog();
        final UserContent thisContent = new UserContent();
        thisContent.setComment(et_giphy_comment.getText().toString());
        thisContent.setCreatedAt(new Date().getTime());
        thisContent.setLikes(0);
        thisContent.setContentUrl(currentGiphy.getImages().getOriginal().getUrl().replace("http://", "https://"));
        thisContent.setCatContent("contentGif");
        GiphyMeasureData giphyMeasureData = new GiphyMeasureData();
        giphyMeasureData.setWidth(Integer.parseInt(currentGiphy.getImages().getOriginal().getWidth()));
        giphyMeasureData.setHeight(Integer.parseInt(currentGiphy.getImages().getOriginal().getHeight()));
        thisContent.setGiphyMeasureData(giphyMeasureData);

        if (chatReferences == null) {
            Bundle publishBundle = new Bundle();
            if (!TextUtils.isEmpty(et_giphy_comment.getText().toString()))
                publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Comment.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Gif.toString());
            publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Publish.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Gif.toString());
            mFirebaseAnalytics.logEvent(Analytics.My_Profile_Add.MY_PROFILE_ADD_EVENT.My_Profile_Add_Type.toString(), publishBundle);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(MuappApplication.DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
            String key = ref.push().getKey();
            ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    preferenceHelper.putAddedContentEnabled();
                    hideProgressDialog();
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
            hideProgressDialog();
            setResult(RESULT_OK);
            finish();
/*
            Message m = new Message();
            m.setTimeStamp(new Date().getTime());
            m.setSenderId(loggedUser.getId());
            m.setContent(etMessage.getText().toString());
            m.setReaded(false);
            if (content != null)
                m.setAttachment(content);
            etMessage.setText("");
            conversationReference.child(conversationReference.push().getKey()).setValue(m);
            yourConversation.child("conversation").child(yourConversation.push().getKey()).setValue(m);
            myConversation.child("lastMessage").setValue(m);
            yourConversation.child("lastMessage").setValue(m);
*/
        }
    }
}
