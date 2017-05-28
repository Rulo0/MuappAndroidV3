package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.MuappUserInfoHandler;
import me.muapp.android.Classes.Internal.MuappUser;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.MatchingUserProfileFragment;

public class ProfileViewActivity extends BaseActivity implements MuappUserInfoHandler, OnFragmentInteractionListener {
    public static final String USER_ID = "USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int userId = getIntent().getIntExtra(USER_ID, -1);
        setContentView(R.layout.activity_profile_view);
        if (userId < 0)
            finish();
        new APIService(this).getFullUser(userId, this);

    }

    @Override
    public void onFailure(boolean isSuccessful, String responseString) {
        finish();
    }

    @Override
    public void onSuccess(int responseCode, MuappUser muappuser) {
        Log.w("GettingUser", muappuser.toString() + "\n" + muappuser.toMatchingUser().toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_profile_view,   MatchingUserProfileFragment.newInstance(muappuser.toMatchingUser()));
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }
}
