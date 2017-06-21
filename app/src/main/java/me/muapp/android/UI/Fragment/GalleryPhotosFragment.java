package me.muapp.android.UI.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import me.muapp.android.Classes.Util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserMedia;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.AddDevicePhotosAdapter;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

/**
 * Created by rulo on 12/04/17.
 */

public class GalleryPhotosFragment extends Fragment {
    private static final int REQUEST_MEDIA = 44;
    RecyclerView recycler_add_device;
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
    AddDevicePhotosAdapter ada;

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
        ada = new AddDevicePhotosAdapter(getContext());
        ada.setOnImageSelectedListener(onImageSelectedListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_add_photos, container, false);
        recycler_add_device = (RecyclerView) rootView.findViewById(R.id.recycler_add_device);
        return rootView;
    }

    public void fistImageLoad() {
        try {
            recycler_add_device.findViewHolderForAdapterPosition(0).itemView.performClick();
        } catch (Exception x) {
            Log.wtf("setFirstPhoto", x.getMessage());
            x.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_add_device.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recycler_add_device.setAdapter(ada);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (checkAndRequestPermissions())
            getAllUserMedia();
    }

    private void getAllUserMedia() {
        new getUserMediaTask().execute(getContext());
    }

    class getUserMediaTask extends AsyncTask<Context, Void, List<UserMedia>> {
        CursorLoader cursorLoader;
        String selection;

        @Override
        protected void onPreExecute() {
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    + " AND "
                    + MediaStore.Files.FileColumns.DATA
                    + " NOT LIKE '%Muapp/voiceNotes%'";
            Uri queryUri = MediaStore.Files.getContentUri("external");
            cursorLoader = new CursorLoader(
                    getContext(),
                    queryUri,
                    projection,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );
        }

        @Override
        protected List<UserMedia> doInBackground(Context... params) {
            List<UserMedia> result = new ArrayList<>();
            try {
                Cursor cursor = cursorLoader.loadInBackground();
                try {
                    while (cursor.moveToNext()) {
                        UserMedia dp = new UserMedia(cursor.getInt(0), cursor.getString(1), Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0)), cursor.getLong(2), cursor.getInt(3));
                        result.add(dp);
                    }
                } finally {
                    cursor.close();
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<UserMedia> userMedias) {
            super.onPostExecute(userMedias);
            for (UserMedia m : userMedias) {
                ada.addPhoto(m);
                recycler_add_device.scrollToPosition(0);
            }
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