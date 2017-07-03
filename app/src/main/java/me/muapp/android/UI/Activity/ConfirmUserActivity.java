package me.muapp.android.UI.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import me.muapp.android.Classes.Util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.Constants;
import me.muapp.android.Classes.Util.LoginHelper;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.ResultReceivers.AddressResultReceiver;
import me.muapp.android.Services.FetchAddressIntentService;

import static me.muapp.android.Classes.Util.Utils.serializeUser;
import static me.muapp.android.UI.Activity.LocationCheckerActivity.SHOULD_REDIRECT_TO_CONFIRM;

public class ConfirmUserActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ConfirmUserActivity";
    private static final int REQUEST_GENDER = 19;
    public static final String CONFIRMED_GENDER = "CONFIRMED_GENDER";
    TextView txt_gender, txt_age;
    Button btn_correct_info, btn_sync;
    private static final int REQUEST_CHECK_SETTINGS = 791;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;
    private String mLastUpdateTime;
    private AddressResultReceiver mResultReceiver;
    private static final String LOCATION_KEY = "LOCATION";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_TIME_UPDATED";

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
        if (Utils.hasLocationPermissions(this)) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                if (Utils.hasLocationPermissions(ConfirmUserActivity.this)) {
                                    getLocation();
                                } else {
                                    requestPermissions();
                                }
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                            }
                        })
                        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(LocationServices.API)
                        .build();
            }
            updateValuesFromBundle(savedInstanceState);
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
           /* DialogLocation lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            Log.v(TAG, lat + " - " + lon);*/
            createLocationRequest();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Nearby");
            mResultReceiver = new AddressResultReceiver(new android.os.Handler());
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    startIntentService(mResultReceiver, location);
                    mCurrentLocation = location;
                    new PreferenceHelper(ConfirmUserActivity.this).putLocation(location);
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    Log.wtf(TAG, location.getLatitude() + " - " + location.getLongitude() + " @ " + mLastUpdateTime);
                }
            });
        } else {
            requestPermissions();
        }
    }

    protected void startIntentService(AddressResultReceiver rec, Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.Location.RECEIVER, rec);
        intent.putExtra(Constants.Location.LOCATION_DATA_EXTRA, location);
        Log.v(TAG, location.toString());
        startService(intent);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5 * 60 * 1000);
        mLocationRequest.setFastestInterval(5 * 60 * 500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // DialogLocation settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    ConfirmUserActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // DialogLocation settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void requestPermissions() {
        Intent redirectIntent = new Intent(this, LocationCheckerActivity.class);
        redirectIntent.putExtra(SHOULD_REDIRECT_TO_CONFIRM, true);
        startActivity(redirectIntent);
        finish();
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
        mFirebaseAnalytics.logEvent(Analytics.Login.LOGIN_EVENTS.Login_Confirm_Confirm.toString(), null);
        new APIService(this).confirmUser(loggedUser, new PreferenceHelper(this).getLocation(), new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                try {
                    JSONObject response = new JSONObject(userResponse);
                    if (response.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                        if (u != null) {
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
                                    startActivity(new Intent(ConfirmUserActivity.this, Utils.hasLocationPermissions(ConfirmUserActivity.this) ? MainActivity.class : LocationCheckerActivity.class));
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
        mFirebaseAnalytics.logEvent(Analytics.Login.LOGIN_EVENTS.Login_Confirm_Synchronise.toString(), null);
        new APIService(this).loginToMuapp(new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                try {
                    JSONObject response = new JSONObject(userResponse);
                    if (response.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                        if (u != null) {
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


    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected() && mLocationListener != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, mLocationListener);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getLocation();
        }
    }
}
