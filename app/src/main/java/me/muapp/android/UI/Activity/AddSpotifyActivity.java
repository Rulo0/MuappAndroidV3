package me.muapp.android.UI.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Spotify.Data.Song;
import me.muapp.android.Classes.Spotify.Data.SpotifyResult;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.SpotifyAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.muapp.android.UI.Activity.AddSpotifyDetailActivity.SPOTIFY_REQUEST_CODE;

public class AddSpotifyActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    public static String TAG = "AddSpotifyActivity";
    /*SearchView srch_spotify_tracks;*/
    RecyclerView recycler_spotify;
    LinearLayout placeholder_spotify;
    ProgressBar progress_spotify;
    SpotifyAdapter ada;
    ProgressUtil progressUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ada = new SpotifyAdapter(this);
        setContentView(R.layout.activity_spotify_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //srch_spotify_tracks = (SearchView) findViewById(srch_spotify_tracks);
        placeholder_spotify = (LinearLayout) findViewById(R.id.placeholder_spotify);
        progress_spotify = (ProgressBar) findViewById(R.id.progress_spotify);
        //srch_spotify_tracks.setOnQueryTextListener(this);
        recycler_spotify = (RecyclerView) findViewById(R.id.recycler_spotify);
        recycler_spotify.setLayoutManager(new LinearLayoutManager(this));
        recycler_spotify.setAdapter(ada);
        progressUtil = new ProgressUtil(this, recycler_spotify, progress_spotify);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchInSpotify(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void searchInSpotify(String query) {
        Utils.animView(placeholder_spotify, false);
        new SpotifySearchTask().execute(query);
    }

    private class SpotifySearchTask extends AsyncTask<String, Void, List<Song>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ada.clearItems();
            progressUtil.showProgress(true);
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            progressUtil.showProgress(false);
            if (songs.size() > 0) {
                for (Song s : songs) {
                    ada.addSong(s);
                }
            } else {
                ((TextView) findViewById(R.id.txt_placeholder_spotify)).setText(getString(R.string.lbl_no_results_found));
                Utils.animView(placeholder_spotify, true);
            }
        }

        @Override
        protected List<Song> doInBackground(String... params) {
            List<Song> songs = new ArrayList<>();
            try {
                String url = String.format("https://api.spotify.com/v1/search?query=%s&type=track&limit=50", URLEncoder.encode(params[0], "utf-8"));
                Log.wtf(TAG, url);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.wtf(TAG, responseString);
                    JSONObject result = new JSONObject(responseString);
                    if (result.has("tracks")) {
                        SpotifyResult spotifyResult = new Gson().fromJson(result.getJSONObject("tracks").toString(), SpotifyResult.class);
                        if (spotifyResult != null) {
                            songs = spotifyResult.getSongs();
                        }
                    }
                }
            } catch (Exception x) {
                Log.d(TAG, x.getMessage());
                x.printStackTrace();
            }
            return songs;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf(TAG, requestCode + " " + requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SPOTIFY_REQUEST_CODE:
                    finish();
                    break;
            }
        }
    }
}
