package me.muapp.android.UI.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Giphy.Data.GiphyEntries;
import me.muapp.android.Classes.Giphy.Data.GiphyEntry;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.AddGiphyAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddGiphyActivity extends BaseActivity {
    RecyclerView recycler_add_giphy;
    AddGiphyAdapter ada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_giphy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ada = new AddGiphyAdapter(this);
        ada.setActivity(this);
        recycler_add_giphy = (RecyclerView) findViewById(R.id.recycler_add_giphy);
        recycler_add_giphy.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recycler_add_giphy.setAdapter(ada);
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

    private class GetGyphyTask extends AsyncTask<Void, Void, List<GiphyEntry>> {
        @Override
        protected void onPostExecute(List<GiphyEntry> result) {
            super.onPostExecute(result);
            ada.addPhotos(result);
        }

        @Override
        protected List<GiphyEntry> doInBackground(Void... params) {
            List<GiphyEntry> result = new ArrayList<>();
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.giphy.com/v1/gifs/trending?api_key=dc6zaTOxFJmzC&limit=100")
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
