package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Spotify.Data.Song;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.AddSpotifyActivity;
import me.muapp.android.UI.Activity.AddSpotifyDetailActivity;

import static me.muapp.android.UI.Activity.AddSpotifyDetailActivity.CURRENT_SONG;
import static me.muapp.android.UI.Activity.AddSpotifyDetailActivity.SPOTIFY_REQUEST_CODE;
import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;

/**
 * Created by rulo on 28/03/17.
 */

public class SpotifyAdapter extends RecyclerView.Adapter<SpotifyAdapter.SongViewHolder> {
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private final LayoutInflater mInflater;
    private List<Song> songs;
    private Context mContext;
    private String userFBToken;
    ChatReferences chatReferences;

    public void setChatReferences(ChatReferences chatReferences) {
        this.chatReferences = chatReferences;
    }

    public SpotifyAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.songs = new ArrayList<>();
        this.mContext = context;
        this.userFBToken = new PreferenceHelper(context).getFacebookToken();
    }


    public void clearItems() {
        songs = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addSong(Song song) {
        this.songs.add(songs.size(), song);
        notifyItemInserted(songs.size());
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.spotify_item_layout, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView img_spotify;
        TextView txt_spotify_title, txt_spotify_artist;
        View container_spotify;

        public SongViewHolder(View itemView) {
            super(itemView);
            this.container_spotify = itemView.findViewById(R.id.container_spotify);
            this.img_spotify = (ImageView) itemView.findViewById(R.id.img_spotify);
            this.txt_spotify_title = (TextView) itemView.findViewById(R.id.txt_spotify_title);
            this.txt_spotify_artist = (TextView) itemView.findViewById(R.id.txt_spotify_artist);
        }

        public void bind(final Song song) {
            Glide.with(mContext).load(song.getAlbum().getHigherImage()).centerCrop().placeholder(R.drawable.ic_spotify).into(img_spotify);
            txt_spotify_title.setText(song.getName());
            txt_spotify_artist.setText(song.getAlbum().getArtistNames());
            container_spotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent spotifyIntent = new Intent(mContext, AddSpotifyDetailActivity.class);
                    spotifyIntent.putExtra(CURRENT_SONG, new Gson().toJson(song));
                    if (chatReferences != null)
                        spotifyIntent.putExtra(CONTENT_FROM_CHAT, chatReferences);
                    ((AddSpotifyActivity) mContext).startActivityForResult(spotifyIntent, SPOTIFY_REQUEST_CODE);
                }
            });
        }
    }
}
