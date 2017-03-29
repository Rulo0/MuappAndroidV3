package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import me.muapp.android.Classes.Internal.FacebookAlbum;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.UserFBPhotosAdapter;

import static me.muapp.android.UI.Activity.FacebookPhothoDetailActivity.PHOTO_URL;

public class FacebookPhotosActivity extends BaseActivity {
    public static final String ALBUM_EXTRA = "ALBUM_EXTRA";
    public static final int FACEBOOK_PHOTO_REQUEST_CODE = 755;
    FacebookAlbum currentAlbum;
    RecyclerView data_container;
    UserFBPhotosAdapter ada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_photos);
        data_container = (RecyclerView) findViewById(R.id.data_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra(ALBUM_EXTRA)) {
            currentAlbum = getIntent().getParcelableExtra(ALBUM_EXTRA);
            if (currentAlbum != null) {
                ada = new UserFBPhotosAdapter(this, currentAlbum.getPhotosId());
                getSupportActionBar().setTitle(currentAlbum.getName());
                setupViews();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent returnIntent = new Intent();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FACEBOOK_PHOTO_REQUEST_CODE:
                    if (data.hasExtra(PHOTO_URL))
                        setResult(data.hasExtra(PHOTO_URL) ? RESULT_OK : RESULT_CANCELED, returnIntent);
                    returnIntent.putExtra(PHOTO_URL, data.getStringExtra(PHOTO_URL));
                    finish();
                    break;
            }
        }
    }

    private void setupViews() {
        data_container.setLayoutManager(new GridLayoutManager(this, 3));
        data_container.setAdapter(ada);
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
}
