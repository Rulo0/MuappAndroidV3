package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.rd.PageIndicatorView;
import com.rd.animation.AnimationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.muapp.android.Classes.Internal.GiphyMeasureData;
import me.muapp.android.Classes.Internal.MuappQualifications.Qualification;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.SpotifyData;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.YoutubeViewActivity;
import me.muapp.android.UI.Fragment.VideoViewDialogFragment;

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
    UserQualificationsAdapter userQualificationsAdapter;

    public void setQualifications(List<Qualification> qualifications) {
        userQualificationsAdapter = new UserQualificationsAdapter(context, qualifications);
        notifyItemChanged(1);
    }

    int screenWidth;
    List<MuappQuote> quoteList;
    String lang;
    FragmentManager fragmentManager;
    User user;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public UserContentAdapter(Context context, User user) {
        this.user = user;
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

        switch (position) {
            case 0:
                return -1;
            case 1:
                return -2;
            default:
                return viewTypeMap.get(userContentList.get(position - 2).getCatContent());
        }
    }

    @Override
    public UserContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.wtf("ViewType", viewType + "");
        UserContentHolder holder;
        switch (viewType) {
            case -1:
                View headerView = mInflater.inflate(R.layout.profile_header_layout, parent, false);
                holder = new HeaderContentHolder(headerView);
                break;
            case -2:
                View qualificationsView = mInflater.inflate(R.layout.layout_qualifications_profile, parent, false);
                holder = new QualificationsContentHolder(qualificationsView);
                break;
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
            case 7:
                View videoView = mInflater.inflate(R.layout.user_content_video_item_layout, parent, false);
                holder = new VideoContentHolder(videoView);
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
        if (position == 0)
            holder.bind(user);
        else if (position == 1)
            holder.bind(userQualificationsAdapter);
        else {
            holder.bind(userContentList.get(position - 2));
        }
    }

    @Override
    public int getItemCount() {
        return userContentList.size() + 2;
    }

    interface UserContentInterface {
        void bind(UserContent c);

        void bind(User u);

        void bind(UserQualificationsAdapter userQualificationsAdapter);
    }

    class UserContentHolder extends RecyclerView.ViewHolder implements UserContentInterface, View.OnClickListener {
        public UserContentHolder(View itemView) {
            super(itemView);
        }

        ImageButton btnMenu;
        PopupMenu menu;
        UserContent itemContent;

        public void setBtnMenu(ImageButton btnMenu) {
            this.btnMenu = btnMenu;
            btnMenu.setOnClickListener(this);
        }

        @Override
        public void bind(UserContent c) {
            itemContent = c;
            menu = new PopupMenu(context, btnMenu);
            menu.inflate(R.menu.content_menu);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete_content:
                            Toast.makeText(context, itemContent.getKey(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

        @Override
        public void bind(User u) {

        }

        @Override
        public void bind(UserQualificationsAdapter adapter) {

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == btnMenu.getId()) {
                // menu.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new CharSequence[]{context.getString(R.string.lbl_delete_content), context.getString(android.R.string.cancel)}
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteContent();
                                }
                            }
                        });
                builder.show();
            }
        }

        private void deleteContent() {
            FirebaseDatabase.getInstance().getReference().child("content").child(String.valueOf(new UserHelper(context).getLoggedUser().getId())).child(itemContent.getKey()).removeValue();

            if (!TextUtils.isEmpty(itemContent.getStorageName())) {
                FirebaseStorage.getInstance().getReference().child(itemContent.getStorageName()).delete();
            }
            if (!TextUtils.isEmpty(itemContent.getVideoThumbStorage())) {
                FirebaseStorage.getInstance().getReference().child(itemContent.getVideoThumbStorage()).delete();
            }
        }
    }

    class HeaderContentHolder extends UserContentHolder {
        ProfilePicturesAdapter profilePicturesAdapter;
        PageIndicatorView indicator_profile_pictures;
        ViewPager pager_profile_pictures, pager_qualifications;
        TextView title;
        TextView txt_statistics_visits, txt_statistics_muapps, txt_statistics_matches;

        public HeaderContentHolder(View itemView) {
            super(itemView);
            pager_profile_pictures = (ViewPager) itemView.findViewById(R.id.pager_profile_pictures);
            indicator_profile_pictures = (PageIndicatorView) itemView.findViewById(R.id.indicator_profile_pictures);
            title = (TextView) itemView.findViewById(R.id.pillbox_section_text);
            indicator_profile_pictures.setViewPager(pager_profile_pictures);
            indicator_profile_pictures.setRadius(5);
            indicator_profile_pictures.setAnimationType(AnimationType.SWAP);
            txt_statistics_visits = (TextView) itemView.findViewById(R.id.txt_statistics_visits);
            txt_statistics_muapps = (TextView) itemView.findViewById(R.id.txt_statistics_muapps);
            txt_statistics_matches = (TextView) itemView.findViewById(R.id.txt_statistics_matches);
        }

        @Override
        public void bind(User u) {
            super.bind(u);
            title.setText("");
            profilePicturesAdapter = new ProfilePicturesAdapter(context, u.getAlbum());
            pager_profile_pictures.setAdapter(profilePicturesAdapter);
            indicator_profile_pictures.setCount(u.getAlbum().size());
            createHeader(u, title);
            txt_statistics_visits.setText(String.valueOf(u.getVisits()));
            txt_statistics_matches.setText(String.valueOf(u.getMatches()));
        }


        private void createHeader(User user, TextView nameView) {
            String userAge = String.format(context.getString(R.string.format_user_years), user.getAge());
            SpannableString ssAge = new SpannableString(userAge);
            ssAge.setSpan(new StyleSpan(Typeface.BOLD), 0, ssAge.length(), 0);
            nameView.append(ssAge);
            user.setHometown("Chicken Town");
            user.setEducation("Some Place University (SPU)");
            user.setWork("MUAPP");
            if (!TextUtils.isEmpty(user.getHometown())) {
                nameView.append(context.getString(R.string.format_user_hometown));
                nameView.append(" ");
                String userHomeTown = user.getHometown();
                SpannableString ssHomeTown = new SpannableString(userHomeTown);
                ssHomeTown.setSpan(new StyleSpan(Typeface.BOLD), 0, ssHomeTown.length(), 0);
                nameView.append(ssHomeTown);
            }
            if (user.getVisibleEducation() && !TextUtils.isEmpty(user.getEducation())) {
                nameView.append(context.getString(R.string.format_user_studies));
                nameView.append(" ");
                String userStudies = user.getEducation();
                SpannableString ssStudies = new SpannableString(userStudies);
                ssStudies.setSpan(new StyleSpan(Typeface.BOLD), 0, ssStudies.length(), 0);
                nameView.append(ssStudies);
            }
            if (user.getVisibleWork() && !TextUtils.isEmpty(user.getWork())) {
                nameView.append(context.getString(R.string.format_user_work));
                nameView.append(" ");
                String userWork = user.getWork();
                SpannableString ssWork = new SpannableString(userWork);
                ssWork.setSpan(new StyleSpan(Typeface.BOLD), 0, userWork.length(), 0);
                nameView.append(ssWork);
            }
        }
    }

    class QualificationsContentHolder extends UserContentHolder {
        RecyclerView recycler_profile_qualifications;
        TextView txt_profile_qualification;
        View containerView;

        public QualificationsContentHolder(View itemView) {
            super(itemView);
            containerView = itemView;
            txt_profile_qualification = (TextView) itemView.findViewById(R.id.txt_profile_qualification);
            recycler_profile_qualifications = (RecyclerView) itemView.findViewById(R.id.recycler_profile_qualifications);
            LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recycler_profile_qualifications.setLayoutManager(llm);
        }

        @Override
        public void bind(UserQualificationsAdapter qualificationsAdapter) {
            super.bind(qualificationsAdapter);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (qualificationsAdapter != null) {
                recycler_profile_qualifications.setAdapter(qualificationsAdapter);
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
                txt_profile_qualification.setText(String.format(context.getString(R.string.lbl_average_score), user.getAverage(), user.getQualificationsCount()));
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }

    class PictureContentHolder extends UserContentHolder {
        TextView txt_image_comment;
        RelativeTimeTextView txt_picture_date;
        ImageView img_picture_content;
        ImageButton btn_picture_menu;

        public PictureContentHolder(View itemView) {
            super(itemView);
            this.txt_image_comment = (TextView) itemView.findViewById(R.id.txt_image_comment);
            this.txt_picture_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_picture_date);
            this.img_picture_content = (ImageView) itemView.findViewById(R.id.img_picture_content);
            this.btn_picture_menu = (ImageButton) itemView.findViewById(R.id.btn_picture_menu);
            setBtnMenu(btn_picture_menu);
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

    class VideoContentHolder extends UserContentHolder {
        TextView txt_video_comment;
        RelativeTimeTextView txt_video_date;
        ImageView img_video_content;
        ImageButton btn_video_menu;

        public VideoContentHolder(View itemView) {
            super(itemView);
            this.txt_video_comment = (TextView) itemView.findViewById(R.id.txt_video_comment);
            this.txt_video_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_video_date);
            this.img_video_content = (ImageView) itemView.findViewById(R.id.img_video_content);
            this.btn_video_menu = (ImageButton) itemView.findViewById(R.id.btn_video_menu);
            setBtnMenu(btn_video_menu);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            Glide.with(context).load(c.getThumbUrl()).placeholder(R.drawable.ic_logo_muapp_no_caption).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_video_content);
            if (!TextUtils.isEmpty(c.getComment())) {
                txt_video_comment.setText(c.getComment());
                txt_video_comment.setVisibility(View.VISIBLE);
            } else {
                txt_video_comment.setVisibility(View.GONE);
            }
            txt_video_date.setReferenceTime(c.getCreatedAt());
            img_video_content.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            try {
                VideoViewDialogFragment videoViewDialogFragment = VideoViewDialogFragment.newInstance(itemContent);
                videoViewDialogFragment.show(fragmentManager.beginTransaction(), "VideoViewDialogFragment");
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    class GifContentHolder extends UserContentHolder {
        TextView txt_gif_comment;
        RelativeTimeTextView txt_gif_date;
        ImageView img_gif_content;
        View contentView;
        ImageButton btn_gif_menu;

        public GifContentHolder(View itemView) {
            super(itemView);
            this.contentView = itemView;
            this.txt_gif_comment = (TextView) itemView.findViewById(R.id.txt_gif_comment);
            this.txt_gif_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_gif_date);
            this.img_gif_content = (ImageView) itemView.findViewById(R.id.img_gif_content);
            this.btn_gif_menu = (ImageButton) itemView.findViewById(R.id.btn_gif_menu);
            setBtnMenu(btn_gif_menu);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            try {
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
            } catch (Exception x) {
                Log.wtf("Binding Error", x.getMessage());
                x.printStackTrace();
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
        ImageButton btn_spotify_menu;

        public SpotifyContentHolder(View itemView) {
            super(itemView);
            this.txt_spotify_comment = (TextView) itemView.findViewById(R.id.txt_spotify_comment);
            this.txt_spotify_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_spotify_date);
            this.btn_play_detail = (ImageButton) itemView.findViewById(R.id.btn_play_detail);
            this.img_detail_album_blurred = (ImageView) itemView.findViewById(R.id.img_detail_album_blurred);
            this.img_detail_album = (ImageView) itemView.findViewById(R.id.img_detail_album);
            this.txt_detail_name = (TextView) itemView.findViewById(R.id.txt_detail_name);
            this.txt_detail_artist = (TextView) itemView.findViewById(R.id.txt_detail_artist);
            this.btn_spotify_menu = (ImageButton) itemView.findViewById(R.id.btn_spotify_menu);
            setBtnMenu(btn_spotify_menu);
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
            if (v.getId() == btn_play_detail.getId())
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
        ImageButton btn_youtube_menu;

        public YoutubeContentHolder(View itemView) {
            super(itemView);
            this.contentView = itemView;
            this.youtube_thumbnail = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
            this.txt_youtube_comment = (TextView) itemView.findViewById(R.id.txt_youtube_comment);
            this.txt_youtube_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_youtube_date);
            this.btn_youtube_menu = (ImageButton) itemView.findViewById(R.id.btn_youtube_menu);
            setBtnMenu(btn_youtube_menu);
        }

        @Override
        public void bind(final UserContent c) {
            super.bind(c);
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
            super.onClick(v);
            if (v.getId() == youtube_thumbnail.getId()) {
                Intent youtubeIntent = new Intent(context, YoutubeViewActivity.class);
                youtubeIntent.putExtra("itemContent", itemContent);
                context.startActivity(youtubeIntent);
             /*   try {
                    YoutubeViewDialogFragment editNameDialogFragment = YoutubeViewDialogFragment.newInstance(itemContent);
                    editNameDialogFragment.show(fragmentManager.beginTransaction(), "YoutubeViewDialogFragment");
                } catch (Exception x) {
                    x.printStackTrace();
                }*/
            }
        }
    }

    class QuoteContentHolder extends UserContentHolder {
        TextView txt_quote_comment;
        TextView txt_quote_prefix;
        RelativeTimeTextView txt_quote_date;
        ImageButton btn_quote_menu;

        public QuoteContentHolder(View itemView) {
            super(itemView);
            this.txt_quote_comment = (TextView) itemView.findViewById(R.id.txt_quote_comment);
            this.txt_quote_prefix = (TextView) itemView.findViewById(R.id.txt_quote_prefix);
            this.txt_quote_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_quote_date);
            this.btn_quote_menu = (ImageButton) itemView.findViewById(R.id.btn_quote_menu);
            setBtnMenu(btn_quote_menu);
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
        ImageButton btn_audio_menu;

        public AudioContentHolder(View itemView) {
            super(itemView);
            this.txt_audio_comment = (TextView) itemView.findViewById(R.id.txt_audio_comment);
            this.txt_audio_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_audio_date);
            this.btn_audio_content = (ImageButton) itemView.findViewById(R.id.btn_audio_content);
            this.btn_audio_menu = (ImageButton) itemView.findViewById(R.id.btn_audio_menu);
            setBtnMenu(this.btn_audio_menu);
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
            if (v.getId() == btn_audio_content.getId())
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
