package me.muapp.android.UI.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eralp.circleprogressview.CircleProgressView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

public class AddVoiceNoteActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static final String AUDIO_RECORDER_FILE_EXT_M4A = ".m4a";
    private static final String AUDIO_RECORDER_FOLDER = "Muapp/voiceNotes";
    private static final int REQUEST_MEDIA = 456;
    private MediaRecorder recorder = null;
    Vibrator v;
    ImageButton imb_record_voice;
    private File thisFile;
    CountDownTimer voiceCountdown;
    CircleProgressView mCircleProgressView;
    RelativeLayout voice_record_button_container, container_voice_controls;
    ImageButton voicenote_play;
    ImageButton voicenote_drop;
    MediaPlayer mediaPlayer;
    EditText et_voicenote_comment;
    TextView txt_hold_to_record;
    LinearLayout container_write_comment_voicenote;

    private void logEvent(String bundleAction) {
        Bundle voicenoteBundle = new Bundle();
        voicenoteBundle.putString(Analytics.MyProfileVoiceNote.MY_PROFILE_VOICENOTE_PROPERTY.Action.toString(), bundleAction);
        mFirebaseAnalytics.logEvent(Analytics.MyProfileVoiceNote.MY_PROFILE_VOICENOTE_EVENT.My_Profile_VoiceNote.toString(), voicenoteBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voicenote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mediaPlayer = new MediaPlayer();
        imb_record_voice = (ImageButton) findViewById(R.id.imb_record_voice);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        txt_hold_to_record = (TextView) findViewById(R.id.txt_hold_to_record);
        container_write_comment_voicenote = (LinearLayout) findViewById(R.id.container_write_comment_voicenote);
        et_voicenote_comment = (EditText) findViewById(R.id.et_voicenote_comment);
        mCircleProgressView = (CircleProgressView) findViewById(R.id.circle_progress_view);
        voice_record_button_container = (RelativeLayout) findViewById(R.id.voice_record_button_container);
        container_voice_controls = (RelativeLayout) findViewById(R.id.container_voice_controls);
        voicenote_play = (ImageButton) findViewById(R.id.voicenote_play);
        voicenote_drop = (ImageButton) findViewById(R.id.voicenote_drop);
        imb_record_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v("RECORDING", "Start");
                        if (checkAndRequestPermissions()) {
                            startRecording();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        Log.v("RECORDING", "Stop");
                        break;
                }
                return false;
            }
        });
    }


    private void showCommentLayout(Boolean showComment) {
        container_write_comment_voicenote.setVisibility(showComment ? View.VISIBLE : View.GONE);
        txt_hold_to_record.setVisibility(showComment ? View.GONE : View.VISIBLE);
    }

    private void startRecording() {
        logEvent(Analytics.MyProfileVoiceNote.MY_PROFILE_VOICENOTE_ACTION.Record.toString());
        if (thisFile != null) {
            thisFile.delete();
        }

        v.vibrate(100);
        final Float Length = 20000f;
        final Float Period = 1f;
        voiceCountdown = new CountDownTimer(Length.longValue(), Period.longValue()) {
            @Override
            public void onTick(long millisUntilFinished_) {
                mCircleProgressView.setProgress(((Length - millisUntilFinished_) * 100) / Length);
            }

            @Override
            public void onFinish() {
                stopRecording();
                // do whatever when tehe bar is full
            }
        }.start();
        thisFile = new File(getFilename());
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(thisFile.getAbsolutePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(256);
        recorder.setAudioChannels(1);
        recorder.setAudioSamplingRate(44100);
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.v("RECORDING", "Error: " + what + ", " + extra);
            }
        });
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Log.v("RECORDING", "Error: " + what + ", " + extra);
            }
        });
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (voiceCountdown != null)
            voiceCountdown.cancel();
        try {
            if (recorder != null) {
                mCircleProgressView.setProgressWithAnimation(0, 300);
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                v.vibrate(100);
                voice_record_button_container.setVisibility(View.VISIBLE);
                showCommentLayout(true);
                voicenote_play.setOnClickListener(getButtonListener());
                voicenote_drop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logEvent(Analytics.MyProfileVoiceNote.MY_PROFILE_VOICENOTE_ACTION.Delete.toString());
                        if (thisFile != null) {
                            thisFile.delete();
                            thisFile = null;
                        }
                        voice_record_button_container.setVisibility(View.GONE);
                        showCommentLayout(false);
                    }
                });

            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private View.OnClickListener getButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(thisFile.getAbsolutePath());
                    mediaPlayer.setOnPreparedListener(AddVoiceNoteActivity.this);
                    mediaPlayer.setOnCompletionListener(AddVoiceNoteActivity.this);
                    mediaPlayer.prepareAsync();
                } catch (Exception x) {
                }
            }
        };


    }


    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File newfile = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!newfile.exists()) {
            newfile.mkdirs();
        }
        String fname = (newfile.getAbsolutePath() + "/" + "voiceNote" + new Date().getTime() + AUDIO_RECORDER_FILE_EXT_M4A);
        File f = new File(fname);
        if (f.exists())
            f.delete();
        return fname;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_MEDIA:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // startRecording();
                    } else {
                        checkAndRequestPermissions();
                    }
                } else {
                    Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                            .show();

                }
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionAudio != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Camera Needed");
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Write Needed");
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Read Needed");
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MEDIA);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_publish:
                publishVoiceNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishVoiceNote() {
        logEvent(Analytics.MyProfileVoiceNote.MY_PROFILE_VOICENOTE_ACTION.Publish.toString());
        Bundle publishBundle = new Bundle();
        if (!TextUtils.isEmpty(et_voicenote_comment.getText().toString()))
            publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Comment.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Audio.toString());
        publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Publish.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Audio.toString());
        mFirebaseAnalytics.logEvent(Analytics.My_Profile_Add.MY_PROFILE_ADD_EVENT.My_Profile_Add_Type.toString(), publishBundle);

        final StorageReference mainReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(loggedUser.getId())).child("post-audios").child("audio" + new Date().getTime());
        if (thisFile != null) {
            int size = (int) thisFile.length();
            byte[] bytes = new byte[size];
            try {
                showProgressDialog();
                final UserContent thisContent = new UserContent();
                thisContent.setComment(et_voicenote_comment.getText().toString());
                thisContent.setCreatedAt(new Date().getTime());
                thisContent.setLikes(0);
                thisContent.setCatContent("contentAud");
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(thisFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                UploadTask uploadTask = mainReference.putBytes(bytes);
                final long startTime = System.currentTimeMillis();
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Long transfered = taskSnapshot.getBytesTransferred();
                        @SuppressWarnings("VisibleForTests") Long total = taskSnapshot.getTotalByteCount();
                    }
                });
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getDownloadUrl();
                            thisContent.setContentUrl(downloadUrl.toString());
                            thisContent.setStorageName(mainReference.getPath());
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(MuappApplication.DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
                            String key = ref.push().getKey();
                            ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    long stopTime = System.currentTimeMillis();
                                    long elapsedTime = stopTime - startTime;
                                    preferenceHelper.putAddedContentEnabled();
                                    hideProgressDialog();
                                    thisFile.delete();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            YoYo.with(Techniques.Wobble)
                    .duration(700)
                    .playOn(container_voice_controls);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        voicenote_play.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        voicenote_play.setImageResource(R.drawable.ic_pause);
    }
}
