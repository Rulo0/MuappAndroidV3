package me.muapp.android.UI.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;
import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.Internal.CurrentNavigationElement;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.BasicFragment;
import me.muapp.android.UI.Fragment.ChatFragment;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.ProfileFragment;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener, SearchView.OnQueryTextListener {
    private static final int PHONE_REQUEST_CODE = 79;
    public static final String TAG = "MainActivity";
    private CurrentNavigationElement navigationElement;
    private int mSelectedItem;
    HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();
    private Realm realm;
    BottomNavigationView navigation;


    public void phoneValidation() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, PHONE_REQUEST_CODE);
        preferenceHelper.putFirstLoginDisabled();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_male);
        realm = CacheUtils.getInstance(loggedUser);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        if (checkPlayServices()) {
            final String token = FirebaseInstanceId.getInstance().getToken();
            if (!TextUtils.equals(new PreferenceHelper(this).getGCMToken(), token)) {
                FirebaseMessaging.getInstance().subscribeToTopic("android");
                Log.d(TAG, "InstanceID token: " + token);
                JSONObject tokenUser = new JSONObject();
                try {
                    tokenUser.put("push_token", token);
                    new APIService(this).patchUser(tokenUser, new UserInfoHandler() {
                        @Override
                        public void onSuccess(int responseCode, String userResponse) {
                        }

                        @Override
                        public void onSuccess(int responseCode, User user) {
                            new PreferenceHelper(MainActivity.this).putGCMToken(token);
                            saveUser(user);
                        }

                        @Override
                        public void onFailure(boolean isSuccessful, String responseString) {

                        }
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
        if (preferenceHelper.getFirstLogin() && !loggedUser.getFakeAccount())
            phoneValidation();
        navigation.setOnNavigationItemSelectedListener(this);
        fragmentHashMap.put(R.id.navigation_home, ChatFragment.newInstance(loggedUser));
        fragmentHashMap.put(R.id.navigation_dashboard, BasicFragment.newInstance(loggedUser));
        fragmentHashMap.put(R.id.navigation_notifications, ProfileFragment.newInstance(loggedUser));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_main_male, fragmentHashMap.get(R.id.navigation_home));
        ft.commit();

        navigationElement = new CurrentNavigationElement(navigation.getMenu().findItem(R.id.navigation_home), fragmentHashMap.get(R.id.navigation_home));
        startActivity(new Intent(this, SpotifySearchActivity.class));
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }

    private void confirmMyUser() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                Log.wtf(TAG, account.getId() + " " + account.getPhoneNumber());

                loggedUser.setFakeAccount(true);
                saveUser(loggedUser);
                try {
                    new APIService(MainActivity.this).setUserFakeAccount(true, null);
                } catch (Exception x) {

                }
            }

            @Override
            public void onError(AccountKitError accountKitError) {
                Log.wtf(TAG, accountKitError.getErrorType().toString());
                Log.wtf(TAG, accountKitError.getErrorType().getMessage().toString());
                Log.wtf(TAG, accountKitError.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf(TAG, requestCode + " " + resultCode);
        if (requestCode == PHONE_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                Log.wtf(TAG, loginResult.getError().getErrorType().getMessage());
            } else if (loginResult.wasCancelled()) {
                Log.wtf(TAG, "login_cancelled");
            } else {
                Log.wtf(TAG, "confirmMyUser");
                confirmMyUser();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
        } catch (Exception x) {
            x.printStackTrace();
        }
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}