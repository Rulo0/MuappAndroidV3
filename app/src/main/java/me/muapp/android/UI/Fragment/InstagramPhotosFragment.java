package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import me.muapp.android.Classes.Instagram.Data.InstagramPhoto;
import me.muapp.android.Classes.Instagram.Data.InstagramPhotos;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserInfo;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.AddInstagramPhotosAdapter;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rulo on 12/04/17.
 */

public class InstagramPhotosFragment extends Fragment implements ValueEventListener {
    OnImageSelectedListener onImageSelectedListener;
    User loggedUser;
    private static final String ARG_LOGGED_USER = "LOGGED_USER";
    Button btn_connect_instagram;
    DatabaseReference userInstagramReference;
    UserInfo userInfo;
    RecyclerView recycler_add_instagram;
    AddInstagramPhotosAdapter ada;

    public InstagramPhotosFragment() {
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

    public static InstagramPhotosFragment newInstance(User user) {
        InstagramPhotosFragment fragment = new InstagramPhotosFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOGGED_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public void fistImageLoad() {
        try {
            recycler_add_instagram.findViewHolderForAdapterPosition(0).itemView.performClick();
        } catch (Exception x) {
            Log.wtf("setFirstPhoto", x.getMessage());
            x.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        loggedUser = args.getParcelable(ARG_LOGGED_USER);
        userInstagramReference = FirebaseDatabase.getInstance().getReference().child("users").child(String.valueOf(loggedUser.getId()));
        ada = new AddInstagramPhotosAdapter(getContext());
        ada.setOnImageSelectedListener(onImageSelectedListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.instagram_add_photos, container, false);
        btn_connect_instagram = (Button) rootView.findViewById(R.id.btn_connect_instagram);
        recycler_add_instagram = (RecyclerView) rootView.findViewById(R.id.recycler_add_instagram);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_add_instagram.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recycler_add_instagram.setAdapter(ada);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.wtf("MyUser", loggedUser.getId() + "");
        userInfo = dataSnapshot.getValue(UserInfo.class);
        if (userInfo != null) {
            Log.wtf("onDataChange", userInfo.toString());
            btn_connect_instagram.setVisibility(View.GONE);
            new InstagramDataTask().execute(userInfo);
        } else {
            btn_connect_instagram.setVisibility(View.VISIBLE);
            btn_connect_instagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OauthInstagramDialog instagramDialog = new OauthInstagramDialog();
                    instagramDialog.setUserId(loggedUser.getId());
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    instagramDialog.show(ft, "dialog");
                }
            });
        }
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

    @Override
    public void onStart() {
        super.onStart();
        userInstagramReference.addValueEventListener(this);
    }

    private class InstagramDataTask extends AsyncTask<UserInfo, Void, InstagramPhotos> {
        @Override
        protected InstagramPhotos doInBackground(UserInfo... p) {
            InstagramPhotos photos = new InstagramPhotos();
            try {
                String url = String.format("https://api.instagram.com/v1/users/self/media/recent/?access_token=%s", p[0].getInstagramToken());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    photos = new Gson().fromJson(responseString, InstagramPhotos.class);
                }
            } catch (Exception x) {
            } finally {
                return photos;
            }
        }

        @Override
        protected void onPostExecute(InstagramPhotos instagramPhotos) {
            super.onPostExecute(instagramPhotos);

            for (InstagramPhoto p : instagramPhotos.getData()) {
                Log.wtf("onPostExecute", p.toString());
                ada.addPhotho(p);
            }

        }
    }

}