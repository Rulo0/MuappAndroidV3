package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.content.model.QBFile;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.QuickbloxHelper;
import me.muapp.android.Classes.Quickblox.cache.MessageCacheHelper;
import me.muapp.android.Classes.Quickblox.cache.MessageCacheObject;
import me.muapp.android.Classes.Util.FileCache;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;

/**
 * Created by rulo on 6/04/17.
 */

public class PokeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int POKE_MESSAGE_SENT = 0;
    private static final int POKE_MESSAGE_RECEIVED = 1;
    private static final int POKE_IMAGE_SENT = 2;
    private static final int POKE_IMAGE_RECEIVED = 3;
    private static final int POKE_STICKER_SENT = 4;
    private static final int POKE_STICKER_RECEIVED = 5;
    private static final int POKE_DATE = 6;
    private static final int POKE_VOICE_SENT = 7;
    private static final int POKE_VOICE_RECEIVED = 8;

    public interface PokeAdapterListener {
        void onPokeReaded(MessageCacheObject poke);
    }

    private MuappApplication application;

    private PokeAdapterListener listener;
    private List<MessageCacheObject> pokes = new ArrayList<>();

    private VoiceNotesPlayer voiceNotesPlayer;
    private FileCache voiceNotesCache;
    private Utils utils;
    Context context;

    public PokeAdapter(MuappApplication application, Context context, PokeAdapterListener listener) {
        this.application = application;
        this.context = context;
        this.listener = listener;
        this.utils = new Utils();
        voiceNotesCache = new FileCache(context, QuickbloxHelper.VOICE_NOTES_CACHE_DIRECTORY);
        voiceNotesPlayer = new VoiceNotesPlayer();
    }

    public void release() {
        voiceNotesPlayer.release();

    }

    public List<MessageCacheObject> getPokes() {
        return pokes;
    }


    public void addPokes(List<MessageCacheObject> newPokes) {
        if (newPokes != null) {
            ArrayList<MessageCacheObject> pokesArray = new ArrayList<>(newPokes);
            addDateHeaders(pokesArray);
            pokes = pokesArray;
            notifyDataSetChanged();
        }
    }//addPokes

    public long getLastPokeSentTime() {
        if (pokes.size() == 0) {
            return Long.MAX_VALUE;
        } else {
            return pokes.get(0).getDateSent();
        }
    }

    private void addDateHeaders(ArrayList<MessageCacheObject> array) {
        int i = 0;
        Calendar previous = null;
        MessageCacheObject poke;
        while (i < array.size()) {
            poke = array.get(i);
            if (previous == null) {
                if (!(poke.getBody() != null && poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_DATE))) {
                    previous = Calendar.getInstance();
                    previous.setTimeInMillis(poke.getDateSent() * 1000);
                    MessageCacheObject msg = new MessageCacheObject();
                    msg.setBody(QuickbloxHelper.POKE_DATE);
                    msg.setDateSent(poke.getDateSent());
                    array.add(i, msg);
                    i++;
                } else { //update time
                    if (array.size() > i + 1) {
                        poke.setDateSent(array.get(i + 1).getDateSent());
                        previous = Calendar.getInstance();
                        previous.setTimeInMillis(poke.getDateSent() * 1000);
                        i++;
                    } else {
                        previous = Calendar.getInstance();
                        previous.setTimeInMillis(poke.getDateSent() * 1000);
                        i++;
                    }
                }
            } else {
                Calendar pokeTime = Calendar.getInstance();
                pokeTime.setTimeInMillis(poke.getDateSent() * 1000);
                if (pokeTime.get(Calendar.DAY_OF_YEAR) != (previous.get(Calendar.DAY_OF_YEAR))) {
                    if (!(poke.getBody() != null && poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_DATE))) {
                        MessageCacheObject msg = new MessageCacheObject();
                        msg.setBody(QuickbloxHelper.POKE_DATE);
                        msg.setDateSent(poke.getDateSent());
                        array.add(i, msg);
                        i++;
                        previous = pokeTime;
                    } else { //update time
                        if (array.size() > i + 1) {
                            poke.setDateSent(array.get(i + 1).getDateSent());
                            previous = Calendar.getInstance();
                            previous.setTimeInMillis(poke.getDateSent() * 1000);
                        }
                        i++;
                    }
                } else if (poke.getBody() != null && poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_DATE)) {
                    array.remove(i);
                    i--;
                }
            }
            i++;
        }
    }//addDateHeaders

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case POKE_MESSAGE_SENT:
                View v1 = inflater.inflate(R.layout.poke_bubble_right, parent, false);
                viewHolder = new TextPokeViewHolder(v1);
                break;
            case POKE_MESSAGE_RECEIVED:
                View v2 = inflater.inflate(R.layout.poke_bubble_left, parent, false);
                viewHolder = new TextPokeViewHolder(v2);
                break;
            case POKE_IMAGE_SENT:
                View v3 = inflater.inflate(R.layout.poke_bubble_image_right, parent, false);
                viewHolder = new ImagePokeViewHolder(v3);
                break;
            case POKE_IMAGE_RECEIVED:
                View v4 = inflater.inflate(R.layout.poke_bubble_image_left, parent, false);
                viewHolder = new ImagePokeViewHolder(v4);
                break;
            case POKE_STICKER_SENT:
                View v5 = inflater.inflate(R.layout.poke_bubble_sticker_right, parent, false);
                viewHolder = new StickerPokeViewHolder(v5);
                break;
            case POKE_STICKER_RECEIVED:
                View v6 = inflater.inflate(R.layout.poke_bubble_sticker_left, parent, false);
                viewHolder = new StickerPokeViewHolder(v6);
                break;
            case POKE_DATE:
                View v7 = inflater.inflate(R.layout.poke_bubble_date, parent, false);
                viewHolder = new DatePokeViewHolder(v7);
                break;
            case POKE_VOICE_SENT:
                View v10 = inflater.inflate(R.layout.poke_bubble_voice_right, parent, false);
                viewHolder = new VoicePokeViewHolder(v10, true);
                break;
            case POKE_VOICE_RECEIVED:
                View v11 = inflater.inflate(R.layout.poke_bubble_voice_left, parent, false);
                viewHolder = new VoicePokeViewHolder(v11, false);
                break;
        }
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MessageCacheObject poke = this.pokes.get(position);
        int viewType = holder.getItemViewType();
        switch (viewType) {

            case POKE_MESSAGE_SENT:
                TextPokeViewHolder vh1 = (TextPokeViewHolder) holder;
                vh1.pokeText.setText(poke.getBody());
                vh1.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));
                setStateIcon(poke, vh1.pokeStatus);
                break;
            case POKE_MESSAGE_RECEIVED:
                TextPokeViewHolder vh2 = (TextPokeViewHolder) holder;
                vh2.pokeText.setText(poke.getBody());
                vh2.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));

                if (!poke.isReadByMe() && listener != null) {
                    listener.onPokeReaded(poke);
                }
                break;
            case POKE_IMAGE_SENT: {
                final ImagePokeViewHolder vh3 = (ImagePokeViewHolder) holder;
                vh3.pokeImage.setImageDrawable(null);
                vh3.progressBar.setVisibility(View.VISIBLE);

                final String url;
                if (poke.getAttachmentId() != null) {
                    url = QBFile.getPrivateUrlForUID(poke.getAttachmentId());
                } else {
                    url = poke.getAttachmentUrl();
                }

                Glide.with(context).load(url).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().into(vh3.pokeImage);

                vh3.pokeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // chatActivity.onImageClicked(url);
                    }
                });

                vh3.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));
                setStateIcon(poke, vh3.pokeStatus);
                break;
            }
            case POKE_IMAGE_RECEIVED: {
                final ImagePokeViewHolder vh4 = (ImagePokeViewHolder) holder;
                vh4.pokeImage.setImageDrawable(null);
                vh4.progressBar.setVisibility(View.VISIBLE);

                final String url;
                if (poke.getAttachmentId() != null) {
                    url = QBFile.getPrivateUrlForUID(poke.getAttachmentId());
                } else {
                    url = poke.getAttachmentUrl();
                }

                vh4.pokeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  chatActivity.onImageClicked(url);
                    }
                });

                Glide.with(context).load(url).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().into(vh4.pokeImage);
                vh4.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));

                if (!poke.isReadByMe() && listener != null) {
                    listener.onPokeReaded(poke);
                }
                break;
            }
            case POKE_STICKER_SENT:
                StickerPokeViewHolder vh5 = (StickerPokeViewHolder) holder;

                String stickerUrl = poke.getAttachmentUrl();
                vh5.pokeImage.setImageDrawable(null);
                Glide.with(context).load(stickerUrl).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().into(vh5.pokeImage);
                vh5.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));
                setStateIcon(poke, vh5.pokeStatus);

                break;
            case POKE_STICKER_RECEIVED:
                StickerPokeViewHolder vh6 = (StickerPokeViewHolder) holder;

                String stickerUrl2 = poke.getAttachmentUrl();
                vh6.pokeImage.setImageDrawable(null);
                Glide.with(context).load(stickerUrl2).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().into(vh6.pokeImage);
                vh6.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));
                if (!poke.isReadByMe() && listener != null) {
                    listener.onPokeReaded(poke);
                }
                break;
            case POKE_DATE:
                DatePokeViewHolder vh7 = (DatePokeViewHolder) holder;
                vh7.pokeDate.setText(this.convertToDatePokeDayName(poke.getDateSent() * 1000));
                break;
            case POKE_VOICE_SENT: {
                final VoicePokeViewHolder vh = (VoicePokeViewHolder) holder;
                vh.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));
                setStateIcon(poke, vh.pokeStatus);

                if (vh.task != null) {
                    vh.task.setListener(null);
                }
                vh.prepareVoiceNote(poke.getAttachmentId(), poke.getAttachmentUrl(), poke.getAttachmentSize());
                break;
            }
            case POKE_VOICE_RECEIVED: {
                final VoicePokeViewHolder vh = (VoicePokeViewHolder) holder;
                vh.pokeHour.setText(this.convertToPokeHour(poke.getDateSent() * 1000));

                if (vh.task != null) {
                    vh.task.setListener(null);
                }
                vh.prepareVoiceNote(poke.getAttachmentId(), poke.getAttachmentUrl(), poke.getAttachmentSize());
                if (!poke.isReadByMe() && listener != null) {
                    listener.onPokeReaded(poke);
                }
                break;
            }

        }
    }//onBindViewHolder

    private void setStateIcon(MessageCacheObject poke, ImageView iv) {
        if (!poke.isSent()) { //Not sent
            iv.setImageDrawable(null);
        } else if (MessageCacheHelper.isMessageReadedByOpponent(poke)) {
            iv.setImageResource(R.drawable.ic_chat_indicator_read);
        } else if (MessageCacheHelper.isMessageDelivered(poke)) {
            iv.setImageResource(R.drawable.ic_chat_indicator_received);
        } else {
            iv.setImageResource(R.drawable.ic_chat_indicator_sent);
        }
    }//setStateIcon

    @Override
    public int getItemViewType(int position) {
        MessageCacheObject poke = pokes.get(position);
        if (poke.getBody() != null && poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_DATE)) {
            return POKE_DATE;
        } else if (poke.getAttachmentType() != null) {
            if (poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_IMAGE)) {
                if (isSentMessage(poke)) {
                    return POKE_IMAGE_SENT;
                } else {
                    return POKE_IMAGE_RECEIVED;
                }
            } else if (poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_VOICE)) {
                if (isSentMessage(poke)) {
                    return POKE_VOICE_SENT;
                } else {
                    return POKE_VOICE_RECEIVED;
                }
            } else if (poke.getBody().equalsIgnoreCase(QuickbloxHelper.POKE_STICKER)) {
                if (isSentMessage(poke)) {
                    return POKE_STICKER_SENT;
                } else {
                    return POKE_STICKER_RECEIVED;
                }
            }
        } else if (poke.getBody() != null && poke.getBody().length() > 0) { //message
            if (isSentMessage(poke)) {
                return POKE_MESSAGE_SENT;
            } else {
                return POKE_MESSAGE_RECEIVED;
            }
        }
        return POKE_MESSAGE_SENT;
    }//getItemViewType

    @Override
    public int getItemCount() {
        return this.pokes.size();
    }

    private String convertToDatePokeDayName(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        //simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    private String convertToPokeHour(long date) {
        DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        //simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    private boolean isSentMessage(MessageCacheObject poke) {

        return poke.getSenderId() != null &&
                poke.getSenderId().intValue() == QuickBloxChatHelper.getInstance().getCurrentUserId();

    }

    //------- View holders ---------------------------------------------------------------------

    private interface OnVoiceDownloadedListener {
        void onVoiceDownloaded(FileDescriptor fd);
    }

    private class VoiceDownloaderTask extends AsyncTask<String, Void, FileDescriptor> {

        OnVoiceDownloadedListener listener;

        VoiceDownloaderTask(OnVoiceDownloadedListener listener) {
            this.listener = listener;
        }

        public void setListener(OnVoiceDownloadedListener listener) {
            this.listener = listener;
        }

        @Override
        protected FileDescriptor doInBackground(String... params) {
            Log.wtf("VoiceDownloaderTask", params[0]);
            String url = QBFile.getPrivateUrlForUID(params[0]);
            Log.wtf("VoiceDownloaderTask", url);
            if (voiceNotesCache.downloadUrlToCache(params[0], url)) {
                return voiceNotesCache.getFileFromCache(params[0]);
            }
            return null;
        }

        protected void onPostExecute(FileDescriptor fd) {
            if (listener != null) {
                listener.onVoiceDownloaded(fd);
            }
        }
    }//VoiceDownloaderTask

    private class TextPokeViewHolder extends RecyclerView.ViewHolder {
        TextView pokeText;
        TextView pokeHour;
        ImageView pokeStatus;

        TextPokeViewHolder(View itemView) {
            super(itemView);
            this.pokeText = (TextView) itemView.findViewById(R.id.poke_text);
            this.pokeHour = (TextView) itemView.findViewById(R.id.poke_hour);
            this.pokeStatus = (ImageView) itemView.findViewById(R.id.poke_status);
        }
    }

    private class ImagePokeViewHolder extends RecyclerView.ViewHolder {
        ImageView pokeImage;
        ImageView pokeStatus;
        TextView pokeHour;
        ProgressBar progressBar;

        ImagePokeViewHolder(View itemView) {
            super(itemView);
            this.pokeImage = (ImageView) itemView.findViewById(R.id.poke_image);
            this.pokeStatus = (ImageView) itemView.findViewById(R.id.poke_status);
            this.pokeHour = (TextView) itemView.findViewById(R.id.poke_hour);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.indeterminateprogress);
        }
    }

    private class StickerPokeViewHolder extends RecyclerView.ViewHolder {
        ImageView pokeImage;
        ImageView pokeStatus;
        TextView pokeHour;

        StickerPokeViewHolder(View itemView) {
            super(itemView);
            this.pokeImage = (ImageView) itemView.findViewById(R.id.poke_image);
            this.pokeStatus = (ImageView) itemView.findViewById(R.id.poke_status);
            this.pokeHour = (TextView) itemView.findViewById(R.id.poke_hour);
        }
    }

    private class DatePokeViewHolder extends RecyclerView.ViewHolder {
        TextView pokeDate;

        DatePokeViewHolder(View itemView) {
            super(itemView);
            this.pokeDate = (TextView) itemView.findViewById(R.id.poke_date);

        }
    }

    private class VoicePokeViewHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener, VoiceNotesPlayerListener {

        VoiceDownloaderTask task = null;
        ImageButton pokePlay;
        TextView pokeHour;
        ProgressBar progressBar;
        ImageView pokeStatus;
        TextView pokeDuration;
        SeekBar seekbar;

        FileDescriptor audio = null;
        String audioAttachmentId = null;
        boolean sent;

        VoicePokeViewHolder(View itemView, boolean sent) {
            super(itemView);
            this.sent = sent;
            this.pokePlay = (ImageButton) itemView.findViewById(R.id.poke_play);
            this.pokeHour = (TextView) itemView.findViewById(R.id.poke_hour);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.indeterminateprogress);
            this.pokeStatus = (ImageView) itemView.findViewById(R.id.poke_status);
            this.pokeDuration = (TextView) itemView.findViewById(R.id.poke_duration);
            this.seekbar = (SeekBar) itemView.findViewById(R.id.poke_seek);
            seekbar.setOnSeekBarChangeListener(this);
            voiceNotesPlayer.addListener(this);
        }

        private void prepareVoiceNote(final String attachmentId, final String attachmentUrl, double attachmentSize) {

            pokeDuration.setText(utils.secondToTimeFormat((int) attachmentSize));

            audioAttachmentId = attachmentId;
            audio = getFileDescriptor(attachmentId, attachmentUrl);
            if (audio == null) {
                seekbar.setEnabled(false);
            } else {
                seekbar.setEnabled(true);
            }
            seekbar.setMax((int) attachmentSize);
            seekbar.setProgress(0);

            if (sent) {
                pokePlay.setImageResource(R.drawable.ic_chat_play_red);
            } else {
                pokePlay.setImageResource(R.drawable.ic_chat_play_green);
            }
            pokePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean playingMe = voiceNotesPlayer.isPlaying(audio, audioAttachmentId);
                    voiceNotesPlayer.stop();

                    if (playingMe) {
                        if (sent) {
                            pokePlay.setImageResource(R.drawable.ic_chat_play_red);
                        } else {
                            pokePlay.setImageResource(R.drawable.ic_chat_play_green);
                        }
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        audio = getFileDescriptor(attachmentId, attachmentUrl);
                        if (audio == null && attachmentId != null) { //download
                            task = new VoiceDownloaderTask(new OnVoiceDownloadedListener() {
                                @Override
                                public void onVoiceDownloaded(FileDescriptor fd) {
                                    audio = fd;
                                    seekbar.setEnabled(fd != null);
                                    if (fd == null) {
                                    } else {
                                        playVoiceNote();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, attachmentId);
                        } else if (audio != null) {
                            playVoiceNote();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }//prepareVoiceNote

        private FileDescriptor getFileDescriptor(String attachmentId, String attachmentUrl) {
            FileDescriptor fd;
            if (attachmentId == null) {
                try {
                    fd = new FileInputStream(new File(attachmentUrl)).getFD();
                } catch (IOException e) {
                    e.printStackTrace();
                    fd = null;
                }
            } else {
                fd = voiceNotesCache.getFileFromCache(attachmentId);
            }
            return fd;
        }

        private void playVoiceNote() {
            if (sent) {
                pokePlay.setImageResource(R.drawable.ic_chat_pause_red);
            } else {
                pokePlay.setImageResource(R.drawable.ic_chat_pause_green);
            }
            voiceNotesPlayer.play(audio, audioAttachmentId, seekbar.getProgress());
        }//playVoiceNote


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (voiceNotesPlayer.isPlaying(audio, audioAttachmentId)) {
                voiceNotesPlayer.removeUpdates();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (voiceNotesPlayer.isPlaying(audio, audioAttachmentId)) {
                voiceNotesPlayer.seekTo(seekBar.getProgress());
            }
        }


        @Override
        public void onCompletion(FileDescriptor file, String attachmentId) {
            if (voiceNotesPlayer.isSameAudio(audio, audioAttachmentId, file, attachmentId)) {
                seekbar.setProgress(0);
                if (sent) {
                    pokePlay.setImageResource(R.drawable.ic_chat_play_red);
                } else {
                    pokePlay.setImageResource(R.drawable.ic_chat_play_green);
                }
            }
        }

        @Override
        public void onPlaying(FileDescriptor file, String attachmentId, int milisecond) {
            if (voiceNotesPlayer.isSameAudio(audio, audioAttachmentId, file, attachmentId)) {
                if (sent) {
                    pokePlay.setImageResource(R.drawable.ic_chat_pause_red);
                } else {
                    pokePlay.setImageResource(R.drawable.ic_chat_pause_green);
                }
                seekbar.setProgress(milisecond / 1000);
            }
        }

        @Override
        public void onPause(FileDescriptor file, String attachmentId) {
            if (voiceNotesPlayer.isSameAudio(audio, audioAttachmentId, file, attachmentId)) {
                if (sent) {
                    pokePlay.setImageResource(R.drawable.ic_chat_play_red);
                } else {
                    pokePlay.setImageResource(R.drawable.ic_chat_play_green);
                }
            }
        }

        @Override
        public void onError(FileDescriptor file, String attachmentId) {
            if (voiceNotesPlayer.isSameAudio(audio, audioAttachmentId, file, attachmentId)) {
                if (sent) {
                    pokePlay.setImageResource(R.drawable.ic_chat_play_red);
                } else {
                    pokePlay.setImageResource(R.drawable.ic_chat_play_green);
                }
            }
        }
    }//VoicePokeViewHolder

    interface VoiceNotesPlayerListener {
        void onCompletion(FileDescriptor file, String attachmentId);

        void onPlaying(FileDescriptor file, String attachmentId, int milisecond);

        void onPause(FileDescriptor file, String attachmentId);

        void onError(FileDescriptor file, String attachmentId);
    }

    /**
     *
     */
    private class VoiceNotesPlayer {

        MediaPlayer player;
        FileDescriptor nowPlaying;
        String nowPlayingId;
        Handler mHandler;

        ArrayList<VoiceNotesPlayerListener> listeners = new ArrayList<VoiceNotesPlayerListener>();

        private Runnable mUpdateTimeTask = new Runnable() {
            public void run() {
                notifyPLaying(player.getCurrentPosition());
                mHandler.postDelayed(this, 100);
            }
        };

        VoiceNotesPlayer() {
            mHandler = new Handler();
            player = new MediaPlayer();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    notifyCompletion();
                }
            });
        }

        void release() {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }
                removeUpdates();
                player.setOnCompletionListener(null);
                player.setOnPreparedListener(null);
                player.release();
                player = null;
            }
        }

        void removeUpdates() {
            mHandler.removeCallbacks(mUpdateTimeTask);
        }

        public void addListener(VoiceNotesPlayerListener listener) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        private void notifyCompletion() {
            for (VoiceNotesPlayerListener l : listeners) {
                l.onCompletion(nowPlaying, nowPlayingId);
            }
        }

        private void notifyError() {
            for (VoiceNotesPlayerListener l : listeners) {
                l.onError(nowPlaying, nowPlayingId);
            }
        }

        private void notifyPause() {
            for (VoiceNotesPlayerListener l : listeners) {
                l.onPause(nowPlaying, nowPlayingId);
            }
        }

        private void notifyPLaying(int ms) {
            for (VoiceNotesPlayerListener l : listeners) {
                l.onPlaying(nowPlaying, nowPlayingId, ms);
            }
        }

        boolean isPlaying(FileDescriptor fd, String attachmentId) {
            return isSameAudio(fd, attachmentId, nowPlaying, nowPlayingId) &&
                    player.isPlaying();
        }

        void stop() {
            if (player.isPlaying()) {
                player.stop();
                player.reset();
                mHandler.removeCallbacks(mUpdateTimeTask);
                notifyPause();
            }
        }

        void seekTo(int seconds) {
            if (player.isPlaying()) {
                player.seekTo(seconds * 1000);
                updateProgress();
            }
        }

        private void updateProgress() {
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }

        public void play(FileDescriptor fd, String attachmentId, final int second) {
            nowPlaying = fd;
            nowPlayingId = attachmentId;
            stop();
            try {
                player.setDataSource(fd);
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.seekTo(second * 1000);
                        player.start();
                        updateProgress();
                    }
                });
            } catch (IOException e) {
                notifyError();
                e.printStackTrace();
            }
        }

        boolean isSameAudio(FileDescriptor fd1, String id1,
                            FileDescriptor fd2, String id2) {
            if (fd1 != null && fd1 == fd2) {
                return true;
            } else if (id1 != null && id2 != null &&
                    id1.equalsIgnoreCase(id2)) {
                return true;
            } else {
                return false;
            }
        }
    }//VoiceNotesPlayer
}
