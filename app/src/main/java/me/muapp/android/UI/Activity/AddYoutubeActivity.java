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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.Classes.Youtube.Data.YoutubeResult;
import me.muapp.android.Classes.Youtube.Data.YoutubeVideo;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.YoutubeAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;
import static me.muapp.android.UI.Activity.AddYoutubeDetailActivity.YOUTUBE_REQUEST_CODE;

public class AddYoutubeActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    LinearLayout placeholder_youtube;
    ProgressBar progress_youtube;
    private ProgressUtil progressUtil;
    RecyclerView recycler_youtube;
    YoutubeAdapter ada;
    private final String TAG = "AddYoutubeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_youtube);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ada = new YoutubeAdapter(this);
        placeholder_youtube = (LinearLayout) findViewById(R.id.placeholder_youtube);
        progress_youtube = (ProgressBar) findViewById(R.id.progress_youtube);
        recycler_youtube = (RecyclerView) findViewById(R.id.recycler_youtube);
        recycler_youtube.setLayoutManager(new LinearLayoutManager(this));
        recycler_youtube.setAdapter(ada);
        progressUtil = new ProgressUtil(this, recycler_youtube, progress_youtube);
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
        new YoutubeSearchTask().execute(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private class YoutubeSearchTask extends AsyncTask<String, Void, List<YoutubeVideo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ada.clearItems();
            Utils.animView(placeholder_youtube, false);
            progressUtil.showProgress(true);
        }

        @Override
        protected void onPostExecute(List<YoutubeVideo> videos) {
            super.onPostExecute(videos);
            progressUtil.showProgress(false);
            if (videos.size() > 0) {
                for (YoutubeVideo v : videos) {
                    ada.addVideo(v);
                }
            } else {
                ((TextView) findViewById(R.id.txt_placeholder_youtube)).setText(getString(R.string.lbl_no_results_found));
                Utils.animView(placeholder_youtube, true);
            }
        }

        @Override
        protected List<YoutubeVideo> doInBackground(String... params) {
            List<YoutubeVideo> result = new ArrayList<>();
            try {
                String url = String.format("https://www.googleapis.com/youtube/v3/search?part=snippet&fields=items(id,snippet(title,channelTitle,thumbnails))&order=viewCount&q=%s&type=video&maxResults=25&key=%s", URLEncoder.encode(params[0], "utf-8"), getYoutubeApiKey());
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
                    YoutubeResult queryResult = new Gson().fromJson(responseString, YoutubeResult.class);
                    result = queryResult.getYoutubeVideos();
                }
            } catch (Exception x) {
                Log.d(TAG, x.getMessage());
                x.printStackTrace();
            }
            return result;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case YOUTUBE_REQUEST_CODE:
                    finish();
            }
        }
    }
}
