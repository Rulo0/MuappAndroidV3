package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import me.muapp.android.R;

public class SpotifySearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    SearchView srch_spotify_tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        srch_spotify_tracks = (SearchView) findViewById(R.id.srch_spotify_tracks);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
