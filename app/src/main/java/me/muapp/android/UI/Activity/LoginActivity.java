package me.muapp.android.UI.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.Errors.Login.LoginError;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.Constants;
import me.muapp.android.Classes.Util.LoginHelper;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;

import static me.muapp.android.Classes.Util.Utils.serializeUser;
import static me.muapp.android.UI.Activity.ErrorActivity.ERROR_EXTRA;
import static me.muapp.android.UI.Activity.ErrorActivity.ERROR_SHOW_EMAIL;
import static me.muapp.android.UI.Activity.LocationCheckerActivity.SHOULD_REDIRECT_TO_CONFIRM;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    CallbackManager callbackManager;
    LoginButton loginButton;
    PreferenceHelper preferenceHelper;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onStart() {
        super.onStart();
        if (loggedUser != null) {
            redirectLoggedUser();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        preferenceHelper = new PreferenceHelper(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        final List<String> permissions = getFacebookPermissions();
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w("FBLogin", "Success:" + loginResult.getAccessToken());
                Set<String> setPermissions = new HashSet<>(permissions);
                if (loginResult.getRecentlyGrantedPermissions().containsAll(setPermissions)) {
                    AccessToken accessToken = loginResult.getAccessToken();
                    preferenceHelper.putFacebookId(loginResult.getAccessToken().getUserId());
                    preferenceHelper.putFacebookToken(accessToken.getToken());
                    preferenceHelper.putFacebookTokenExpiration(accessToken.getExpires().getTime());
                    loginToMuapp();
                    //  startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, ErrorActivity.class));
                }
            }

            @Override
            public void onCancel() {
                Log.w("FBLogin", "Canceled:");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FBLogin", error.getMessage());
                error.printStackTrace();

            }
        });
        findViewById(R.id.txt_login_fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
                mFirebaseAnalytics.logEvent(Analytics.Login.LOGIN_EVENTS.Login_FB.toString(), null);
            }
        });

        findViewById(R.id.img_info_login).setOnClickListener(this);
        findViewById(R.id.txt_privacy_login).setOnClickListener(this);
        findViewById(R.id.txt_terms_login).setOnClickListener(this);
        if (loggedUser != null) {
            try {
                // startActivity(new Intent(LoginActivity.this, ConfirmUserActivity.class));
            } catch (Exception x) {
            }
        }
    }

    private void loginToMuapp() {
        showProgressDialog();
        try {
            new APIService(LoginActivity.this).loginToMuapp(new UserInfoHandler() {
                @Override
                public void onSuccess(int responseCode, String userResponse) {
                    hideProgressDialog();
                    try {
                        JSONObject response = new JSONObject(userResponse);
                        if (response.has("user")) {
                            Gson gson = new Gson();
                            User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                            if (u != null) {
                                saveUser(u);
                                redirectLoggedUser();
                                new LoginHelper(LoginActivity.this).performFullLogin();
                            } else {

                            }
                        } else {
                            if (response.has("error")) {
                                Gson gson = new Gson();
                                LoginError le = gson.fromJson(response.toString(), LoginError.class);
                                if (le != null)
                                    validateError(le);
                            }
                        }

                    } catch (Exception x) {

                        x.printStackTrace();
                    }

                }

                @Override
                public void onSuccess(int responseCode, User user) {

                }

                @Override
                public void onFailure(boolean isSuccessful, String responseString) {
                    hideProgressDialog();
                    Gson gson = new Gson();
                    LoginError le = gson.fromJson(responseString, LoginError.class);
                    if (le != null)
                        Log.wtf(TAG, "onFailure " + le.toString());
                }
            });
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private void validateError(LoginError le) {
        Intent errorIntent = new Intent(LoginActivity.this, ErrorActivity.class);
        Bundle params = new Bundle();
        switch (le.getErrorType()) {
            case NoPhoto:
                params.putString(Analytics.Login.LOGIN_TYPE.Type.toString(), Analytics.Login.LOGIN_ERROR_TYPE.Photo.toString());
                Log.wtf(TAG, "NoPhoto");
                errorIntent.putExtra(ERROR_EXTRA, getString(R.string.lbl_error_profile_picture));
                break;
            case Underage:
                params.putString(Analytics.Login.LOGIN_TYPE.Type.toString(), Analytics.Login.LOGIN_ERROR_TYPE.Minor.toString());
                Log.wtf(TAG, "Underage");
                errorIntent.putExtra(ERROR_EXTRA, getString(R.string.lbl_error_minor));
                break;
            case NoFriends:
                params.putString(Analytics.Login.LOGIN_TYPE.Type.toString(), Analytics.Login.LOGIN_ERROR_TYPE.Friends.toString());
                Log.wtf(TAG, "NoFriends");
                errorIntent.putExtra(ERROR_EXTRA, getString(R.string.lbl_error_real_person));
                errorIntent.putExtra(ERROR_SHOW_EMAIL, true);
                break;
            case Expelled:
                params.putString(Analytics.Login.LOGIN_TYPE.Type.toString(), Analytics.Login.LOGIN_ERROR_TYPE.FBPermissions.toString());
                Log.wtf(TAG, "Expelled");
                errorIntent.putExtra(ERROR_EXTRA, getString(R.string.lbl_error_requirements));
                break;
            case FBPermissions:
                params.putString(Analytics.Login.LOGIN_TYPE.Type.toString(), Analytics.Login.LOGIN_ERROR_TYPE.FBPermissions.toString());
                Log.wtf(TAG, "FBPermissions");
                errorIntent.putExtra(ERROR_EXTRA, getString(R.string.lbl_error_permissions));
                break;
            default:
                Log.wtf(TAG, "U");
                break;
        }
        mFirebaseAnalytics.logEvent(Analytics.Login.LOGIN_EVENTS.Login_Error.toString(), params);
        startActivity(errorIntent);
    }

    private void redirectLoggedUser() {
        Log.i(TAG, "confirmed " + loggedUser.getConfirmed());
        Log.i(TAG, "is female " + (User.Gender.getGender(loggedUser.getGender()) == User.Gender.Female));
        Log.i(TAG, "pending " + loggedUser.getPending());
        if (loggedUser.getConfirmed() != null && loggedUser.getConfirmed()) {
            if (User.Gender.getGender(loggedUser.getGender()) == User.Gender.Male) {
                Log.i(TAG, "User is male");
                //Usuario hombre
                if (loggedUser.getPending()) {
                    //Usuario aceptado
                    startActivity(new Intent(this, ManGateActivity.class));
                } else {
                    //Usuario no aceptado
                    startActivity(new Intent(this, Utils.hasLocationPermissions(this) ? MainActivity.class : LocationCheckerActivity.class));
                }
            } else {
                //Usuario mujer
                startActivity(new Intent(this, Utils.hasLocationPermissions(this) ? MainActivity.class : LocationCheckerActivity.class));
            }
        } else {
            //Usuario sin confirmar datos
            //startActivity(new Intent(this, ConfirmUserActivity.class));
            Intent redirectIntent = new Intent(this, LocationCheckerActivity.class);
            redirectIntent.putExtra(SHOULD_REDIRECT_TO_CONFIRM, true);
            startActivity(redirectIntent);
        }
        finish();

    }

    private List<String> getFacebookPermissions() {
        final String[] permissions = Constants.Facebook.PERMISSIONS.split(",");
        return Arrays.asList(permissions);
    }


    @Override
    public void onConnectionChange(ConnectivityEvent event) {

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setShowTitle(true);
        intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        switch (v.getId()) {
            case R.id.txt_privacy_login:
                customTabsIntent.launchUrl(this, Uri.parse(Constants.URL.PRIVACY));
                break;
            case R.id.txt_terms_login:
                customTabsIntent.launchUrl(this, Uri.parse(Constants.URL.TERMS));
                break;
            case R.id.img_info_login:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_muapp_app)
                        .setMessage(R.string.lbl_info_message)
                        .setTitle(R.string.lbl_info_title);
                AlertDialog dialog = builder.create();
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }
}