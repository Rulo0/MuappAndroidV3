package me.muapp.android.UI.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
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

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.Internal.CurrentNavigationElement;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Util.Constants;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.Services.FetchAddressIntentService;
import me.muapp.android.UI.Fragment.AddContentDialogFragment;
import me.muapp.android.UI.Fragment.BasicFragment;
import me.muapp.android.UI.Fragment.ChatFragment;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.ProfileFragment;

import static me.muapp.android.R.id.btn_add_youtube;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener, SearchView.OnQueryTextListener, AddContentDialogFragment.Listener {
    private static final int PHONE_REQUEST_CODE = 79;
    public static final String TAG = "MainActivity";
    private CurrentNavigationElement navigationElement;
    private int mSelectedItem;
    HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();
    private Realm realm;
    BottomNavigationView navigation;
    FloatingActionButton fab_add_content;
    ConstraintLayout add_item_layout;
    Toolbar toolbar;
    private static final int REQUEST_CHECK_SETTINGS = 399;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;
    private String mLastUpdateTime;
    private static final int REQUEST_LOCATION = 426;
    private AddressResultReceiver mResultReceiver;
    private static final String LOCATION_KEY = "LOCATION";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "LAST_TIME_UPDATED";

    public FloatingActionButton getFab_add_content() {
        return fab_add_content;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_male);
    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        realm = CacheUtils.getInstance(loggedUser);
        add_item_layout = (ConstraintLayout) findViewById(R.id.add_item_layout);
        add_item_layout.bringToFront();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        fab_add_content = (FloatingActionButton) findViewById(R.id.fab_add_content);
        if (checkPlayServices()) {
            final String token = FirebaseInstanceId.getInstance().getToken();
            if (!TextUtils.equals(new PreferenceHelper(this).getGCMToken(), token) || !loggedUser.getPushToken().equals(token)) {
                FirebaseMessaging.getInstance().subscribeToTopic("android");
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
        fragmentHashMap.put(R.id.navigation_profile, ProfileFragment.newInstance(loggedUser));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_main_male, fragmentHashMap.get(R.id.navigation_home));
        ft.commit();
        fab_add_content.hide();
        navigationElement = new CurrentNavigationElement(navigation.getMenu().findItem(R.id.navigation_home), fragmentHashMap.get(R.id.navigation_home));
        invalidateOptionsMenu();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (checkAndRequestPermissions()) {
                                getLocation();
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
      /*  SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);*/
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
            case R.id.action_settings_profile:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_edit_profile:
                startActivity(new Intent(this, ProfileSettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectFragment(MenuItem item) {
        try {
            Log.wtf("selectFragment", item.getTitle().toString());
            Fragment frag = fragmentHashMap.get(item.getItemId());
            if (frag instanceof ProfileFragment) {
                Log.wtf("selectFragment", "Profile");
            }

            mSelectedItem = item.getItemId();
            if (frag != null) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                if (frag.isAdded()) {
                    ft.show(frag);
                } else {
                    ft.add(R.id.content_main_male, frag, item.getTitle().toString());
                }
                ft.hide(navigationElement.getFrag());
                ft.commit();
            }
            if (frag instanceof ProfileFragment) {
                fab_add_content.show();
            } else {
                fab_add_content.hide();
            }

            fab_add_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddContentDialogFragment.newInstance(false).show(getSupportFragmentManager(), "dialog");
                }
            });
            navigationElement = new CurrentNavigationElement(item, frag);
            invalidateOptionsMenu();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_profile)
            getSupportActionBar().setTitle(loggedUser.getFullName());
        else
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

    @Override
    public void onAddContentClicked(int buttonId) {
        switch (buttonId) {
            case R.id.btn_add_quote:
                startActivity(new Intent(MainActivity.this, AddQuoteActivity.class));
                break;
            case R.id.btn_add_voice:
                startActivity(new Intent(MainActivity.this, AddVoiceNoteActivity.class));
                break;
            case R.id.btn_add_photo:
                startActivity(new Intent(MainActivity.this, AddPhotosActivity.class));
                break;
            case R.id.btn_add_giphy:
                startActivity(new Intent(MainActivity.this, AddGiphyActivity.class));
                break;
            case R.id.btn_add_spotify:
                startActivity(new Intent(MainActivity.this, AddSpotifyActivity.class));
                break;
            case btn_add_youtube:
                startActivity(new Intent(MainActivity.this, AddYoutubeActivity.class));
                break;
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionFine != PackageManager.PERMISSION_GRANTED) {
            Log.v("checkPermissions", "Camera Needed");
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_LOCATION);
            return false;
        }
        return true;
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
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    protected void startIntentService(AddressResultReceiver rec, Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.Location.RECEIVER, rec);
        intent.putExtra(Constants.Location.LOCATION_DATA_EXTRA, location);
        Log.v(TAG, location.toString());
        startService(intent);
    }


    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
           /* Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            Log.v(TAG, lat + " - " + lon);*/
            createLocationRequest();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Nearby");
            mResultReceiver = new AddressResultReceiver(new android.os.Handler());
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    startIntentService(mResultReceiver, location);
                    mCurrentLocation = location;
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    Log.wtf(TAG, location.getLatitude() + " - " + location.getLongitude() + " @ " + mLastUpdateTime);
                }
            });
        }
    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.Location.RESULT_DATA_KEY);
            Log.v(TAG, mAddressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.Location.SUCCESS_RESULT) {
                Log.v(TAG, "Address Found!");
            }

        }
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