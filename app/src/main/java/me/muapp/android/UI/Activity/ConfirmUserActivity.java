package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.LoginHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;

import static me.muapp.android.Classes.Util.Utils.serializeUser;

public class ConfirmUserActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ConfirmUserActivity";
    private static final int REQUEST_GENDER = 19;
    public static final String CONFIRMED_GENDER = "CONFIRMED_GENDER";
    TextView txt_gender, txt_age;
    Button btn_correct_info, btn_sync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user);
        getSupportActionBar().hide();
        txt_gender = (TextView) findViewById(R.id.txt_gender);
        txt_age = (TextView) findViewById(R.id.txt_age);
        btn_correct_info = (Button) findViewById(R.id.btn_correct_info);
        btn_sync = (Button) findViewById(R.id.btn_sync);
        btn_correct_info.setOnClickListener(this);
        btn_sync.setOnClickListener(this);
        if (loggedUser.validate() == User.ValidationResult.Ok) {
            pupulateData(loggedUser);
        } else {
            validateUser();
        }
        if (checkPlayServices()) {
            final String token = FirebaseInstanceId.getInstance().getToken();
            // if (!TextUtils.equals(new PreferenceHelper(this).getGCMToken(), token)) {
            FirebaseMessaging.getInstance().subscribeToTopic("android");
            loggedUser.setPushToken(token);
            saveUser(loggedUser);
            // }
        }
    }

    private void validateUser() {
        switch (loggedUser.validate()) {
            case NoBirthday:
                Log.i(TAG, "No Birthday");
                break;
            case NoGender:
                Log.i(TAG, "No Gender");
                startActivityForResult(new Intent(this, ConfirmUserGenderActivity.class), REQUEST_GENDER);
                break;
            case NotConfirmed:
                Log.i(TAG, "Not Confirmed");
                pupulateData(loggedUser);
                break;
        }
    }

    private void pupulateData(final User u) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (u != null) {
                    txt_gender.setText(u.getGenderString(ConfirmUserActivity.this));
                    txt_age.setText(String.valueOf(u.getAge()));
                } else {
                    Log.wtf(TAG, "User Is Null");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_correct_info:
                confirmUser();
                break;
            case R.id.btn_sync:
                syncUser();
                break;
        }
    }

    private void confirmUser() {
        showProgressDialog();
        new APIService(this).confirmUser(loggedUser, new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                try {
                    JSONObject response = new JSONObject(userResponse);
                    if (response.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                        if (u != null) {
                            Log.wtf(TAG, u.toString());
                            saveUser(u);
                            if (loggedUser.validate() == User.ValidationResult.Ok) {
                                ConfirmUserActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        new LoginHelper(ConfirmUserActivity.this).performFullLogin();
                                    }
                                });
                                hideProgressDialog();
                                if (loggedUser.getGender() == User.Gender.Male.getValue())
                                    startActivity(new Intent(ConfirmUserActivity.this, ManGateActivity.class));
                                else
                                    startActivity(new Intent(ConfirmUserActivity.this, MainActivity.class));
                                finish();
                            } else {
                                validateUser();
                            }
                        } else {
                            hideProgressDialog();
                            Log.wtf(TAG, "user is null");
                        }
                    }

                } catch (Exception x) {
                    hideProgressDialog();
                    x.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int responseCode, User user) {

            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {

            }
        });
    }

    private void syncUser() {
        new APIService(this).loginToMuapp(new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                try {
                    JSONObject response = new JSONObject(userResponse);
                    if (response.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                        if (u != null) {
                            Log.wtf(TAG, u.toString());
                            new UserHelper(ConfirmUserActivity.this).saveUser(u);
                            validateUser();
                        } else {
                            Log.wtf(TAG, "user is null");
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

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GENDER:
                    Log.wtf(CONFIRMED_GENDER, data.getIntExtra(CONFIRMED_GENDER, User.Gender.Unknown.getValue()) + "");
                    loggedUser.setGender(data.getIntExtra(CONFIRMED_GENDER, User.Gender.Unknown.getValue()));
                    new UserHelper(this).saveUser(loggedUser);
                    break;
            }
        }
        validateUser();
    }
}
