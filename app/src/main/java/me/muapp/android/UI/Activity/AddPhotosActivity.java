package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.AddFBPhotosAdapter;

public class AddPhotosActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class FacebookPhotosFragment extends Fragment {
        User loggedUser;
        private static final String ARG_LOGGED_USER = "LOGGED_USER";
        List<FacebookAlbum> albums;
        RecyclerView recycler_photo_add;
        AddFBPhotosAdapter ada;

        public FacebookPhotosFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FacebookPhotosFragment newInstance(User user) {
            FacebookPhotosFragment fragment = new FacebookPhotosFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_LOGGED_USER, user);
            fragment.setArguments(args);
            return fragment;
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
            ada = new AddFBPhotosAdapter(getContext(), null);
            recycler_photo_add.setLayoutManager(new GridLayoutManager(getContext(), 5));
            recycler_photo_add.setAdapter(ada);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(), String.format(
                    "/%s/albums", new PreferenceHelper(getContext()).getFacebookId()),
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                JSONObject graph = response.getJSONObject();
                                if (graph.has("data") && !graph.isNull("data")) {
                                    JSONArray array = graph.getJSONArray("data");
                                    albums = FacebookAlbum.asList(array);
                                    for (final FacebookAlbum a : albums) {
                                        new GraphRequest(
                                                AccessToken.getCurrentAccessToken(), String.format("/%s/photos", a.getId()),
                                                null,
                                                HttpMethod.GET,
                                                new GraphRequest.Callback() {
                                                    public void onCompleted(GraphResponse response) {
                                                        try {
                                                            Log.wtf("ALBUMS", response.toString());
                                                            JSONObject albumGraph = response.getJSONObject();
                                                            List<String> photos;
                                                            if (albumGraph.has("data") && !albumGraph.isNull("data")) {
                                                                JSONArray array = albumGraph.getJSONArray("data");
                                                                photos = FacebookAlbum.asAlbumList(array);
                                                                a.setPhotosId(photos);
                                                                a.setFirstPhotoId(photos.get(0));
                                                                for (String photo : a.getPhotosId()) {
                                                                    ada.addPhotho(photo);
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
                            } catch (Exception x) {
                                Log.e(TAG, x.toString());
                                x.printStackTrace();
                            }

                        }
                    }
            ).executeAsync();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FacebookPhotosFragment.newInstance(loggedUser);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
