package me.muapp.android.UI.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.API.Params.AlbumParam;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.Log;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.UserPhotos.UserPhotoTouchHelper;
import me.muapp.android.UI.Adapter.UserPhotos.UserPictureAdapter;
import me.muapp.android.UI.Fragment.Interface.OnProfileImageSelectedListener;

import static me.muapp.android.UI.Activity.FacebookPhotoDetailActivity.PHOTO_URL;
import static me.muapp.android.UI.Adapter.UserPhotos.UserPictureAdapter.picturesData;

public class ProfileSettingsActivity extends BaseActivity implements OnProfileImageSelectedListener {
    private static final int PIC_CROP = 724;
    RecyclerView recycler_user_photos;
    UserPictureAdapter adapter;
    EditText et_user_biography;
    public static final int REQUEST_FACEBOOK_ALBUMS = 451;
    private int REQUEST_MEDIA = 377;
    public static final String ADAPTER_POSITION = "ADAPTER_POSITION";
    private File photoFile;
    private Uri photoURI;
    private final int REQUEST_CAMERA = 911;
    private String mCurrentPhotoPath;
    private final int SELECT_FILE = 912;
    private int photoSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        et_user_biography = (EditText) findViewById(R.id.et_user_biography);
        Log.wtf("Biography", ":" + loggedUser.getDescription());
        et_user_biography.setText(loggedUser.getDescription());
        recycler_user_photos = (RecyclerView) findViewById(R.id.recycler_user_photos);
        if (loggedUser.getPending()) {
            recycler_user_photos.setVisibility(View.GONE);
        }
        List<String> userPhotos = new ArrayList<>();
        userPhotos.addAll(loggedUser.getAlbum());

