package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.muapp.android.R;

public class FacebookPhotoDetailActivity extends AppCompatActivity {
    public static final String PHOTO_URL = "photo_url";
    ImageView img_photo_detail;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_photo_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_photo_detail = (ImageView) findViewById(R.id.img_photo_detail);
        if (getIntent().hasExtra(PHOTO_URL)) {
            photoUrl = getIntent().getStringExtra(PHOTO_URL);
            if (!TextUtils.isEmpty(photoUrl)) {
                Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().fitCenter().into(img_photo_detail);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save_photo:
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                returnIntent.putExtra(PHOTO_URL, photoUrl);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
