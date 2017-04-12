package me.muapp.android.UI.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

/**
 * Created by rulo on 12/04/17.
 */

public class GalleryPhotosFragment extends Fragment {
    private static final int REQUEST_MEDIA = 44;
    OnImageSelectedListener onImageSelectedListener;
    User loggedUser;
    private static final String ARG_LOGGED_USER = "LOGGED_USER";
    String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE
    };

    public GalleryPhotosFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onImageSelectedListener = (OnImageSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LogoutUser");
        }
    }

    public static GalleryPhotosFragment newInstance(User user) {
        GalleryPhotosFragment fragment = new GalleryPhotosFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        loggedUser = args.getParcelable(ARG_LOGGED_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_add_photos, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (checkAndRequestPermissions())
            getAllUserMedia();
    }

    private void getAllUserMedia() {
        try {
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    + " AND "
                    + MediaStore.Files.FileColumns.DATA
                    + " NOT LIKE '%Muapp/voiceNotes%'";

            Uri queryUri = MediaStore.Files.getContentUri("external");

            CursorLoader cursorLoader = new CursorLoader(
                    getContext(),
                    queryUri,
                    projection,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );

            Cursor cursor = cursorLoader.loadInBackground();
            try {
                while (cursor.moveToNext()) {
                    Log.wtf("getAllUserMedia", cursor.getString(0) + " | " + cursor.getString(1) + " | " + cursor.getString(2) + " | " + cursor.getString(3) + " | " + cursor.getString(4) + " | " + cursor.getString(5));
                    Log.wtf("getAllUserMedia", Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0)).toString());
                }
            } finally {
                cursor.close();
            }
        } catch (Exception x) {
            Log.wtf("GALLERY", x.getMessage());
            x.printStackTrace();
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Write Needed");
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Read Needed");
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MEDIA);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MEDIA) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        getAllUserMedia();
                    } else {
                        checkAndRequestPermissions();
                    }
                }
            }
        }
    }
}