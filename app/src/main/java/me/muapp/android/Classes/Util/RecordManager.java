package me.muapp.android.Classes.Util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rulo on 5/04/17.
 */

public class RecordManager {
    public interface RecordManagerListener{
        void onRecordCompleted(String audioPath, int time);
        boolean checkPermissionVoice();
    }

    private static final int ANIMATION_DURATION = 250;

    private MediaRecorder mRecorder = null;
    private String mFileName = null;

    Timer countTimer = null;
    int time = 0;

    private View counterLayout;
    private TextView counter;
    private View slideToCancel;
    private View chatInputVoiceRecord;
    private View button;
    private RecordManagerListener listener;

    //Dragging management
    boolean dragging = false;
    private int halfScreenWidth;
    private float startX;
    private float lastTouchX;
    private float totalDx = 0;

    public RecordManager(@NonNull final View button,
                         @NonNull View counterLayout,
                         @NonNull final TextView counter,
                         @NonNull final TextView slideToCancel,
                         @NonNull final View chatInputVoiceRecord,
                         @NonNull final File storageDir,
                         @NonNull final RecordManagerListener listener) {
        this.button = button;
        this.counterLayout = counterLayout;
        this.counter = counter;
        this.slideToCancel = slideToCancel;
        this.chatInputVoiceRecord = chatInputVoiceRecord;
        this.listener = listener;

        createTempFile(storageDir);
        halfScreenWidth = Utils.getScreenWidth(button.getContext())/2;

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if( listener.checkPermissionVoice()) {
                        dragging = true;
                        vibrate();
                        chatInputVoiceRecord.setAlpha(0.7f);
                        button.setAlpha(0.7f);
                        counter.setVisibility(View.VISIBLE);
                        slideToCancel.setTranslationX(0);
                        slideToCancel.setVisibility(View.VISIBLE);

                        startX = motionEvent.getRawX();
                        lastTouchX = startX;
                        totalDx = 0;

                        createTempFile(storageDir);
                        startRecord();
                    }

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                    if( dragging) {
                        dragging = false;
                        chatInputVoiceRecord.setAlpha(1f);
                        button.setAlpha(1f);
                        stopRecord();
                    }

                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    if( dragging) {
                        final float x = motionEvent.getRawX();

                        if (x < halfScreenWidth) {
                            dragging = false;
                            chatInputVoiceRecord.setAlpha(1f);
                            button.setAlpha(1f);
                            cancelRecord();
                        } else {
                            final float dx = x - lastTouchX;
                            totalDx += dx;
                            if (totalDx > 0) {
                                totalDx = 0;
                            }
                            slideToCancel.setTranslationX(totalDx);
                            chatInputVoiceRecord.setTranslationX(totalDx);
                            lastTouchX = x;
                        }
                    }

                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void vibrate(){
        Vibrator v = (Vibrator) button.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(ANIMATION_DURATION);
    }

    private void createTempFile(File storageDir){

        if( mFileName == null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "voicerecord_" + timeStamp + "_";

            if( !storageDir.exists()) {
                storageDir.mkdir();
            }
            try {
                File audio = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".mp4",         /* suffix */
                        storageDir      /* directory */
                );
                audio.deleteOnExit();
                mFileName = audio.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                //TODO manage error
            }
        }
    }

    public void onPause() {
        stopCount(false);
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void startRecord() {
        time = -1;

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(256);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);

        try {
            mRecorder.prepare();
            counter.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mRecorder != null) {
                        mRecorder.start();
                        startCount();
                    }
                }
            }, ANIMATION_DURATION);

        } catch (IOException e) {
            e.printStackTrace();
            //TODO manage error
        }

    }

    private void stopRecord() {

        stopMediaRecorder();
        counterLayout.post(new Runnable() {
            @Override
            public void run() {
                counterLayout.setVisibility(View.GONE);
            }
        });
        stopCount(true);
    }

    private void stopMediaRecorder(){
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void cancelRecord(){
        stopMediaRecorder();
        vibrate();
        counterLayout.post(new Runnable() {
            @Override
            public void run() {
                counter.setVisibility(View.GONE);
                slideToCancel.setVisibility(View.GONE);
                chatInputVoiceRecord.animate().translationX(0).setDuration(150).start();

            }
        });
        counterLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                counterLayout.setVisibility(View.GONE);
            }
        },150);
        stopCount(false);
    }

    private void startCount() {
        stopCount(false);
        time = -1;
        countTimer = new Timer();
        countTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                counter.post(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        counterLayout.setVisibility(View.VISIBLE);
                        counter.setText(getTimeText());
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopCount(boolean success) {
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }

        if( success && time > 0){
            listener.onRecordCompleted(mFileName, time);
        }
    }

    private String getTimeText() {
        String res = "";
        int m = time / 60;
        if (m < 10) {
            res += "0";
        }
        res += m + ":";
        m = time % 60;
        if (m < 10) {
            res += 0;
        }
        res += m;
        return res;
    }
}
