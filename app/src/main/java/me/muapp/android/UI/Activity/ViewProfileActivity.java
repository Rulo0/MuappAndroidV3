package me.muapp.android.UI.Activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.MuappUserInfoHandler;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;
import me.muapp.android.UI.Fragment.ViewUserProfileFragment;

public class ViewProfileActivity extends BaseActivity implements MuappUserInfoHandler, OnFragmentInteractionListener, OnProfileScrollListener {
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    MatchingUser user;
    TextView txt_profile_view_name;
    ImageView img_profile_view_verified;
    RelativeLayout container_actions_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fucker");
        txt_profile_view_name = (TextView) findViewById(R.id.txt_profile_view_name);
        img_profile_view_verified = (ImageView) findViewById(R.id.img_profile_view_verified);
        container_actions_profile = (RelativeLayout) findViewById(R.id.container_actions_profile);
        int userId = getIntent().getIntExtra(USER_ID, -1);
        if (userId < 0)
            finish();
        txt_profile_view_name.setText(getIntent().getStringExtra(USER_NAME));
        new APIService(this).getFullUser(userId, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onFailure(boolean isSuccessful, String responseString) {
        finish();
    }

    @Override
    public void onSuccess(int responseCode, MatchingUser muappuser) {
        user = muappuser;
        if (user.getFakeAccount()) {

        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_profile_view_verified.setImageResource(R.drawable.ic_verified_profile);
                img_profile_view_verified.setVisibility(View.VISIBLE);
                img_profile_view_verified.setPadding(0, 0, dpToPx(4), 0);
            }
        });

        Log.w("GettingUser", muappuser.toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_profile_view, ViewUserProfileFragment.newInstance(muappuser));
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showControls(final Boolean show) {
        Handler mainHandler = new Handler(getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Utils.animViewScale(ViewProfileActivity.this, container_actions_profile, show);
            }
        };
        mainHandler.post(myRunnable);

    }

    @Override
    public void onScrollToTop() {
        showControls(true);
    }

    @Override
    public void onScroll() {
        showControls(false);
    }
}
