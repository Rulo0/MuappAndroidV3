package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.StorageReference;

import me.muapp.android.Classes.Giphy.Data.GiphyEntry;
import me.muapp.android.Classes.Giphy.Data.Original;
import me.muapp.android.R;

public class AddGiphyDetailActivity extends BaseActivity {
    public static final String CURRENT_GIPHY = "CURRENT_GIPHY";
    public static final int GIPHY_CODE = 44;
    EditText et_media_comment;
    ImageView img_giphy_detail;
    StorageReference mainReference;
    GiphyEntry currentGiphy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_giphy_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_giphy_detail = (ImageView) findViewById(R.id.img_giphy_detail);
        if ((currentGiphy = getIntent().getParcelableExtra(CURRENT_GIPHY)) != null) {
            Original o = currentGiphy.getImages().getOriginal();
            Glide.with(this).load(currentGiphy.getImages().getPreviewGif().getUrl()).asGif().priority(Priority.IMMEDIATE).placeholder(R.drawable.ic_logo_muapp_no_caption).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(img_giphy_detail);
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

    }

}
