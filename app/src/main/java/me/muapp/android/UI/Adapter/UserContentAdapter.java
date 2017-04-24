package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.muapp.android.Classes.Internal.GiphyMeasureData;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.SpotifyData;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.YoutubeViewActivity;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;

/**
 * Created by rulo on 18/04/17.
 */
public class UserContentAdapter extends RecyclerView.Adapter<UserContentAdapter.UserContentHolder> {
    SortedList<UserContent> userContentList;
    HashMap<String, Integer> viewTypeMap = new HashMap<String, Integer>() {{
        put("contentAud", 1);
        put("contentCmt", 2);
        put("contentGif", 3);
        put("contentPic", 4);
        put("contentQte", 5);
        put("contentSpt", 6);
        put("contentVid", 7);
        put("contentYtv", 8);
    }};
    Context context;
    LayoutInflater mInflater;
    MediaPlayer mediaPlayer;
    String currentPlaying = "";
    ImageButton previewPlayedButton;
    int screenWidth;
    List<MuappQuote> quoteList;
    String lang;
    FragmentTransaction fragmentTransaction;

    public void setFragmentTransaction(FragmentTransaction fragmentTransaction) {
        this.fragmentTransaction = fragmentTransaction;
    }

    public UserContentAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.userContentList = new SortedList<>(UserContent.class, new SortedList.Callback<UserContent>() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public int compare(UserContent o1, UserContent o2) {
                return new Date(o2.getCreatedAt()).compareTo(new Date(o1.getCreatedAt()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(UserContent oldItem, UserContent newItem) {
                return newItem.toString().equals(oldItem.toString());
            }

            @Override
            public boolean areItemsTheSame(UserContent item1, UserContent item2) {
                return item1.getKey().equals(item2.getKey());
            }
        });
        this.context = context;
        this.quoteList = new ArrayList<>();
        this.lang = Locale.getDefault().getLanguage();
        mediaPlayer = new MediaPlayer();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    public void addContent(UserContent c) {
        userContentList.add(c);
    }

    public void setQuoteList(List<MuappQuote> quoteList) {
        this.quoteList = quoteList;
        notifyDataSetChanged();
    }

    public void removeContent(String contentKey) {
        for (int i = 0; i < userContentList.size(); i++) {
            if (userContentList.get(i).getKey().equals(contentKey)) {
                userContentList.removeItemAt(i);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypeMap.get(userContentList.get(position).getCatContent());
    }

    @Override
    public UserContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.wtf("ViewType", viewType + "");
        UserContentHolder holder;
        switch (viewType) {
            case 1:
                View voiceView = mInflater.inflate(R.layout.user_content_audio_item_layout, parent, false);
                holder = new AudioContentHolder(voiceView);
                break;
            case 3:
                View gifView = mInflater.inflate(R.layout.user_content_gif_item_layout, parent, false);
                holder = new GifContentHolder(gifView);
                break;
            case 5:
                View quoteView = mInflater.inflate(R.layout.user_content_quote_item_layout, parent, false);
                holder = new QuoteContentHolder(quoteView);
                break;
            case 6:
                View spotifyView = mInflater.inflate(R.layout.user_content_spotify_item_layout, parent, false);
                holder = new SpotifyContentHolder(spotifyView);
                break;
            case 8:
                View youtubeView = mInflater.inflate(R.layout.user_content_youtube_item_layout, parent, false);
                holder = new YoutubeContentHolder(youtubeView);
                break;
            default:
                View picView = mInflater.inflate(R.layout.user_content_picture_item_layout, parent, false);
                holder = new PictureContentHolder(picView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(UserContentHolder holder, int position) {
        holder.bind(userContentList.get(position));
    }

    @Override
    public int getItemCount() {
        return userContentList.size();
    }

    interface UserContentInterface {
        void bind(UserContent c);
    }

    class UserContentHolder extends RecyclerView.ViewHolder implements UserContentInterface, View.OnClickListener {
        public UserContentHolder(View itemView) {
            super(itemView);
        }

        UserContent itemContent;

        @Override
        public void bind(UserContent c) {
            itemContent = c;
        }

        @Override
        public void onClick(View v) {

        }
    }

    class PictureContentHolder extends UserContentHolder {
        TextView txt_image_comment;
        RelativeTimeTextView txt_picture_date;
        ImageView img_picture_content;


        public PictureContentHolder(View itemView) {
            super(itemView);
            this.txt_image_comment = (TextView) itemView.findViewById(R.id.txt_image_comment);
            this.txt_picture_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_picture_date);
            this.img_picture_content = (ImageView) itemView.findViewById(R.id.img_picture_content);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            Glide.with(context).load(c.getContentUrl()).placeholder(R.drawable.ic_logo_muapp_no_caption).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_picture_content);
            if (!TextUtils.isEmpty(c.getComment())) {
                txt_image_comment.setText(c.getComment());
                txt_image_comment.setVisibility(View.VISIBLE);
            } else {
                txt_image_comment.setVisibility(View.GONE);
            }
            txt_picture_date.setReferenceTime(c.getCreatedAt());
        }
    }

    class GifContentHolder extends UserContentHolder {
        TextView txt_gif_comment;
        RelativeTimeTextView txt_gif_date;
        ImageView img_gif_content;
        View contentView;

        public GifContentHolder(View itemView) {
            super(itemView);
            this.contentView = itemView;
            this.txt_gif_comment = (TextView) itemView.findViewById(R.id.txt_gif_comment);
            this.txt_gif_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_gif_date);
            this.img_gif_content = (ImageView) itemView.findViewById(R.id.img_gif_content);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            if (!TextUtils.isEmpty(c.getComment())) {
                txt_gif_comment.setText(c.getComment());
                txt_gif_comment.setVisibility(View.VISIBLE);
            } else {
                txt_gif_comment.setVisibility(View.GONE);
            }
            txt_gif_date.setReferenceTime(c.getCreatedAt());

            GiphyMeasureData giphyMeasureData = c.getGiphyMeasureData();
            float aspectRatio;
            if (giphyMeasureData.getHeight() >= giphyMeasureData.getWidth()) {
                aspectRatio = (float) giphyMeasureData.getHeight() / (float) giphyMeasureData.getWidth();
                Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_logo_muapp_no_caption).priority(Priority.IMMEDIATE).diskCacheStrategy(DiskCacheStrategy.SOURCE).override((int) (screenWidth * aspectRatio), screenWidth).into(img_gif_content);
            } else {
                aspectRatio = (float) giphyMeasureData.getWidth() / (float) giphyMeasureData.getHeight();
                Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_logo_muapp_no_caption).priority(Priority.IMMEDIATE).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(screenWidth, (int) (screenWidth * aspectRatio)).into(img_gif_content);
            }
        }
    }

    class SpotifyContentHolder extends UserContentHolder {
        TextView txt_spotify_comment;
        RelativeTimeTextView txt_spotify_date;
        ImageView img_detail_album_blurred, img_detail_album;
        TextView txt_detail_name, txt_detail_artist;
        ImageButton btn_play_detail;
        SpotifyData currentData;

        public SpotifyContentHolder(View itemView) {
            super(itemView);
            this.txt_spotify_comment = (TextView) itemView.findViewById(R.id.txt_spotify_comment);
            this.txt_spotify_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_spotify_date);
            this.btn_play_detail = (ImageButton) itemView.findViewById(R.id.btn_play_detail);
            this.img_detail_album_blurred = (ImageView) itemView.findViewById(R.id.img_detail_album_blurred);
            this.img_detail_album = (ImageView) itemView.findViewById(R.id.img_detail_album);
            this.txt_detail_name = (TextView) itemView.findViewById(R.id.txt_detail_name);
            this.txt_detail_artist = (TextView) itemView.findViewById(R.id.txt_detail_artist);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            if (!TextUtils.isEmpty(c.getComment())) {
                txt_spotify_comment.setText(c.getComment());
                txt_spotify_comment.setVisibility(View.VISIBLE);
            } else {
                txt_spotify_comment.setVisibility(View.GONE);
            }
            txt_spotify_date.setReferenceTime(c.getCreatedAt());
            if ((currentData = c.getSpotifyData()) != null) {
                Glide.with(context).load(currentData.getThumb()).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.IMMEDIATE).centerCrop().into(img_detail_album);
                Glide.with(context).load(currentData.getThumb()).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.IMMEDIATE).bitmapTransform(new CenterCrop(context), new BlurTransformation(context)).into(img_detail_album_blurred);
                txt_detail_name.setText(currentData.getName());
                txt_detail_artist.setText(currentData.getArtistName());
                if (currentPlaying.equals(currentData.getPreviewUrl())) {
                    btn_play_detail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_circle));
                } else {
                    btn_play_detail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                }
                btn_play_detail.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            try {
                if (!currentPlaying.equals(currentData.getPreviewUrl())) {
                    if (previewPlayedButton != null) {
                        previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, R.drawable.ic_content_play) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentPlaying = currentData.getPreviewUrl());
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            btn_play_detail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_circle));
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currentPlaying = "";
                            btn_play_detail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                        }
                    });
                    mediaPlayer.prepareAsync();
                    previewPlayedButton = (ImageButton) v;
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        btn_play_detail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                    } else {
                        mediaPlayer.start();
                        btn_play_detail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause_circle));
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    class YoutubeContentHolder extends UserContentHolder {
        TextView txt_youtube_comment;
        RelativeTimeTextView txt_youtube_date;
        View contentView;
        String youtubeVideoId;
        YouTubeThumbnailView youtube_thumbnail;

        public YoutubeContentHolder(View itemView) {
            super(itemView);
            this.contentView = itemView;
            this.youtube_thumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
            this.txt_youtube_comment = (TextView) itemView.findViewById(R.id.txt_youtube_comment);
            this.txt_youtube_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_youtube_date);
        }

        @Override
        public void bind(final UserContent c) {
            super.bind(c);
            this.youtubeVideoId = c.getVideoId();
            if (!TextUtils.isEmpty(c.getComment())) {
                txt_youtube_comment.setText(c.getComment());
                txt_youtube_comment.setVisibility(View.VISIBLE);
            } else {
                txt_youtube_comment.setVisibility(View.GONE);
            }
            txt_youtube_date.setReferenceTime(c.getCreatedAt());
            youtube_thumbnail.initialize(getYoutubeApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(c.getVideoId());
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            youtube_thumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent youtubeIntent = new Intent(context, YoutubeViewActivity.class);
            youtubeIntent.putExtra("itemContent", itemContent);
            context.startActivity(youtubeIntent);
        }
    }

    class QuoteContentHolder extends UserContentHolder {
        TextView txt_quote_comment;
        TextView txt_quote_prefix;
        RelativeTimeTextView txt_quote_date;


        public QuoteContentHolder(View itemView) {
            super(itemView);
            this.txt_quote_comment = (TextView) itemView.findViewById(R.id.txt_quote_comment);
            txt_quote_prefix = (TextView) itemView.findViewById(R.id.txt_quote_prefix);
            this.txt_quote_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_quote_date);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            txt_quote_prefix.setText(getQuote(c.getQuoteId()));
            txt_quote_comment.setText(c.getComment());
            txt_quote_date.setReferenceTime(c.getCreatedAt());
        }

        private String getQuote(String quoteId) {
            if (quoteList != null) {
                for (MuappQuote q : quoteList) {
                    if (q.getKey().equals(quoteId))
                        return lang.equals("es") ? q.getCaptionSpa() : q.getCaptionEng();
                }
            }
            return "";
        }
    }

    class AudioContentHolder extends UserContentHolder {
        TextView txt_audio_comment;
        RelativeTimeTextView txt_audio_date;
        ImageButton btn_audio_content;

        public AudioContentHolder(View itemView) {
            super(itemView);
            this.txt_audio_comment = (TextView) itemView.findViewById(R.id.txt_audio_comment);
            this.txt_audio_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_audio_date);
            this.btn_audio_content = (ImageButton) itemView.findViewById(R.id.btn_audio_content);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            if (!TextUtils.isEmpty(c.getComment())) {
                txt_audio_comment.setText(c.getComment());
                txt_audio_comment.setVisibility(View.VISIBLE);
            } else {
                txt_audio_comment.setVisibility(View.GONE);
            }
            txt_audio_date.setReferenceTime(c.getCreatedAt());
            btn_audio_content.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            try {
                if (!currentPlaying.equals(itemContent.getContentUrl())) {
                    if (previewPlayedButton != null) {
                        previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, R.drawable.ic_content_play) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentPlaying = itemContent.getContentUrl());
                    Log.wtf("trying to play", currentPlaying);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause));
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currentPlaying = "firebasestorage";
                            btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play));
                        }
                    });
                    mediaPlayer.prepareAsync();
                    previewPlayedButton = (ImageButton) v;
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play));
                    } else {
                        mediaPlayer.start();
                        btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause));
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    public void stopMediaPlayer() {
        mediaPlayer.stop();
    }

    public void releaseMediaPlayer() {
        mediaPlayer.release();
    }

}
