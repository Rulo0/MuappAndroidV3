package me.muapp.android.UI.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import me.muapp.android.Classes.Util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Giphy.Data.GiphyEntries;
import me.muapp.android.Classes.Giphy.Data.GiphyEntry;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.AddGiphyAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static me.muapp.android.UI.Activity.AddGiphyDetailActivity.GIPHY_CODE;
import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;

public class AddGiphyActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    RecyclerView recycler_add_giphy;
    ProgressBar progress_giphy;
    AddGiphyAdapter ada;
    ProgressUtil progressUtil;
    LinearLayout placeholder_giphy;
    ChatReferences chatReferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_giphy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chatReferences = getIntent().getParcelableExtra(CONTENT_FROM_CHAT);
        ada = new AddGiphyAdapter(this);
        if (chatReferences != null)
            ada.setChatReferences(chatReferences);
        ada.setActivity(this);
        progress_giphy = (ProgressBar) findViewById(R.id.progress_giphy);
        recycler_add_giphy = (RecyclerView) findViewById(R.id.recycler_add_giphy);
        progressUtil = new ProgressUtil(this, recycler_add_giphy, progress_giphy);
        recycler_add_giphy.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycler_add_giphy.setAdapter(ada);
        placeholder_giphy = (LinearLayout) findViewById(R.id.include_placeholder_giphy);
        new GetGyphyTask().execute();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GIPHY_CODE:
                    finish();
                    break;
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            new SearchGyphyTask().execute(query);
        }
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private class SearchGyphyTask extends AsyncTask<String, Void, List<GiphyEntry>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressUtil.showProgress(true);
            Utils.animViewFade(placeholder_giphy, false);
        }

        @Override
        protected void onPostExecute(List<GiphyEntry> result) {
            super.onPostExecute(result);
            progressUtil.showProgress(false);
            if (result.size() > 0) {
                ada.addPhotos(result);
                Utils.animViewFade(recycler_add_giphy, true);
            } else {
                Utils.animViewFade(placeholder_giphy, true);
                Utils.animViewFade(recycler_add_giphy, false);
            }
        }

        @Override
        protected List<GiphyEntry> doInBackground(String... params) {
            List<GiphyEntry> result = new ArrayList<>();
            try {
                Log.wtf("Searching", String.format("http://api.giphy.com/v1/gifs/search?q=%s&api_key=3o6ZthRhJhfgXMOmpa&limit=100&lang=%s", URLEncoder.encode(params[0], "utf-8"), Locale.getDefault().getLanguage()));
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format("http://api.giphy.com/v1/gifs/search?q=%s&api_key=3o6ZthRhJhfgXMOmpa&limit=100&lang=%s", URLEncoder.encode(params[0], "utf-8"), Locale.getDefault().getLanguage()))
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    GiphyEntries entries = new Gson().fromJson(response.body().string(), GiphyEntries.class);
                    result = entries.getGiphyEntries();
                }
            } catch (Exception x) {

            }
            return result;
        }

    }

    private class GetGyphyTask extends AsyncTask<Void, Void, List<GiphyEntry>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressUtil.showProgress(true);
        }

        @Override
        protected void onPostExecute(List<GiphyEntry> result) {
            super.onPostExecute(result);
            progressUtil.showProgress(false);
            if (result.size() > 0) {
                ada.addPhotos(result);
            } else {

            }
        }

        @Override
        protected List<GiphyEntry> doInBackground(Void... params) {
            List<GiphyEntry> result = new ArrayList<>();
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.giphy.com/v1/gifs/trending?api_key=3o6ZthRhJhfgXMOmpa&limit=100")
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    GiphyEntries entries = new Gson().fromJson(response.body().string(), GiphyEntries.class);
                    result = entries.getGiphyEntries();
                }
            } catch (Exception x) {

            }
            return result;
        }
    }
}
