package me.muapp.android.UI.Activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Internal.UserMedia;
import me.muapp.android.R;

import static com.bumptech.glide.Glide.with;
import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class AddPhotosDetailActivity extends BaseActivity {
    public static final String CURRENT_MEDIA = "CURRENT_MEDIA";
    EditText et_media_comment;
    UserMedia currentMedia;
    VideoView vv_video_detail;
    ImageView img_photo_detail;
    StorageReference mainReference;
    public static final int PHOTOS_REQUEST = 711;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_photo_detail = (ImageView) findViewById(R.id.img_photo_detail);
        vv_video_detail = (VideoView) findViewById(R.id.vv_video_detail);
        et_media_comment = (EditText) findViewById(R.id.et_media_comment);
        if ((currentMedia = getIntent().getParcelableExtra(CURRENT_MEDIA)) == null) {
            finish();
        }
        loadData(currentMedia);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vv_video_detail.isPlaying()) {
            vv_video_detail.stopPlayback();
        }
    }

    private void loadData(UserMedia currentMedia) {
        if (currentMedia.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            if (img_photo_detail.getVisibility() != View.VISIBLE)
                img_photo_detail.setVisibility(View.VISIBLE);
            if (vv_video_detail.getVisibility() != View.GONE)
                vv_video_detail.setVisibility(View.GONE);
            if (currentMedia.getPath() != null) {
                with(this).load(currentMedia.getPath()).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_photo_detail);
            } else {
                with(this).load(currentMedia.getUri()).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img_photo_detail);
            }
        } else if (currentMedia.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            if (vv_video_detail.getVisibility() != View.VISIBLE)
                vv_video_detail.setVisibility(View.VISIBLE);
            if (img_photo_detail.getVisibility() != View.GONE)
                img_photo_detail.setVisibility(View.GONE);
            vv_video_detail.setVideoPath(currentMedia.getPath());
            vv_video_detail.setMediaController(new MediaController(this));
            vv_video_detail.requestFocus();
            vv_video_detail.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_publish:
                publishThisMedia();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishThisMedia() {
        showProgressDialog();
        final UserContent thisContent = new UserContent();
        thisContent.setComment(et_media_comment.getText().toString());
        thisContent.setCreatedAt(new Date().getTime());
        thisContent.setLikes(0);
        mainReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(loggedUser.getId()));
        if (currentMedia.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            mainReference = mainReference.child("post-images");
            thisContent.setCatContent("contentPic");
        } else {
            mainReference = mainReference.child("post-videos");
            thisContent.setCatContent("contentVid");
        }

        mainReference = mainReference.child("media" + new Date().getTime());
        if (currentMedia.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            Glide.with(this)
                    .load(currentMedia.getPath())
                    .asBitmap()
                    .toBytes(Bitmap.CompressFormat.JPEG, 85)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .atMost()
                    .override(400, 400) // 1280 I guess
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE) // read it from cache
                    .skipMemoryCache(true) // don't save in memory, needed only once
                    .into(new SimpleTarget<byte[]>() {
                        @Override
                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
                            UploadTask uploadTask = mainReference.putBytes(resource);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getDownloadUrl();
                                        thisContent.setContentUrl(downloadUrl.toString());
                                        thisContent.setStorageName(mainReference.getPath());
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
                                        String key = ref.push().getKey();
                                        ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                hideProgressDialog();
                                                setResult(RESULT_OK);
                                                finish();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
        } else if (currentMedia.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            final StorageReference thumbReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(loggedUser.getId())).child("post-previewVideo").child("media" + new Date().getTime());
            try {
                Glide.with(this)
                        .load(currentMedia.getPath())
                        .asBitmap()
                        .override(400, 400)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap thumbResource, GlideAnimation<? super Bitmap> glideAnimation) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                thumbResource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] thumb = stream.toByteArray();
                                UploadTask thumbUploadTask = thumbReference.putBytes(thumb);
                                thumbUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {
                                        if (thumbTask.isSuccessful()) {
                                            @SuppressWarnings("VisibleForTests") Uri thumbDownloadUrl = thumbTask.getResult().getDownloadUrl();
                                            thisContent.setThumbUrl(thumbDownloadUrl.toString());
                                            thisContent.setVideoThumbStorage(thumbReference.getPath());
                                            File file = new File(currentMedia.getPath());
                                            int size = (int) file.length();
                                            byte[] bytes = new byte[size];
                                            try {
                                                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
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
                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
                                                            String key = ref.push().getKey();
                                                            ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    long stopTime = System.currentTimeMillis();
                                                                    long elapsedTime = stopTime - startTime;
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
                                        }
                                    }
                                });
                            }
                        });
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }
}

