package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.R;
import me.muapp.android.UI.Adapter.UserPhotos.UserPhotoTouchHelper;
import me.muapp.android.UI.Adapter.UserPhotos.UserPictureAdapter;

public class UserPhotosActivity extends BaseActivity {
    RecyclerView recycler_user_photos;
    UserPictureAdapter adapter;
    public static final int REQUEST_FACEBOOK_ALBUMS = 451;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycler_user_photos = (RecyclerView) findViewById(R.id.recycler_user_photos);
        List<String> userPhotos = new ArrayList<>();
        userPhotos.addAll(loggedUser.getAlbum());

        if (userPhotos.size() < 6)
            for (int i = userPhotos.size(); i < 6; i++) {
                if (i < 3)
                    userPhotos.add(userPhotos.get(0));
                else
                    userPhotos.add("");
            }


        adapter = new UserPictureAdapter(this, userPhotos);
        recycler_user_photos.setLayoutManager(new GridLayoutManager(this, 2));
      /*  ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.recycler_decoration_offset);
        recycler_user_photos.addItemDecoration(itemDecoration);
       */
        recycler_user_photos.setHasFixedSize(true);
        recycler_user_photos.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new UserPhotoTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recycler_user_photos);
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
