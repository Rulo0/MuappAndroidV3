package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import me.muapp.android.Classes.Internal.CurrentNavigationElement;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.BasicFragment;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.ManGateFragment;

public class ManGateActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {
    private CurrentNavigationElement navigationElement;
    User currentLoggedUser;
    private int mSelectedItem;
    HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_gate);
        currentLoggedUser = new UserHelper(this).getLoggedUser();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_man_gate);
        navigation.setOnNavigationItemSelectedListener(this);
        fragmentHashMap.put(R.id.navigation_man_discover, ManGateFragment.newInstance(loggedUser));
        fragmentHashMap.put(R.id.navigation_profile_man_profile, BasicFragment.newInstance(loggedUser));
        Fragment frag = fragmentHashMap.get(R.id.navigation_man_discover);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.man_content_container, frag);
        ft.commit();
        navigationElement = new CurrentNavigationElement(navigation.getMenu().findItem(R.id.navigation_man_discover), frag);
    }

    private void selectFragment(MenuItem item) {
        try {
            Log.wtf("selectFragment", item.getTitle().toString());
            Fragment frag = fragmentHashMap.get(item.getItemId());
            mSelectedItem = item.getItemId();
            if (frag != null) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.hide(navigationElement.getFrag());
                ft.show(frag);
                ft.commit();

            }
            navigationElement = new CurrentNavigationElement(item, frag);
            invalidateOptionsMenu();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (navigationElement.getItm().getItemId()) {
            case R.id.navigation_man_discover:
                getMenuInflater().inflate(R.menu.empty_menu, menu);
                break;
            case R.id.navigation_profile_man_profile:
                getMenuInflater().inflate(R.menu.man_profile_gate_menu, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_description:
                startActivity(new Intent(this, ProfileSettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getSupportActionBar().setTitle(item.getTitle());
        if (!navigationElement.getItm().equals(item)) {
            selectFragment(item);
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }
}

