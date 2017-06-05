package me.muapp.android.UI.Activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;

import org.json.JSONObject;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.LoginHelper;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;

import static me.muapp.android.Classes.Util.Utils.serializeUser;
import static me.muapp.android.UI.Activity.LocationCheckerActivity.SHOULD_REDIRECT_TO_CONFIRM;

public class SplashActivity extends BaseActivity {
    public static final String TAG = "SplashActivity";
    Snackbar snackbar;
    ImageView img_kiss1;
    ImageView img_kiss2;
    AnimatorSet kissFrontLaunch;
    String pendingMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics.logEvent(Analytics.Login.LOGIN_EVENTS.Login_Loading.name(), null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        img_kiss1 = (ImageView) findViewById(R.id.img_kiss1);
        img_kiss2 = (ImageView) findViewById(R.id.img_kiss2);

        if (!TextUtils.isEmpty(pendingMatch = getIntent().getStringExtra("dialog_key"))) {
            new PreferenceHelper(this).putPendingMatch(pendingMatch);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animate1(img_kiss1);
        animate1(img_kiss2);
        new Thread(new Runnable() {
            public void run() {
                validateLogin();
            }
        }).start();


     /*   splashHandler = new Handler();
        splashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                validateLogin();
            }
        }, 5000);*/
    }

    private void animateToActivity(final Class target) {
        kissFrontLaunch = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.launch_kiss_anim);
        kissFrontLaunch.setTarget(img_kiss2);
        kissFrontLaunch.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                img_kiss1.clearAnimation();
                img_kiss2.clearAnimation();
                Intent redirectIntent = new Intent(SplashActivity.this, target);
                if (target == ConfirmUserActivity.class) {
                    redirectIntent = new Intent(SplashActivity.this, LocationCheckerActivity.class);
                    redirectIntent.putExtra(SHOULD_REDIRECT_TO_CONFIRM, true);
                }
              /*  if (loggedUser != null) {
                    Log.wtf("ValidateLogin", loggedUser.toString());
                    if (loggedUser.getConfirmed() != null && loggedUser.getConfirmed())
                        redirectIntent.setClass(SplashActivity.this, ManGateActivity.class);
                    // else
                    //   redirectIntent.setClass(SplashActivity.this, ConfirmUserActivity.class);
                } else {
                    Log.wtf("ValidateLogin", "is null");
                    redirectIntent.setClass(SplashActivity.this, LoginActivity.class);
                }*/
                startActivity(redirectIntent);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        this.runOnUiThread(new Runnable() {
            public void run() {
                kissFrontLaunch.start();
            }
        });
    }

    private void validateLogin() {
        if (isInternetAvaliable) {
            new APIService(this).loginToMuapp(new UserInfoHandler() {
                @Override
                public void onSuccess(int responseCode, String userResponse) {
                    Log.wtf(TAG, userResponse);
                    try {
                        JSONObject response = new JSONObject(userResponse);
                        if (response.has("user")) {
                            Gson gson = new Gson();
                            User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                            if (u != null) {
                                if (u.getId() != null)
                                    new LoginHelper(SplashActivity.this).performFullLogin();
                                Log.wtf(TAG, u.toString());
                                saveUser(u);
                                redirectLoggedUser();
                            } else {
                                Log.wtf(TAG, "user is null");
                                animateToActivity(LoginActivity.class);
                            }
                        }

                    } catch (Exception x) {
                        Log.wtf(TAG, x.getMessage());
                        x.printStackTrace();
                    }

                }

                @Override
                public void onSuccess(int responseCode, User user) {

                }

                @Override
                public void onFailure(boolean isSuccessful, String responseString) {
                    animateToActivity(LoginActivity.class);
                }
            });

        } else {
            snackbar = Snackbar
                    .make(findViewById(R.id.splash_parent), "No Internet", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            validateLogin();
                        }
                    });
            snackbar.show();
        }
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
                    animateToActivity(ManGateActivity.class);
                } else {
                    //Usuario no aceptado
                    animateToActivity(Utils.hasLocationPermissions(this) ? MainActivity.class : LocationCheckerActivity.class);
                }
            } else {
                //Usuario mujer
                animateToActivity(Utils.hasLocationPermissions(this) ? MainActivity.class : LocationCheckerActivity.class);
            }
        } else {
            //Usuario sin confirmar datos
            animateToActivity(ConfirmUserActivity.class);
        }
    }

    private void animate1(View view) {
        ScaleAnimation mAnimation = new ScaleAnimation(0.65f, 1f, 0.65f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (view.getId() == R.id.img_kiss1)
            mAnimation = new ScaleAnimation(1f, 0.65f, 1f, 0.65f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(mAnimation);
    }

    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        super.onConnectionChange(event);
     /*   if (snackbar != null)
            snackbar.dismiss();*/
        // validateLogin();
    }
}
