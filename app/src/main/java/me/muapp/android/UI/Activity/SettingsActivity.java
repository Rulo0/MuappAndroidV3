package me.muapp.android.UI.Activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.AgeRange;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserSettings;
import me.muapp.android.Classes.Util.Constants;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;

public class SettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "SettingsActivity";
    private static final int PHONE_REQUEST_CODE = 791;
    CheckBox chk_studies, chk_job, chk_last_name, chk_matches, chk_messages;
    LinearLayout phone_verification_container;
    UserSettings userSettings, mainUserSetting;
    RangeBar distance_seekbar, age_seekbar;
    TextView txt_settings_distance_value;
    TextView txt_settings_age_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        phone_verification_container = (LinearLayout) findViewById(R.id.phone_verification_container);
        chk_studies = (CheckBox) findViewById(R.id.chk_studies);
        chk_job = (CheckBox) findViewById(R.id.chk_job);
        chk_last_name = (CheckBox) findViewById(R.id.chk_last_name);
        chk_matches = (CheckBox) findViewById(R.id.chk_matches);
        chk_messages = (CheckBox) findViewById(R.id.chk_messages);
        age_seekbar = (RangeBar) findViewById(R.id.age_seekbar);
        distance_seekbar = (RangeBar) findViewById(R.id.distance_seekbar);
        txt_settings_distance_value = (TextView) findViewById(R.id.txt_settings_distance_value);
        txt_settings_age_value = (TextView) findViewById(R.id.txt_settings_age_value);
        chk_studies.setOnCheckedChangeListener(this);
        chk_job.setOnCheckedChangeListener(this);
        chk_last_name.setOnCheckedChangeListener(this);
        chk_matches.setOnCheckedChangeListener(this);
        chk_messages.setOnCheckedChangeListener(this);
        findViewById(R.id.txt_share_app).setOnClickListener(this);
        if (User.Gender.getGender(loggedUser.getGender()) == User.Gender.Female) {
            ((TextView) findViewById(R.id.txt_share_app)).setText(getString(R.string.lbl_share_dialog_title));
        }
        findViewById(R.id.txt_phone_validation).setOnClickListener(this);
        findViewById(R.id.txt_help).setOnClickListener(this);
        findViewById(R.id.txt_terms).setOnClickListener(this);
        findViewById(R.id.txt_privacy).setOnClickListener(this);
        findViewById(R.id.txt_rate_muapp).setOnClickListener(this);
        findViewById(R.id.btn_logout_settings).setOnClickListener(this);
        findViewById(R.id.btn_delete_account_settings).setOnClickListener(this);
        userSettings = new UserSettings();
        userSettings.setVisibleEducation(loggedUser.getVisibleEducation());
        userSettings.setVisibleWork(loggedUser.getVisibleWork());
        userSettings.setVisibleLastName(loggedUser.getVisibleLastName());
        userSettings.setNotifyMatches(loggedUser.getNotifyMatches());
        userSettings.setNotifyPokes(loggedUser.getNotifyPokes());
        userSettings.setDistance(loggedUser.getDistance());
        userSettings.setAgeRange(loggedUser.getAgeRange());
        try {
            mainUserSetting = userSettings.clone();
        } catch (Exception x) {
            x.printStackTrace();
        }
        distance_seekbar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                txt_settings_distance_value.setText((Integer.parseInt(rightPinValue) == 500 ? rightPinValue + "+" : rightPinValue) + " km");
                userSettings.setDistance(Integer.parseInt(rightPinValue));
            }
        });

        age_seekbar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                txt_settings_age_value.setText(leftPinValue + " / " + (Integer.parseInt(rightPinValue) == 55 ? rightPinValue + "+ " : rightPinValue) + " " + getString(R.string.lbl_years));
                AgeRange ageRange = new AgeRange();
                ageRange.set1(Integer.parseInt(leftPinValue));
                ageRange.set2(Integer.parseInt(rightPinValue));
                userSettings.setAgeRange(ageRange);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fillData();
    }

    private void fillData() {
        age_seekbar.setRangePinsByValue(loggedUser.getAgeRange().get1(), loggedUser.getAgeRange().get2());

        int distanceValue = loggedUser.getDistance();
        if (distanceValue == 0) {
            distanceValue = 1;
        } else if (distanceValue > 500) {
            distanceValue = 500;
        }

        distance_seekbar.setSeekPinByValue(distanceValue);

        if (loggedUser.getFakeAccount()) {
            phone_verification_container.setVisibility(View.GONE);
        }
        chk_studies.setChecked(userSettings.getVisibleEducation());
        chk_job.setChecked(userSettings.getVisibleWork());
        chk_last_name.setChecked(userSettings.getVisibleLastName());
        chk_matches.setChecked(userSettings.getNotifyMatches());
        chk_messages.setChecked(loggedUser.getNotifyPokes());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private void confirmMyUser() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                Bundle phoneBundle = new Bundle();
                phoneBundle.putString(Analytics.Phone.PHONE_PROPERTY.Screen.name(), Analytics.Phone.PHONE_SCREEN.Settings.name());
                mFirebaseAnalytics.logEvent(Analytics.Phone.PHONE_EVENT.Phone.name(), phoneBundle);
                Log.wtf(TAG, account.getId() + " " + account.getPhoneNumber());
                loggedUser.setFakeAccount(true);
                saveUser(loggedUser);
                try {
                    new APIService(SettingsActivity.this).setUserFakeAccount(true, null);
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
    protected void onStop() {
        super.onStop();
        if (loggedUser != null)
            if (!mainUserSetting.toString().equals(userSettings.toString())) {
                Log.d(TAG, "Changed");
                loggedUser.setVisibleEducation(userSettings.getVisibleEducation());
                loggedUser.setVisibleWork(userSettings.getVisibleWork());
                loggedUser.setVisibleLastName(userSettings.getVisibleLastName());
                loggedUser.setNotifyMatches(userSettings.getNotifyMatches());
                loggedUser.setNotifyPokes(userSettings.getNotifyPokes());
                loggedUser.setAgeRange(userSettings.getAgeRange());
                loggedUser.setDistance(userSettings.getDistance());
                saveUser(loggedUser);
                if (mainUserSetting.getAgeRange().get1() != userSettings.getAgeRange().get1() ||
                        mainUserSetting.getAgeRange().get2() != userSettings.getAgeRange().get2() ||
                        mainUserSetting.getDistance() != userSettings.getDistance()) {
                    Log.wtf("SearchInfo", "Changed!");
                    preferenceHelper.putSearchPreferencesChanged();
                }
                try {
                    new APIService(this).patchUser(new JSONObject(new Gson().toJson(userSettings)), new UserInfoHandler() {
                        @Override
                        public void onSuccess(int responseCode, String userResponse) {
                            Log.d(TAG, userResponse);
                        }

                        @Override
                        public void onSuccess(int responseCode, User user) {
                            Log.d(TAG, user.toString());
                        }

                        @Override
                        public void onFailure(boolean isSuccessful, String responseString) {
                            Log.d(TAG, responseString.toString());
                        }
                    });
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }

    }

    @Override
    public void onClick(View v) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        intentBuilder.setShowTitle(true);
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, android.R.color.white));
        intentBuilder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        switch (v.getId()) {
            case R.id.txt_phone_validation:
                phoneValidation();
                break;
            case R.id.txt_share_app:
                shareApp();
                break;
            case R.id.txt_help:
                customTabsIntent.launchUrl(this, Uri.parse(Constants.URL.HELP));
                break;
            case R.id.txt_terms:
                customTabsIntent.launchUrl(this, Uri.parse(Constants.URL.TERMS));
                break;
            case R.id.txt_privacy:
                customTabsIntent.launchUrl(this, Uri.parse(Constants.URL.PRIVACY));
                break;
            case R.id.txt_rate_muapp:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.btn_delete_account_settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.lbl_delete_account))
                        .setMessage(getString(R.string.lbl_delete_account_content))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteUserAccount();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            case R.id.btn_logout_settings:
                exitFromApp();
                break;
        }
    }

    private void shareApp() {
        if (User.Gender.getGender(loggedUser.getGender()) == User.Gender.Male) {
            String shareBody = getString(R.string.lbl_share_app_body);
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.lbl_share_app)));
        } else {
            final SpannableString s = new SpannableString(loggedUser.getCodeUser() + "\n\n" + getString(R.string.lbl_share_dialog_content_settings));
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            s.setSpan(boldSpan, 0, loggedUser.getCodeUser().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new RelativeSizeSpan(1.6f), 0, loggedUser.getCodeUser().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.lbl_send_your_personal_code))
                    //.setMessage(loggedUser.getCodeUser() + " " + getString(R.string.lbl_share_dialog_content_settings))
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.lbl_share), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String message = "";
                            try {
                                message = String.format(getString(R.string.lbl_use_my_code_settings), loggedUser.getCodeUser());
                            } catch (Exception x) {
                                message = x.getMessage();
                                x.printStackTrace();
                            }
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
                            startActivity(Intent.createChooser(sharingIntent, getString(R.string.lbl_share)));
                        }
                    }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chk_studies:
                userSettings.setVisibleEducation(isChecked);
                break;
            case R.id.chk_job:
                userSettings.setVisibleWork(isChecked);
                break;
            case R.id.chk_last_name:
                userSettings.setVisibleLastName(isChecked);
                break;
            case R.id.chk_matches:
                userSettings.setNotifyMatches(isChecked);
                break;
            case R.id.chk_messages:
                userSettings.setNotifyPokes(isChecked);
                break;
        }
        Log.d(TAG, userSettings.toString());
    }

    private void exitFromApp() {

        try {
            JSONObject tokenUser = new JSONObject();
            tokenUser.put("push_token", "");
            new APIService(this).patchUser(tokenUser, null);
        } catch (Exception x) {
            x.printStackTrace();
        }

        new PreferenceHelper(SettingsActivity.this).clear();
        LoginManager.getInstance().logOut();
        hideProgressDialog();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        saveUser(null);
        finish();
    }

    private void deleteUserAccount() {
        showProgressDialog(getString(R.string.lbl_please_wait), getString(R.string.lbl_deleting_your_account));
        new APIService(this).deleteUser(new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                Log.wtf("deleteUserAccount", userResponse.toString());
                exitFromApp();
            }

            @Override
            public void onSuccess(int responseCode, User user) {

            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {
                Log.wtf("deleteUserAccount", responseString.toString());
                hideProgressDialog();
            }
        });
    }

}
