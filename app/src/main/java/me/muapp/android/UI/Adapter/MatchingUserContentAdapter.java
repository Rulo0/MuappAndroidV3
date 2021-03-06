package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.gresse.hugo.vumeterlibrary.VuMeterView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.muapp.android.Classes.Internal.GiphyMeasureData;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.MuappQualifications.Qualification;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.MutualFriends;
import me.muapp.android.Classes.Internal.SpotifyData;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.Log;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.VideoViewActivity;
import me.muapp.android.UI.Activity.YoutubeViewActivity;
import me.muapp.android.UI.Adapter.UserPhotos.UserMutualFriendsAdapter;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;

/**
 * Created by rulo on 18/04/17.
 */
public class MatchingUserContentAdapter extends RecyclerView.Adapter<MatchingUserContentAdapter.UserContentHolder> {
    final int TYPE_HEADER = -1;
    final int TYPE_QUALIFICATIONS = -2;
    final int TYPE_MUTUAL_FRIENDS = -3;
    VuMeterView currentAudioView;
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
        put("contentDesc", 9);
    }};
    Context context;
    LayoutInflater mInflater;
    MediaPlayer mediaPlayer;
    String currentPlaying = "";
    ImageButton previewPlayedButton;
    UserQualificationsAdapter userQualificationsAdapter;
    UserMutualFriendsAdapter userMutualFriendsAdapter;
    Boolean showMenuButton = true;
    RecyclerView parentRecycler;
    private OnProfileScrollListener onProfileScrollListener;
    OnScrollListener matchingScrollListener;
    Timer mediaTimer;
    TextView previewPlayedText;
    int playedSeconds = 0;
    SimpleDateFormat sdfTimer = new SimpleDateFormat("mm:ss");
    boolean hasQualifications = false, hasFriends = false;

    public void setShowMenuButton(Boolean showMenuButton) {
        this.showMenuButton = showMenuButton;
    }

    public void removeAllDescriptions() {
        for (int i = 0; i < userContentList.size(); i++) {
            if (userContentList.get(i).getCatContent().equals("contentDesc"))
                userContentList.removeItemAt(i);
        }
    }

    public void setOnProfileScrollListener(OnProfileScrollListener profileScrollListener) {
        try {
            this.onProfileScrollListener = profileScrollListener;
            matchingScrollListener = new OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager llm = (LinearLayoutManager) parentRecycler.getLayoutManager();
                    Log.wtf("scroll", hasFriends + " " + hasQualifications + " " + userContentList.size());
                    if (hasFriends || hasQualifications || userContentList.size() >= 1) {
                        int pos = llm.findFirstVisibleItemPosition();
                        if (llm.findViewByPosition(pos).getTop() == 0 && pos == 0) {
                            if (onProfileScrollListener != null)
                                onProfileScrollListener.onScrollToTop();
                        } else {
                            if (onProfileScrollListener != null)
                                onProfileScrollListener.onScroll();
                        }
                    }
                }
            };
        } catch (Exception x) {
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    public void setQualifications(final List<Qualification> qualifications, final boolean mustScroll) {
        userQualificationsAdapter = new UserQualificationsAdapter(context, qualifications);
        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {
                if (qualifications.size() > 0)
                    hasQualifications = true;
                notifyItemChanged(2);
                if (mustScroll)
                    parentRecycler.scrollToPosition(2);
            }
        };
        handler.post(r);
    }

    public void setMutualFriends(final List<MutualFriends.MutualFriend> mutualFriends) {
        userMutualFriendsAdapter = new UserMutualFriendsAdapter(context, mutualFriends);
        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {
                hasFriends = true;
                Log.wtf("setMutualFriends", "mutualFriends " + mutualFriends.size());
                notifyItemChanged(1);
            }
        };
        handler.post(r);
    }

    public List<Qualification> getQualificationsList() {
        if (userQualificationsAdapter != null) {
            return userQualificationsAdapter.getQualifications();
        } else return null;
    }

    int screenWidth;
    List<MuappQuote> quoteList;
    String lang;
    FragmentManager fragmentManager;
    MatchingUser user;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public MatchingUserContentAdapter(Context context, MatchingUser user) {
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
                return TYPE_HEADER;
            case 1:
                return TYPE_MUTUAL_FRIENDS;
            case 2:
                return TYPE_QUALIFICATIONS;
            default:
                return viewTypeMap.get(userContentList.get(position - 3).getCatContent());
        }
    }

    public void setUser(MatchingUser user) {
        if (!user.getAlbum().equals(this.user.getAlbum())) {
            this.user = user;
            notifyItemChanged(0);
        }
    }

    @Override
    public UserContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        UserContentHolder holder;
        switch (viewType) {
            case TYPE_HEADER:
                View headerView = mInflater.inflate(R.layout.matching_profile_header_layout, parent, false);
                holder = new HeaderContentHolder(headerView);
                break;
            case TYPE_MUTUAL_FRIENDS:
                View friendsView = mInflater.inflate(R.layout.layout_mutual_friends_profile, parent, false);
                holder = new CommonConnectionsContentHolder(friendsView);
                break;
            case TYPE_QUALIFICATIONS:
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
            case 9:
                View descriptionView = mInflater.inflate(R.layout.user_content_quote_item_layout, parent, false);
                holder = new DescriptionContentHolder(descriptionView);
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
            holder.bind(userMutualFriendsAdapter);
        else if (position == 2)
            holder.bind(userQualificationsAdapter);
        else {
            holder.bind(userContentList.get(position - 3));
        }
    }

    @Override
    public int getItemCount() {
        return userContentList.size() + 3;
    }

    interface UserContentInterface {
        void bind(UserContent c);

        void bind(MatchingUser u);

        void bind(UserQualificationsAdapter userQualificationsAdapter);

        void bind(UserMutualFriendsAdapter userMutualFriendsAdapter);
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
            if (showMenuButton)
                btnMenu.setOnClickListener(this);
            else
                this.btnMenu.setVisibility(View.GONE);
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
            parentRecycler.clearOnScrollListeners();
            parentRecycler.addOnScrollListener(matchingScrollListener);
        }

        @Override
        public void bind(MatchingUser u) {

        }

        @Override
        public void bind(UserQualificationsAdapter adapter) {
            parentRecycler.clearOnScrollListeners();
            parentRecycler.addOnScrollListener(matchingScrollListener);
        }

        @Override
        public void bind(UserMutualFriendsAdapter adapter) {
            parentRecycler.clearOnScrollListeners();
            parentRecycler.addOnScrollListener(matchingScrollListener);
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
            FirebaseDatabase.getInstance().getReference("content").child(String.valueOf(new UserHelper(context).getLoggedUser().getId())).child(itemContent.getKey()).removeValue();
            if (!TextUtils.isEmpty(itemContent.getStorageName())) {
                FirebaseStorage.getInstance().getReference().child(itemContent.getStorageName()).delete();
            }
            if (!TextUtils.isEmpty(itemContent.getVideoThumbStorage())) {
                FirebaseStorage.getInstance().getReference().child(itemContent.getVideoThumbStorage()).delete();
            }
            stopMediaPlayer();
        }
    }

    class HeaderContentHolder extends UserContentHolder {
        ProfilePicturesAdapter profilePicturesAdapter;
        PageIndicatorView indicator_profile_pictures;
        ViewPager pager_profile_pictures;
        TextView title;
        TextView txt_matching_distance;
        TextView txt_matching_last_seen;

        public HeaderContentHolder(View itemView) {
            super(itemView);
            pager_profile_pictures = (ViewPager) itemView.findViewById(R.id.pager_profile_pictures);
            indicator_profile_pictures = (PageIndicatorView) itemView.findViewById(R.id.indicator_profile_pictures);
            title = (TextView) itemView.findViewById(R.id.pillbox_section_text);
            txt_matching_distance = (TextView) itemView.findViewById(R.id.txt_matching_distance);
            txt_matching_last_seen = (TextView) itemView.findViewById(R.id.txt_matching_last_seen);
            indicator_profile_pictures.setViewPager(pager_profile_pictures);
            indicator_profile_pictures.setRadius(5);
            indicator_profile_pictures.setAnimationType(AnimationType.SWAP);
        }

        @Override
        public void bind(MatchingUser u) {
            super.bind(u);
            title.setText("");
            pager_profile_pictures.invalidate();
            profilePicturesAdapter = new ProfilePicturesAdapter(context, u.getAlbum());
            pager_profile_pictures.setAdapter(profilePicturesAdapter);
            indicator_profile_pictures.setCount(u.getAlbum().size());
            createHeader(u, title);
            getLocationString(u.getLatitude(), u.getLongitude());
            getLastSeenString(u.getLastSeenDate());
        }

        private void getLastSeenString(Date lastSeen) {
            long difference = Math.abs(Calendar.getInstance().getTimeInMillis() - lastSeen.getTime());
            txt_matching_last_seen.setText(difference + " mills");

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;
            long weeksInMilli = daysInMilli * 7;
            long elapsedWeeks = difference / weeksInMilli;
            difference = difference % weeksInMilli;
            long elapsedDays = difference / daysInMilli;
            difference = difference % daysInMilli;
            long elapsedHours = difference / hoursInMilli;
            difference = difference % hoursInMilli;
            long elapsedMinutes = difference / minutesInMilli;
            if (elapsedWeeks > 0) {
                txt_matching_last_seen.setText(String.format(Locale.getDefault().getLanguage().equals("es") ? "%s s" : "%s w", elapsedWeeks));
            } else if (elapsedDays > 0) {
                txt_matching_last_seen.setText(String.format("%s d", elapsedDays));
            } else if (elapsedHours > 0) {
                txt_matching_last_seen.setText(String.format("%s h", elapsedHours));
            } else {
                if (elapsedMinutes == 0) {
                    txt_matching_last_seen.setText(String.format("%s m", 1));
                } else {
                    txt_matching_last_seen.setText(String.format("%s m", elapsedMinutes));
                }
            }

            /*Log.i("getMatchingUsers", String.format(
                    "%d weeks, %d days, %d hours, %d minutes",
                    elapsedWeeks,
                    elapsedDays,
                    elapsedHours, elapsedMinutes));*/
        }

        private void getLocationString(String latitude, String longitude) {
            Location locationMatching = new Location("MATCHING");
            try {
                locationMatching.setLatitude(Double.parseDouble(latitude));
            } catch (Exception x) {
                locationMatching.setLatitude(0);
            }
            try {
                locationMatching.setLongitude(Double.parseDouble(longitude));
            } catch (Exception x) {
                locationMatching.setLongitude(0);
            }
            Float distance = locationMatching.distanceTo(new PreferenceHelper(context).getLocation());
            if (distance > 1000) {
                txt_matching_distance.setText(String.format("%s km", Math.round(distance / 1000)));
            } else {
                txt_matching_distance.setText(String.format("%s m", Math.round(distance)));
            }
        }


        private void createHeader(MatchingUser user, TextView nameView) {
            String userAge = String.format(context.getString(R.string.format_user_years), user.getAge());
            SpannableString ssAge = new SpannableString(userAge);
            ssAge.setSpan(new StyleSpan(Typeface.BOLD), 0, ssAge.length(), 0);
            nameView.append(ssAge);
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
            if (qualificationsAdapter != null && qualificationsAdapter.getItemCount() > 0) {
                recycler_profile_qualifications.setAdapter(qualificationsAdapter);
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
                txt_profile_qualification.setText(String.format(context.getString(R.string.lbl_average_score), String.format("%.1f", qualificationsAdapter.getAverage()), qualificationsAdapter.getItemCount()));
            } else {
                itemView.setVisibility(View.GONE);
                param.setMargins(0, 0, 0, 0);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }

    class CommonConnectionsContentHolder extends UserContentHolder {
        RecyclerView recycler_common_connections;
        TextView txt_profile_connections;
        View containerView;

        public CommonConnectionsContentHolder(View itemView) {
            super(itemView);
            containerView = itemView;
            txt_profile_connections = (TextView) itemView.findViewById(R.id.txt_profile_connections);
            recycler_common_connections = (RecyclerView) itemView.findViewById(R.id.recycler_common_connections);
            LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recycler_common_connections.setLayoutManager(llm);
        }

        @Override
        public void bind(UserMutualFriendsAdapter friendsAdapter) {
            super.bind(friendsAdapter);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (friendsAdapter != null && friendsAdapter.getItemCount() > 0) {
                recycler_common_connections.setAdapter(userMutualFriendsAdapter);
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
                txt_profile_connections.setText(String.format(context.getString(R.string.format_common_connections), String.valueOf(friendsAdapter.getItemCount())));
            } else {
                itemView.setVisibility(View.GONE);
                param.setMargins(0, 0, 0, 0);
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
            Glide.with(context).load(c.getContentUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(img_picture_content);
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
            Glide.with(context).load(c.getThumbUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(img_video_content);
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
            if (v.getId() == img_video_content.getId()) {
                Intent videoIntent = new Intent(context, VideoViewActivity.class);
                videoIntent.putExtra("itemContent", itemContent);
                context.startActivity(videoIntent);
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
                    Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).override((int) (screenWidth * aspectRatio), screenWidth).dontAnimate().into(img_gif_content);
                } else {
                    aspectRatio = (float) giphyMeasureData.getWidth() / (float) giphyMeasureData.getHeight();
                    Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(screenWidth, (int) (screenWidth * aspectRatio)).dontAnimate().into(img_gif_content);
                }
            } catch (Exception x) {

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
                Glide.with(context).load(currentData.getThumb()).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.IMMEDIATE).centerCrop().into(img_detail_album);
                Glide.with(context).load(currentData.getThumb()).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.IMMEDIATE).bitmapTransform(new CenterCrop(context), new BlurTransformation(context)).into(img_detail_album_blurred);
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
            previewPlayedText = null;
            if (v.getId() == btn_play_detail.getId())
                if (currentAudioView != null) {
                    currentAudioView.stop(true);
                    currentAudioView = null;
                }
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
        TextView youtube_title;
        YouTubeThumbnailView youtube_thumbnail;
        ImageButton btn_youtube_menu;

        public YoutubeContentHolder(View itemView) {
            super(itemView);
            this.contentView = itemView;
            this.youtube_title = (TextView) itemView.findViewById(R.id.youtube_title);
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
            if (!TextUtils.isEmpty(c.getVideoTitle())) {
                youtube_title.setText(c.getVideoTitle());
                youtube_title.setVisibility(View.VISIBLE);
            } else {
                youtube_title.setVisibility(View.GONE);
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

    private void startTimer() {
        resetTimer();
        if (currentAudioView != null)
            currentAudioView.resume(true);
        mediaTimer = new Timer();
        mediaTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (previewPlayedText != null)
                            previewPlayedText.setText(sdfTimer.format(new Date(mediaPlayer.getCurrentPosition())));
                        playedSeconds = mediaPlayer.getCurrentPosition() / 1000;
                    }
                };
                mainHandler.post(myRunnable);
            }
        }, 0, 100);
    }

    private void pauseTimer() {
        if (mediaTimer != null)
            mediaTimer.cancel();
        if (currentAudioView != null)
            currentAudioView.pause();
    }

    private void resetTimer() {
        if (mediaTimer != null)
            mediaTimer.cancel();
        if (currentAudioView != null)
            currentAudioView.stop(true);
        playedSeconds = 0;
        if (previewPlayedText != null)
            previewPlayedText.setText(sdfTimer.format(new Date(playedSeconds)));
    }


    class AudioContentHolder extends UserContentHolder {
        TextView txt_audio_comment;
        TextView txt_audio_content_timer;
        RelativeTimeTextView txt_audio_date;
        ImageButton btn_audio_content;
        ImageButton btn_audio_menu;
        VuMeterView audio_view;
        TextView txt_audio_content_length;

        public AudioContentHolder(View itemView) {
            super(itemView);
            this.txt_audio_comment = (TextView) itemView.findViewById(R.id.txt_audio_comment);
            this.txt_audio_content_timer = (TextView) itemView.findViewById(R.id.txt_audio_content_timer);
            this.txt_audio_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_audio_date);
            this.btn_audio_content = (ImageButton) itemView.findViewById(R.id.btn_audio_content);
            this.btn_audio_menu = (ImageButton) itemView.findViewById(R.id.btn_audio_menu);
            this.txt_audio_content_length = (TextView) itemView.findViewById(R.id.txt_audio_content_length);
            this.audio_view = (VuMeterView) itemView.findViewById(R.id.audio_view);
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
            if (c.getAudioLenght() != null)
                txt_audio_content_length.setText(sdfTimer.format(new Date(c.getAudioLenght() * 1000)));
            txt_audio_date.setReferenceTime(c.getCreatedAt());
            btn_audio_content.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
            if (v.getId() == btn_audio_content.getId())
                try {
                    if (currentAudioView != null)
                        currentAudioView.stop(true);
                    currentAudioView = audio_view;
                    if (!currentPlaying.equals(itemContent.getContentUrl())) {
                        if (previewPlayedButton != null) {
                            previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, R.drawable.ic_content_play) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                        }
                        if (previewPlayedText != null)
                            previewPlayedText.setText("00:00");
                        previewPlayedText = txt_audio_content_timer;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(currentPlaying = itemContent.getContentUrl());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause));
                                mediaPlayer.start();
                                startTimer();
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                currentPlaying = "firebasestorage";
                                btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play));
                                resetTimer();
                            }
                        });
                        mediaPlayer.prepareAsync();
                        previewPlayedButton = (ImageButton) v;
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            pauseTimer();
                            btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play));
                        } else {
                            mediaPlayer.start();
                            startTimer();
                            btn_audio_content.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause));
                        }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
        }
    }

    class DescriptionContentHolder extends UserContentHolder {
        TextView txt_quote_comment;
        TextView txt_quote_prefix;
        RelativeTimeTextView txt_quote_date;
        ImageButton btn_quote_menu;
        RelativeLayout quotes_container_footer;

        public DescriptionContentHolder(View itemView) {
            super(itemView);
            this.quotes_container_footer = (RelativeLayout) itemView.findViewById(R.id.quotes_container_footer);
            this.txt_quote_comment = (TextView) itemView.findViewById(R.id.txt_quote_comment);
            this.txt_quote_prefix = (TextView) itemView.findViewById(R.id.txt_quote_prefix);
            this.txt_quote_date = (RelativeTimeTextView) itemView.findViewById(R.id.txt_quote_date);
            this.btn_quote_menu = (ImageButton) itemView.findViewById(R.id.btn_quote_menu);
            setBtnMenu(btn_quote_menu);
        }

        @Override
        public void bind(UserContent c) {
            super.bind(c);
            quotes_container_footer.setVisibility(View.GONE);
            txt_quote_prefix.setText(context.getString(R.string.lbl_about_me));
            txt_quote_comment.setText(c.getComment());
            txt_quote_date.setReferenceTime(c.getCreatedAt());
        }
    }

    public void stopMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            resetTimer();
            if (previewPlayedButton != null) {
                previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, R.drawable.ic_content_play) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
            }
        }
        currentPlaying = "";
        if (currentAudioView != null)
            currentAudioView.stop(true);
    }

    public void releaseMediaPlayer() {
        mediaPlayer.release();
    }

}
