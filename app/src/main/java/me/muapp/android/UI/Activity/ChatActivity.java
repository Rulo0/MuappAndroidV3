package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.R;

public class ChatActivity extends AppCompatActivity {
    public static final String DIALOG_EXTRA = "DIALOG_EXTRA";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        if (getIntent().hasExtra(DIALOG_EXTRA))
            setupLayout((DialogCacheObject) getIntent().getParcelableExtra(DIALOG_EXTRA));
        else
            finish();
    }

    public void setupLayout(final DialogCacheObject dialog) {
        ImageView toolbar_opponent_photo = (ImageView) findViewById(R.id.toolbar_opponent_photo);
        Glide.with(this).load(dialog.getOpponentPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption).bitmapTransform(new CropCircleTransformation(this)).into(toolbar_opponent_photo);
        toolbar_opponent_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, dialog.getOpponentName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
