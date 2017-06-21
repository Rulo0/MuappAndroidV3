package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import me.muapp.android.Classes.Util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.Classes.Internal.GiphyMeasureData;
import me.muapp.android.Classes.Internal.SpotifyData;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.YoutubeViewActivity;

import static me.muapp.android.Classes.Youtube.Config.getYoutubeApiKey;

/**
 * Created by rulo on 17/05/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageContentHolder> {
    SortedList<Message> messageList;
    Context context;
    LayoutInflater mInflater;
    MediaPlayer mediaPlayer;
    Timer mediaTimer;
    int playedSeconds;
    RecyclerView mRecyclerView;
    Integer loggedUserId;
    int screenWidth;
    String currentPlaying = "";
    ImageButton previewPlayedButton;
    TextView previewPlayedText;
    Boolean fromOpponent = true;
    String myPhotoUrl;
    String yourPhotoUrl;
    Long lastSeenByOpponent = 0L;
    SimpleDateFormat sdfTimer = new SimpleDateFormat("mm:ss");
    private Set<MessageContentHolder> mBoundViewHolders = new HashSet<>();

    public void setLastSeenByOpponent(Long lastSeenByOpponent) {
        this.lastSeenByOpponent = lastSeenByOpponent;
        Log.wtf("lastSeenByOpponent", "Now: " + lastSeenByOpponent);
        for (MessageContentHolder holder : mBoundViewHolders) {
            holder.updateIndicators();
        }
    }

    public void setParticipantsPhotos(String myPhotoUrl, String yourPhotoUrl) {
        this.myPhotoUrl = myPhotoUrl;
        this.yourPhotoUrl = yourPhotoUrl;
    }

    public void clearConversation() {
        this.messageList.clear();
    }

    public void setLoggedUserId(Integer loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    private static final int TYPE_SENDER = 99;
    private static final int TYPE_RECEIVER = 98;
    HashMap<String, Integer> viewTypeMapSender = new HashMap<String, Integer>() {{
        put("contentAud", 1);
        put("contentCmt", 2);
        put("contentGif", 3);
        put("contentPic", 4);
        put("contentQte", 5);
        put("contentSpt", 6);
        put("contentVid", 7);
        put("contentYtv", 8);
        put("contentDesc", 9);
        put("contentStkr", 10);
    }};
    HashMap<String, Integer> viewTypeMapReceiver = new HashMap<String, Integer>() {{
        put("contentAud", -1);
        put("contentCmt", -2);
        put("contentGif", -3);
        put("contentPic", -4);
        put("contentQte", -5);
        put("contentSpt", -6);
        put("contentVid", -7);
        put("contentYtv", -8);
        put("contentDesc", -9);
        put("contentStkr", -10);
    }};

    public void addMessage(Message m) {
        messageList.add(m);
        Log.wtf("adding", m.toString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }


    public MessagesAdapter(final Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mediaPlayer = new MediaPlayer();
        this.messageList = new SortedList<>(Message.class, new SortedList.Callback<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return new Date(m1.getTimeStamp()).compareTo(new Date(m2.getTimeStamp()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Message oldItem, Message newItem) {
                return newItem.toString().equals(oldItem.toString());
            }

            @Override
            public boolean areItemsTheSame(Message item1, Message item2) {
                return item1.getKey().equals(item2.getKey());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
                mRecyclerView.scrollToPosition(position);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public MessageContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageContentHolder holder;
        switch (viewType) {
            //Text
            case TYPE_SENDER:
                View senderView = mInflater.inflate(R.layout.message_item_layout_sender, parent, false);
                holder = new MyMessageContentHolder(senderView);
                break;
            case TYPE_RECEIVER:
                View receiverView = mInflater.inflate(R.layout.message_item_layout_receiver, parent, false);
                holder = new YourMessageContentHolder(receiverView);
                break;
            //Audio
            case 1:
                View senderViewVoiceNote = mInflater.inflate(R.layout.message_item_layout_sender_voicenote, parent, false);
                holder = new MyAudioContentHolder(senderViewVoiceNote);
                break;
            case -1:
                View receiverViewVoiceNote = mInflater.inflate(R.layout.message_item_layout_receiver_voicenote, parent, false);
                holder = new YourAudioContentHolder(receiverViewVoiceNote);
                break;
            //Gifs
            case 3:
                View senderGifView = mInflater.inflate(R.layout.message_item_layout_sender_gif, parent, false);
                holder = new MyGifContentHolder(senderGifView);
                break;
            case -3:
                View receiverGifView = mInflater.inflate(R.layout.message_item_layout_receiver_gif, parent, false);
                holder = new YourGifContentHolder(receiverGifView);
                break;
            //Pictures
            case 4:
                View senderImageView = mInflater.inflate(R.layout.message_item_layout_sender_image, parent, false);
                holder = new MyImageContentHolder(senderImageView);
                break;
            case -4:
                View receiverImageView = mInflater.inflate(R.layout.message_item_layout_receiver_image, parent, false);
                holder = new YourImageContentHolder(receiverImageView);
                break;
            //Spotify
            case 6:
                View senderSpotifyView = mInflater.inflate(R.layout.message_item_layout_sender_spotify, parent, false);
                holder = new MySpotifyContentHolder(senderSpotifyView);
                break;
            case -6:
                View receiverSpotifyView = mInflater.inflate(R.layout.message_item_layout_receiver_spotify, parent, false);
                holder = new YourSpotifyContentHolder(receiverSpotifyView);
                break;
            //YouTube
            case 8:
                View senderYoutubeView = mInflater.inflate(R.layout.message_item_layout_sender_youtube, parent, false);
                holder = new MyYoutubeContentHolder(senderYoutubeView);
                break;
            case -8:
                View receiverYoutubeView = mInflater.inflate(R.layout.message_item_layout_receiver_youtube, parent, false);
                holder = new YourYoutubeContentHolder(receiverYoutubeView);
                break;
            //Stickers
            case 10:
                View senderStickerView = mInflater.inflate(R.layout.message_item_layout_sender_sticker, parent, false);
                holder = new MyStickerContentHolder(senderStickerView);
                break;
            case -10:
                View receiverStickerView = mInflater.inflate(R.layout.message_item_layout_receiver_sticker, parent, false);
                holder = new YourStickerContentHolder(receiverStickerView);
                break;
            default:
                View view = mInflater.inflate(R.layout.message_item_layout_sender, parent, false);
                holder = new MyMessageContentHolder(view);
                break;
        }
        return holder;
    }

    public void clearMediaPlayer() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void stopMediaPlayer() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        resetTimer();
        currentPlaying = "";
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId() == loggedUserId) {
            if (messageList.get(position).getAttachment() != null) {
                return viewTypeMapSender.get(messageList.get(position).getAttachment().getCatContent());
            }
            return TYPE_SENDER;
        } else {
            if (messageList.get(position).getAttachment() != null) {
                return viewTypeMapReceiver.get(messageList.get(position).getAttachment().getCatContent());
            }
            return TYPE_RECEIVER;
        }
    }

    @Override
    public void onBindViewHolder(MessageContentHolder holder, int position) {
        holder.bind(messageList.get(position));
        mBoundViewHolders.add(holder);
    }

    @Override
    public void onViewRecycled(MessageContentHolder holder) {
        super.onViewRecycled(holder);
        mBoundViewHolders.remove(holder);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    interface UserMessagesInterface {
        void bind(Message message);

        void setIndicatorView(ImageView indicatorView);

        void updateIndicators();
    }

    public class MessageContentHolder extends RecyclerView.ViewHolder implements UserMessagesInterface {
        ImageView indicatorView;
        Long messageTimeStamp;
        UserContent attachment;

        public MessageContentHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Message message) {
            if (message.getAttachment() != null)
                this.attachment = message.getAttachment();

            if (message.getTimeStamp() > new Date().getTime())
                message.setTimeStamp(new Date().getTime());

            this.messageTimeStamp = message.getTimeStamp();


            if (this.indicatorView != null) {
                if (message.getTimeStamp() <= lastSeenByOpponent)
                    this.indicatorView.setImageResource(R.drawable.ic_chat_indicator_read);
                else
                    this.indicatorView.setImageResource(R.drawable.ic_chat_indicator_received);
            }
        }


        @Override
        public void setIndicatorView(ImageView indicatorView) {
            this.indicatorView = indicatorView;
        }

        @Override
        public void updateIndicators() {
            if (this.indicatorView != null) {
                if (this.messageTimeStamp <= lastSeenByOpponent)
                    this.indicatorView.setImageResource(R.drawable.ic_chat_indicator_read);
                else
                    this.indicatorView.setImageResource(R.drawable.ic_chat_indicator_received);
            }
        }
    }

    public class MyMessageContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_sender;
        TextView txt_content_sender;
        ImageView img_indicator_sender;

        public MyMessageContentHolder(View itemView) {
            super(itemView);
            txt_time_sender = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender);
            txt_content_sender = (TextView) itemView.findViewById(R.id.txt_content_sender);
            img_indicator_sender = (ImageView) itemView.findViewById(R.id.img_indicator_sender);
            setIndicatorView(img_indicator_sender);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            txt_time_sender.setReferenceTime(message.getTimeStamp());
            txt_content_sender.setText(message.getContent());
        }
    }

    public class YourMessageContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_receiver;
        TextView txt_content_receiver;

        public YourMessageContentHolder(View itemView) {
            super(itemView);
            txt_time_receiver = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver);
            txt_content_receiver = (TextView) itemView.findViewById(R.id.txt_content_receiver);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            txt_time_receiver.setReferenceTime(message.getTimeStamp());
            txt_content_receiver.setText(message.getContent());
        }
    }

    private void startTimer() {
        if (mediaTimer != null)
            mediaTimer.cancel();
        mediaTimer = new Timer();
        mediaTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (previewPlayedText != null)
                            previewPlayedText.setText(String.valueOf(sdfTimer.format(new Date(mediaPlayer.getCurrentPosition()))));
                        playedSeconds++;
                    }
                });
            }
        }, 0, 100);
    }

    private void pauseTimer() {
        if (mediaTimer != null)
            mediaTimer.cancel();
    }

    private void resetTimer() {
        if (mediaTimer != null)
            mediaTimer.cancel();
        playedSeconds = 0;
        if (previewPlayedText != null)
            previewPlayedText.setText(String.valueOf(sdfTimer.format(new Date(playedSeconds * 1000))));
    }

    public class MyAudioContentHolder extends MessageContentHolder implements View.OnClickListener {
        RelativeTimeTextView txt_time_sender_voicenote;
        ImageView img_sender_audio_face;
        ImageButton btn_sender_audio_play_pause;
        ImageView img_indicator_sender_voicenote;
        UserContent itemContent;
        TextView txt_sender_voicenote_timer;

        public MyAudioContentHolder(View itemView) {
            super(itemView);
            txt_time_sender_voicenote = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender_voicenote);
            txt_sender_voicenote_timer = (TextView) itemView.findViewById(R.id.txt_sender_voicenote_timer);
            img_sender_audio_face = (ImageView) itemView.findViewById(R.id.img_sender_audio_face);
            btn_sender_audio_play_pause = (ImageButton) itemView.findViewById(R.id.btn_sender_audio_play_pause);
            img_indicator_sender_voicenote = (ImageView) itemView.findViewById(R.id.img_indicator_sender_voicenote);
            setIndicatorView(img_indicator_sender_voicenote);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            itemContent = message.getAttachment();
            txt_time_sender_voicenote.setReferenceTime(message.getTimeStamp());
            Glide.with(context).load(myPhotoUrl).placeholder(R.drawable.ic_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(context)).into(img_sender_audio_face);
            btn_sender_audio_play_pause.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (!currentPlaying.equals(itemContent.getContentUrl())) {
                    if (previewPlayedButton != null) {
                        previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, fromOpponent ? R.drawable.ic_content_play : R.drawable.ic_content_play_white) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentPlaying = itemContent.getContentUrl());
                    fromOpponent = false;
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            btn_sender_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause_white));
                            mediaPlayer.start();
                            if (previewPlayedText != null)
                                previewPlayedText.setText("00:00");
                            previewPlayedText = txt_sender_voicenote_timer;
                            startTimer();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            resetTimer();
                            currentPlaying = "firebasestorage";
                            btn_sender_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play_white));
                        }
                    });
                    mediaPlayer.prepareAsync();
                    previewPlayedButton = (ImageButton) v;
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        pauseTimer();
                        btn_sender_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play_white));
                    } else {
                        mediaPlayer.start();
                        startTimer();
                        btn_sender_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause_white));
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    public class YourAudioContentHolder extends MessageContentHolder implements View.OnClickListener {
        RelativeTimeTextView txt_time_receiver_voicenote;
        ImageView img_receiver_audio_face;
        ImageButton btn_receiver_audio_play_pause;
        UserContent itemContent;
        TextView txt_receiver_voicenote_timer;

        public YourAudioContentHolder(View itemView) {
            super(itemView);
            txt_time_receiver_voicenote = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver_voicenote);
            txt_receiver_voicenote_timer = (TextView) itemView.findViewById(R.id.txt_receiver_voicenote_timer);
            img_receiver_audio_face = (ImageView) itemView.findViewById(R.id.img_receiver_audio_face);
            btn_receiver_audio_play_pause = (ImageButton) itemView.findViewById(R.id.btn_receiver_audio_play_pause);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            itemContent = message.getAttachment();
            txt_time_receiver_voicenote.setReferenceTime(message.getTimeStamp());
            Glide.with(context).load(yourPhotoUrl).placeholder(R.drawable.ic_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(context)).into(img_receiver_audio_face);
            btn_receiver_audio_play_pause.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (!currentPlaying.equals(itemContent.getContentUrl())) {
                    if (previewPlayedButton != null) {
                        previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, fromOpponent ? R.drawable.ic_content_play : R.drawable.ic_content_play_white) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
                    }
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentPlaying = itemContent.getContentUrl());
                    fromOpponent = true;
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            btn_receiver_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause));
                            mediaPlayer.start();
                            previewPlayedText = txt_receiver_voicenote_timer;
                            startTimer();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currentPlaying = "firebasestorage";
                            btn_receiver_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play));
                            resetTimer();
                        }
                    });
                    mediaPlayer.prepareAsync();
                    previewPlayedButton = (ImageButton) v;
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        pauseTimer();
                        btn_receiver_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_play));
                    } else {
                        mediaPlayer.start();
                        startTimer();
                        btn_receiver_audio_play_pause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_content_pause));
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    public class MyGifContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_sender_gif;
        TextView txt_content_sender_gif;
        ImageView img_gif_sender;
        ImageView img_indicator_sender_gif;

        public MyGifContentHolder(View itemView) {
            super(itemView);
            img_gif_sender = (ImageView) itemView.findViewById(R.id.img_gif_sender);
            txt_time_sender_gif = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender_gif);
            txt_content_sender_gif = (TextView) itemView.findViewById(R.id.txt_content_sender_gif);
            img_indicator_sender_gif = (ImageView) itemView.findViewById(R.id.img_indicator_sender_gif);
            setIndicatorView(img_indicator_sender_gif);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            txt_time_sender_gif.setReferenceTime(message.getTimeStamp());
            if (!TextUtils.isEmpty(message.getContent()))
                txt_content_sender_gif.setText(message.getContent());
            else txt_content_sender_gif.setVisibility(View.GONE);
            try {
                UserContent c = message.getAttachment();
                GiphyMeasureData giphyMeasureData = c.getGiphyMeasureData();
                float aspectRatio;
                if (giphyMeasureData.getHeight() >= giphyMeasureData.getWidth()) {
                    aspectRatio = (float) giphyMeasureData.getHeight() / (float) giphyMeasureData.getWidth();
                    Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).override((int) (screenWidth * aspectRatio), screenWidth).into(img_gif_sender);
                } else {
                    aspectRatio = (float) giphyMeasureData.getWidth() / (float) giphyMeasureData.getHeight();
                    Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(screenWidth, (int) (screenWidth * aspectRatio)).into(img_gif_sender);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }

        }
    }

    public class YourGifContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_receiver_gif;
        TextView txt_content_receiver_gif;
        ImageView img_gif_receiver;

        public YourGifContentHolder(View itemView) {
            super(itemView);
            img_gif_receiver = (ImageView) itemView.findViewById(R.id.img_gif_receiver);
            txt_time_receiver_gif = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver_gif);
            txt_content_receiver_gif = (TextView) itemView.findViewById(R.id.txt_content_receiver_gif);
        }

        @Override
        public void bind(Message message) {
            txt_time_receiver_gif.setReferenceTime(message.getTimeStamp());
            if (!TextUtils.isEmpty(message.getContent()))
                txt_content_receiver_gif.setText(message.getContent());
            else txt_content_receiver_gif.setVisibility(View.GONE);
            try {
                UserContent c = message.getAttachment();
                GiphyMeasureData giphyMeasureData = c.getGiphyMeasureData();
                float aspectRatio;
                if (giphyMeasureData.getHeight() >= giphyMeasureData.getWidth()) {
                    aspectRatio = (float) giphyMeasureData.getHeight() / (float) giphyMeasureData.getWidth();
                    Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).override((int) (screenWidth * aspectRatio), screenWidth).into(img_gif_receiver);
                } else {
                    aspectRatio = (float) giphyMeasureData.getWidth() / (float) giphyMeasureData.getHeight();
                    Glide.with(context).load(c.getContentUrl()).asGif().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(screenWidth, (int) (screenWidth * aspectRatio)).into(img_gif_receiver);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

    public class MyImageContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_sender_image;
        ImageView img_sender_image;
        ImageView img_indicator_sender_image;

        public MyImageContentHolder(View itemView) {
            super(itemView);
            txt_time_sender_image = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender_image);
            img_sender_image = (ImageView) itemView.findViewById(R.id.img_sender_image);
            img_indicator_sender_image = (ImageView) itemView.findViewById(R.id.img_indicator_sender_image);
            setIndicatorView(img_indicator_sender_image);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            txt_time_sender_image.setReferenceTime(message.getTimeStamp());
            Glide.with(context).load(message.getAttachment().getContentUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(img_sender_image);
        }
    }

    public class YourImageContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_receiver_image;
        ImageView img_receiver_image;

        public YourImageContentHolder(View itemView) {
            super(itemView);
            txt_time_receiver_image = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver_image);
            img_receiver_image = (ImageView) itemView.findViewById(R.id.img_receiver_image);
        }

        @Override
        public void bind(Message message) {
            try {
                txt_time_receiver_image.setReferenceTime(message.getTimeStamp());
            } catch (Exception x) {

            }
            Glide.with(context).load(message.getAttachment().getContentUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(img_receiver_image);
        }
    }

    public class MySpotifyContentHolder extends MessageContentHolder implements View.OnClickListener {
        ImageView img_detail_album_blurred, img_detail_album;
        TextView txt_detail_name, txt_detail_artist;
        ImageButton btn_play_detail;
        SpotifyData currentData;
        TextView txt_content_sender_spotify;
        RelativeTimeTextView txt_time_sender_spotify;
        ImageView img_indicator_sender_spotify;

        public MySpotifyContentHolder(View itemView) {
            super(itemView);
            this.btn_play_detail = (ImageButton) itemView.findViewById(R.id.btn_play_detail);
            this.img_detail_album_blurred = (ImageView) itemView.findViewById(R.id.img_detail_album_blurred);
            this.img_detail_album = (ImageView) itemView.findViewById(R.id.img_detail_album);
            this.txt_detail_name = (TextView) itemView.findViewById(R.id.txt_detail_name);
            this.txt_detail_artist = (TextView) itemView.findViewById(R.id.txt_detail_artist);
            this.txt_content_sender_spotify = (TextView) itemView.findViewById(R.id.txt_content_sender_spotify);
            this.txt_time_sender_spotify = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender_spotify);
            this.img_indicator_sender_spotify = (ImageView) itemView.findViewById(R.id.img_indicator_sender_spotify);
            setIndicatorView(img_indicator_sender_spotify);
        }

        private int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            if (!TextUtils.isEmpty(message.getContent())) {
                txt_content_sender_spotify.setText(message.getContent());
                txt_content_sender_spotify.setVisibility(View.VISIBLE);
            } else
                txt_content_sender_spotify.setVisibility(View.GONE);
            txt_time_sender_spotify.setReferenceTime(message.getTimeStamp());
            UserContent c = message.getAttachment();
            if ((currentData = c.getSpotifyData()) != null) {
                ViewGroup.LayoutParams p = btn_play_detail.getLayoutParams();
                p.width = dpToPx(40);
                p.height = dpToPx(40);
                btn_play_detail.setLayoutParams(p);
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
            if (v.getId() == btn_play_detail.getId())
                try {
                    if (!currentPlaying.equals(currentData.getPreviewUrl())) {
                        if (previewPlayedButton != null) {
                            previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, fromOpponent ? R.drawable.ic_content_play : R.drawable.ic_content_play_white) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
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

    public class YourSpotifyContentHolder extends MessageContentHolder implements View.OnClickListener {
        ImageView img_detail_album_blurred, img_detail_album;
        TextView txt_detail_name, txt_detail_artist;
        ImageButton btn_play_detail;
        SpotifyData currentData;
        TextView txt_content_receiver_spotify;
        RelativeTimeTextView txt_time_receiver_spotify;

        public YourSpotifyContentHolder(View itemView) {
            super(itemView);
            this.btn_play_detail = (ImageButton) itemView.findViewById(R.id.btn_play_detail);
            this.img_detail_album_blurred = (ImageView) itemView.findViewById(R.id.img_detail_album_blurred);
            this.img_detail_album = (ImageView) itemView.findViewById(R.id.img_detail_album);
            this.txt_detail_name = (TextView) itemView.findViewById(R.id.txt_detail_name);
            this.txt_detail_artist = (TextView) itemView.findViewById(R.id.txt_detail_artist);
            this.txt_content_receiver_spotify = (TextView) itemView.findViewById(R.id.txt_content_receiver_spotify);
            this.txt_time_receiver_spotify = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver_spotify);
        }

        private int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            if (!TextUtils.isEmpty(message.getContent())) {
                txt_content_receiver_spotify.setText(message.getContent());
                txt_content_receiver_spotify.setVisibility(View.VISIBLE);
            } else
                txt_content_receiver_spotify.setVisibility(View.GONE);
            txt_time_receiver_spotify.setReferenceTime(message.getTimeStamp());
            UserContent c = message.getAttachment();
            if ((currentData = c.getSpotifyData()) != null) {
                ViewGroup.LayoutParams p = btn_play_detail.getLayoutParams();
                p.width = dpToPx(40);
                p.height = dpToPx(40);
                btn_play_detail.setLayoutParams(p);
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
            if (v.getId() == btn_play_detail.getId())
                try {
                    if (!currentPlaying.equals(currentData.getPreviewUrl())) {
                        if (previewPlayedButton != null) {
                            previewPlayedButton.setImageDrawable(currentPlaying.contains("firebasestorage") ? ContextCompat.getDrawable(context, fromOpponent ? R.drawable.ic_content_play : R.drawable.ic_content_play_white) : ContextCompat.getDrawable(context, R.drawable.ic_play_circle));
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

    public class MyYoutubeContentHolder extends MessageContentHolder implements View.OnClickListener {
        YouTubeThumbnailView youtube_thumbnail_sender;
        TextView txt_content_sender_youtube;
        RelativeTimeTextView txt_time_sender_youtube;
        ImageView img_indicator_sender_youtube;

        public MyYoutubeContentHolder(View itemView) {
            super(itemView);
            this.youtube_thumbnail_sender = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail_sender);
            this.txt_content_sender_youtube = (TextView) itemView.findViewById(R.id.txt_content_sender_youtube);
            this.txt_time_sender_youtube = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender_youtube);
            this.img_indicator_sender_youtube = (ImageView) itemView.findViewById(R.id.img_indicator_sender_youtube);
            setIndicatorView(img_indicator_sender_youtube);
        }


        @Override
        public void bind(Message message) {
            super.bind(message);
            if (!TextUtils.isEmpty(message.getContent())) {
                txt_content_sender_youtube.setText(message.getContent());
                txt_content_sender_youtube.setVisibility(View.VISIBLE);
            } else
                txt_content_sender_youtube.setVisibility(View.GONE);
            final UserContent c = message.getAttachment();
            youtube_thumbnail_sender.initialize(getYoutubeApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(c.getVideoId());
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            youtube_thumbnail_sender.setOnClickListener(this);
            txt_time_sender_youtube.setReferenceTime(message.getTimeStamp());
        }

        @Override
        public void onClick(View v) {
            Intent youtubeIntent = new Intent(context, YoutubeViewActivity.class);
            youtubeIntent.putExtra("itemContent", attachment);
            context.startActivity(youtubeIntent);
        }
    }

    public class YourYoutubeContentHolder extends MessageContentHolder implements View.OnClickListener {
        YouTubeThumbnailView youtube_thumbnail_receiver;
        TextView txt_content_receiver_youtube;
        RelativeTimeTextView txt_time_receiver_youtube;

        public YourYoutubeContentHolder(View itemView) {
            super(itemView);
            this.youtube_thumbnail_receiver = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail_receiver);
            this.txt_content_receiver_youtube = (TextView) itemView.findViewById(R.id.txt_content_receiver_youtube);
            this.txt_time_receiver_youtube = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver_youtube);
        }


        @Override
        public void bind(Message message) {
            super.bind(message);
            if (!TextUtils.isEmpty(message.getContent())) {
                txt_content_receiver_youtube.setText(message.getContent());
                txt_content_receiver_youtube.setVisibility(View.VISIBLE);
            } else
                txt_content_receiver_youtube.setVisibility(View.GONE);
            final UserContent c = message.getAttachment();
            youtube_thumbnail_receiver.initialize(getYoutubeApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                    youTubeThumbnailLoader.setVideo(c.getVideoId());
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            youtube_thumbnail_receiver.setOnClickListener(this);
            txt_time_receiver_youtube.setReferenceTime(message.getTimeStamp());
        }

        @Override
        public void onClick(View v) {
            Intent youtubeIntent = new Intent(context, YoutubeViewActivity.class);
            youtubeIntent.putExtra("itemContent", attachment);
            context.startActivity(youtubeIntent);
        }
    }

    public class MyStickerContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_sender_sticker;
        ImageView img_sender_sticker;
        ImageView img_indicator_sender_sticker;

        public MyStickerContentHolder(View itemView) {
            super(itemView);
            txt_time_sender_sticker = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender_sticker);
            img_sender_sticker = (ImageView) itemView.findViewById(R.id.img_sender_sticker);
            img_indicator_sender_sticker = (ImageView) itemView.findViewById(R.id.img_indicator_sender_sticker);
            setIndicatorView(img_indicator_sender_sticker);
        }

        @Override
        public void bind(Message message) {
            super.bind(message);
            txt_time_sender_sticker.setReferenceTime(message.getTimeStamp());
            Glide.with(context).load(message.getAttachment().getContentUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_sender_sticker);
        }
    }

    public class YourStickerContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_receiver_sticker;
        ImageView img_receiver_sticker;

        public YourStickerContentHolder(View itemView) {
            super(itemView);
            txt_time_receiver_sticker = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver_sticker);
            img_receiver_sticker = (ImageView) itemView.findViewById(R.id.img_receiver_sticker);
        }

        @Override
        public void bind(Message message) {
            try {
                txt_time_receiver_sticker.setReferenceTime(message.getTimeStamp());
            } catch (Exception x) {

            }
            Glide.with(context).load(message.getAttachment().getContentUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_receiver_sticker);
        }
    }

}