        if (userPhotos.size() < 6)
            for (int i = userPhotos.size(); i < 6; i++) {
                userPhotos.add("");
            }
        adapter = new UserPictureAdapter(this, userPhotos);
        adapter.setOnProfileImageSelectedListener(this);
        recycler_user_photos.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_user_photos.setHasFixedSize(true);
        recycler_user_photos.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new UserPhotoTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recycler_user_photos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                attempSaveSettings();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void attempSaveSettings() {
        List<String> userAlbum = new ArrayList<>();
        List<AlbumParam> prms = new ArrayList<>();
        for (String s : adapter.picturesData) {
            if (s != null && !TextUtils.isEmpty(s)) {
                userAlbum.add(s);
                AlbumParam albumParam = new AlbumParam();
                albumParam.setUrl(s);
                prms.add(albumParam);
            }
            Log.wtf("Original", s != null ? s.toString() : "empty");
        }
        uploadPhotos(prms, false);
        loggedUser.setAlbum(userAlbum);
        saveUser(loggedUser);

        final String newDescription = et_user_biography.getText().toString();
        if (!newDescription.equals(loggedUser.getDescription())) {
            try {
                mFirebaseAnalytics.logEvent(Analytics.EditInfo.EDIT_INFO_EVENT.Edit_Info_Description.toString(), null);
                loggedUser.setDescription(newDescription);
                saveUser(loggedUser);
                JSONObject descriptionObj = new JSONObject();
                descriptionObj.put("description", newDescription);
                new APIService(this).patchUser(descriptionObj, null);
                finish();
                /*final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
                reference.orderByChild("catContent").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.wtf("Found", dataSnapshot.getRef().toString());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                Log.wtf("Found", snapshot.child("catContent").getValue(String.class));
                                if (snapshot.child("catContent").getValue(String.class).equals("contentDesc")) {
                                    Log.wtf("Found", snapshot.getRef().toString());
                                    snapshot.getRef().removeValue();
                                }
                            } catch (Exception x) {
                                x.printStackTrace();
                            }
                        }
                        UserContent content = new UserContent();
                        content.setCreatedAt(32535237599000L);
                        content.setCatContent("contentDesc");
                        content.setComment(newDescription);
                        content.setLikes(0);
                        reference.child(reference.push().getKey()).setValue(content).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
            } catch (Exception x) {
                x.printStackTrace();
            }
        } else {
            finish();
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Camera Needed");
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
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


    private void cameraIntent(int position) {
        photoSelectedPosition = position;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "me.muapp.android.fileprovider",
                        photoFile);
                Log.v("Intent photoURI", photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.v("storageDir", storageDir.toString());
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void galleryIntent(int position) {
        photoSelectedPosition = position;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    @Override
    public void onCameraSelected(ImageView container, int position) {
        if (checkAndRequestPermissions()) {
            cameraIntent(position);

        }
    }

    @Override
    public void onGallerySelected(ImageView container, int position) {
        if (checkAndRequestPermissions()) {
            galleryIntent(position);
        }

    }

    @Override
    public void onPictureDeleted(ImageView container, int adapterPosition) {
        List<AlbumParam> prms = new ArrayList<>();
        for (String image : picturesData) {
            if (image != null && !TextUtils.isEmpty(image)) {
                AlbumParam albumParam = new AlbumParam();
                albumParam.setUrl(image);
                prms.add(albumParam);
            }
        }
        uploadPhotos(prms, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE:
                    onSelectFromGalleryResult(data);
                    break;
                case REQUEST_CAMERA:
                    onCaptureImageResult(data);
                    break;
                case PIC_CROP:
                    onPicCropResult(data);
                    break;
                case REQUEST_FACEBOOK_ALBUMS:
                    Log.wtf("imageSelected", data.getStringExtra(PHOTO_URL));
                    Log.wtf("position", "" + data.getIntExtra(ADAPTER_POSITION, 5));
                    picturesData.set(data.getIntExtra(ADAPTER_POSITION, 5), data.getStringExtra(PHOTO_URL));
                    List<AlbumParam> prms = new ArrayList<>();
                    for (String image : picturesData) {
                        if (image != null && !TextUtils.isEmpty(image)) {
                            AlbumParam albumParam = new AlbumParam();
                            albumParam.setUrl(image);
                            prms.add(albumParam);
                        }
                    }
                    uploadPhotos(prms, true);
                    break;
            }
        } else {
            photoSelectedPosition = 0;
        }
    }

    private void onCaptureImageResult(Intent data) {
        Log.v("photoURI", photoURI.toString());
        cropImage(Uri.fromFile(photoFile));
    }

    private void uploadPhotos(List<AlbumParam> albumParams, Boolean showDialog) {
        mFirebaseAnalytics.logEvent(Analytics.EditInfo.EDIT_INFO_EVENT.Edit_Info_Photos.toString(), null);
        if (showDialog)
            showProgressDialog();
        new APIService(this).uploadPhotos(albumParams, new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
            }

            @Override
            public void onSuccess(int responseCode, User user) {
                hideProgressDialog();
                Log.wtf("Upload Completed", user.getAlbum().toString());
                saveUser(user);
                if (user.getAlbum().size() < 6)
                    for (int i = user.getAlbum().size(); i < 6; i++) {
                        user.getAlbum().add("");
                    }
                adapter.setUserPictures(user.getAlbum());
            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {
                hideProgressDialog();
            }
        });
    }

    private void onPicCropResult(Intent data) {
        try {
            Uri selectedImage = data.getData();
            File f;
            f = new File(getRealPathFromURI(selectedImage));
            int size = (int) f.length();
            byte[] bytes = new byte[size];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            Log.wtf("Success", f.getAbsolutePath() + " - " + bytes.length);
            List<String> images = new ArrayList<>(loggedUser.getAlbum());
            final List<AlbumParam> albumParams = new ArrayList<>();
            for (String image : images) {
                AlbumParam albumParam = new AlbumParam();
                albumParam.setUrl(image);
                albumParams.add(albumParam);
            }
            AlbumParam imageParam = new AlbumParam();
            imageParam.setFileBytes(bytes);
            if (adapter.getItemCount() >= photoSelectedPosition) {
                albumParams.set(photoSelectedPosition, imageParam);
            } else {
                albumParams.add(imageParam);
            }
            uploadPhotos(albumParams, true);
        } catch (Exception x) {

        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        File f = null;
        String filePath = "";
        Uri selectedImage = null;
        if (data != null && data.getData() != null) {
            try {
                selectedImage = data.getData();
                Log.v("file", selectedImage.toString());
                String wholeID = DocumentsContract.getDocumentId(selectedImage);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                    Log.v("filePath", filePath);
                    f = new File(filePath);
                } else {
                }
                cursor.close();
                cropImage(Uri.fromFile(f));
            } catch (Exception x) {
                try {
                    f = new File(getRealPathFromURI(selectedImage));
                    cropImage(Uri.fromFile(f));
                } catch (Exception ex) {

                }
            }
        }
    }

    private void cropImage(Uri selectedImageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.setDataAndType(selectedImageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public boolean albumsAreDifferent(ArrayList<String> originalAlbum, ArrayList<String> newUserAlbum) {
        if (originalAlbum == null && newUserAlbum == null)
            return true;
        if ((originalAlbum == null && newUserAlbum != null) || (originalAlbum != null && newUserAlbum == null))
            return false;

        if (originalAlbum.size() != newUserAlbum.size())
            return false;
        for (String itemList1 : originalAlbum) {
            if (!newUserAlbum.contains(itemList1))
                return false;
        }

        return true;
    }


}
