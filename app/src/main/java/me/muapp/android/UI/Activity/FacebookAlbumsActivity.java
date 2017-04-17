package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import me.muapp.android.Classes.Internal.FacebookAlbum;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.UserFBAlbumsAdapter;

import static me.muapp.android.UI.Activity.FacebookPhotoDetailActivity.PHOTO_URL;

public class FacebookAlbumsActivity extends BaseActivity {
    List<FacebookAlbum> albums;
    String TAG = "FacebookAlbumsActivity";
    public static final int FACEBOOK_PHOTOS_REQUEST_CODE = 754;
    RecyclerView data_container;
    UserFBAlbumsAdapter ada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_album);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ada = new UserFBAlbumsAdapter(this);
        data_container = (RecyclerView) findViewById(R.id.data_container);
        data_container.setLayoutManager(new LinearLayoutManager(this));
        data_container.setAdapter(ada);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), String.format(
                "/%s/albums", new PreferenceHelper(this).getFacebookId()),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject graph = response.getJSONObject();
                            if (graph.has("data") && !graph.isNull("data")) {
                                JSONArray array = graph.getJSONArray("data");
                                albums = FacebookAlbum.asList(array);
                                for (final FacebookAlbum a : albums) {
                                    new GraphRequest(
                                            AccessToken.getCurrentAccessToken(), String.format("/%s/photos", a.getId()),
                                            null,
                                            HttpMethod.GET,
                                            new GraphRequest.Callback() {
                                                public void onCompleted(GraphResponse response) {
                                                    try {
                                                        Log.wtf("ALBUMS", response.toString());
                                                        JSONObject albumGraph = response.getJSONObject();
                                                        List<String> photos;
                                                        if (albumGraph.has("data") && !albumGraph.isNull("data")) {
                                                            JSONArray array = albumGraph.getJSONArray("data");
                                                            photos = FacebookAlbum.asAlbumList(array);
                                                            a.setPhotosId(photos);
                                                            a.setFirstPhotoId(photos.get(0));
                                                            ada.addAlbum(a);
                                                            data_container.scrollToPosition(0);
                                                        }
                                                    } catch (Exception x) {
                                                        x.printStackTrace();
                                                    }
                                                }
                                            }
                                    ).executeAsync();
                                }
                            }
                        } catch (Exception x) {
                            Log.e(TAG, x.toString());
                            x.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent returnIntent = new Intent();
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case FACEBOOK_PHOTOS_REQUEST_CODE:
                    if (data.hasExtra(PHOTO_URL))
                        setResult(data.hasExtra(PHOTO_URL) ? RESULT_OK : RESULT_CANCELED, returnIntent);
                    returnIntent.putExtra(PHOTO_URL, data.getStringExtra(PHOTO_URL));
                    finish();
                    break;
            }
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
