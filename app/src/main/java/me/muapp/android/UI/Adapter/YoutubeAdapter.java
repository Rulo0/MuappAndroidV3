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

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Youtube.Data.YoutubeVideo;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.AddYoutubeActivity;
import me.muapp.android.UI.Activity.AddYoutubeDetailActivity;

import static me.muapp.android.UI.Activity.AddYoutubeDetailActivity.CURRENT_VIDEO;
import static me.muapp.android.UI.Activity.AddYoutubeDetailActivity.YOUTUBE_REQUEST_CODE;
import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;

/**
 * Created by rulo on 28/03/17.
 */

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.SongViewHolder> {
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private final LayoutInflater mInflater;
    private List<YoutubeVideo> videos;
    private Context mContext;
    private String userFBToken;
    ChatReferences chatReferences;

    public void setChatReferences(ChatReferences chatReferences) {
        this.chatReferences = chatReferences;
    }

    public YoutubeAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.videos = new ArrayList<>();
        this.mContext = context;
        this.userFBToken = new PreferenceHelper(context).getFacebookToken();
    }


    public void clearItems() {
        videos = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addVideo(YoutubeVideo video) {
        this.videos.add(videos.size(), video);
        notifyItemInserted(videos.size());
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.spotify_item_layout, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.bind(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView img_spotify;
        TextView txt_spotify_title, txt_spotify_artist;
        View container_spotify;
        YoutubeVideo currentVideo;

        public SongViewHolder(View itemView) {
            super(itemView);
            this.container_spotify = itemView.findViewById(R.id.container_spotify);
            this.img_spotify = (ImageView) itemView.findViewById(R.id.img_spotify);
            this.txt_spotify_title = (TextView) itemView.findViewById(R.id.txt_spotify_title);
            this.txt_spotify_artist = (TextView) itemView.findViewById(R.id.txt_spotify_artist);
        }

        public void bind(final YoutubeVideo video) {
            currentVideo = video;
            Glide.with(mContext).load(video.getSnippet().getThumbnails().getDefault().getUrl()).centerCrop().placeholder(R.drawable.ic_spotify).into(img_spotify);
            txt_spotify_title.setText(video.getSnippet().getTitle());
            txt_spotify_artist.setText(video.getSnippet().getChannelTitle());
            container_spotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtubeIntent = new Intent(mContext, AddYoutubeDetailActivity.class);
                    youtubeIntent.putExtra(CURRENT_VIDEO, video);
                    if (chatReferences != null)
                        youtubeIntent.putExtra(CONTENT_FROM_CHAT, chatReferences);
                    ((AddYoutubeActivity) mContext).startActivityForResult(youtubeIntent, YOUTUBE_REQUEST_CODE);
                }
            });
        }
    }
}
