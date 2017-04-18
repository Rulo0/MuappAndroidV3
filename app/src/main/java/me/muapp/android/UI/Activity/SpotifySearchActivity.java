package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import static me.muapp.android.UI.Activity.SpotifyDetailActivity.SPOTIFY_REQUEST_CODE;

public class SpotifySearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    public static String TAG = "SpotifySearchActivity";
    SearchView srch_spotify_tracks;
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
        srch_spotify_tracks = (SearchView) findViewById(R.id.srch_spotify_tracks);
        placeholder_spotify = (LinearLayout) findViewById(R.id.placeholder_spotify);
        progress_spotify = (ProgressBar) findViewById(R.id.progress_spotify);
        srch_spotify_tracks.setOnQueryTextListener(this);
        recycler_spotify = (RecyclerView) findViewById(R.id.recycler_spotify);
        recycler_spotify.setLayoutManager(new LinearLayoutManager(this));
        recycler_spotify.setAdapter(ada);
        progressUtil = new ProgressUtil(this, recycler_spotify, progress_spotify);
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
            for (Song s : songs) {
                ada.addSong(s);
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
                    Toast.makeText(this, "Published", Toast.LENGTH_SHORT).show();
            }
        }
    }
}