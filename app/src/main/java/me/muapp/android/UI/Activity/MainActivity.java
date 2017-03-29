package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

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

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

public class MainActivity extends BaseActivity implements OnFragmentInteractionListener {
    private static final int PHONE_REQUEST_CODE = 79;
    public static final String TAG = "MainActivity";
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
        phoneValidation();
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }

    private void confirmMyUser() {
        loggedUser.setConfirmed(true);
        saveUser(loggedUser);
        try {
            new APIService(this).patchUser(null, new UserInfoHandler() {
                @Override
                public void onSuccess(int responseCode, String userResponse) {

                }

                @Override
                public void onSuccess(int responseCode, User user) {

                }

                @Override
                public void onFailure(boolean isSuccessful, String responseString) {

                }
            });
        } catch (Exception x) {

        }
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                Log.wtf(TAG, account.getId() + " " + account.getPhoneNumber());

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
                confirmMyUser();
            }
        }
    }
}