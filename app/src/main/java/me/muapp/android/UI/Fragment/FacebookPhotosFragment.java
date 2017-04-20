package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import me.muapp.android.Classes.Internal.FacebookAlbum;
import me.muapp.android.Classes.Internal.FacebookImage;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.AddFBPhotosAdapter;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

/**
 * Created by rulo on 12/04/17.
 */

public class FacebookPhotosFragment extends Fragment {
    OnImageSelectedListener onImageSelectedListener;
    User loggedUser;
    private static final String ARG_LOGGED_USER = "LOGGED_USER";
    RecyclerView recycler_photo_add;
    AddFBPhotosAdapter ada;
    boolean hasImage = false;

    public FacebookPhotosFragment() {
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

    public static FacebookPhotosFragment newInstance(User user) {
        FacebookPhotosFragment fragment = new FacebookPhotosFragment();
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
        ada = new AddFBPhotosAdapter(getContext());
        ada.setOnImageSelectedListener(onImageSelectedListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_photos, container, false);
        recycler_photo_add = (RecyclerView) rootView.findViewById(R.id.recycler_photo_add);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_photo_add.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recycler_photo_add.setAdapter(ada);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                String.format("me/photos/uploaded", new PreferenceHelper(getContext()).getFacebookId()),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject graph = response.getJSONObject();
                            Log.wtf("response", graph.toString());
                            Log.wtf("ALBUMS", response.toString());
                            if (graph.has("data") && !graph.isNull("data")) {
                                JSONArray array = graph.getJSONArray("data");
                                List<FacebookImage> photos = FacebookAlbum.imagesAsAlbumList(array);
                                for (FacebookImage i : photos) {
                                    ada.addPhoto(i);
                                    recycler_photo_add.scrollToPosition(0);
                                }
                            }
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}