package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.UserPhotos.UserPhotoTouchHelper;
import me.muapp.android.UI.Adapter.UserPhotos.UserPictureAdapter;

public class ProfileSettingsActivity extends BaseActivity {
    RecyclerView recycler_user_photos;
    UserPictureAdapter adapter;
    EditText et_user_biography;
    public static final int REQUEST_FACEBOOK_ALBUMS = 451;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        et_user_biography = (EditText) findViewById(R.id.et_user_biography);
        et_user_biography.setText(loggedUser.getDescription()
        );
        recycler_user_photos = (RecyclerView) findViewById(R.id.recycler_user_photos);
        if (loggedUser.getPending()) {
            recycler_user_photos.setVisibility(View.GONE);
        }
        List<String> userPhotos = new ArrayList<>();
        userPhotos.addAll(loggedUser.getAlbum());

        if (userPhotos.size() < 6)
            for (int i = userPhotos.size(); i < 6; i++) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                attempSaveSettings();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attempSaveSettings() {
        String newDescription = et_user_biography.getText().toString();
        if (!newDescription.equals(loggedUser.getDescription())) {
            try {
                loggedUser.setDescription(newDescription);
                saveUser(loggedUser);
                JSONObject descriptionObj = new JSONObject();
                descriptionObj.put("description", newDescription);
                new APIService(this).patchUser(descriptionObj, null);
            } catch (Exception x) {
                x.printStackTrace();
            }
            finish();
        }
    }
}
