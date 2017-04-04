package me.muapp.android.UI.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;

/**
 * Created by rulo on 22/03/17.
 */

public class BaseActivity extends AppCompatActivity implements ConnectivityChangeListener {
    public static final String TAG = "BASE ACTIVITY";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 841;
    boolean isInternetAvaliable;
    ProgressDialog dialog;
    User loggedUser;
    PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            ConnectionBuddyCache.clearLastNetworkState(this);
        }
        loggedUser = new UserHelper(this).getLoggedUser();
        preferenceHelper = new PreferenceHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register for connectivity changes
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this);
        isInternetAvaliable = ConnectionBuddy.getInstance().hasNetworkConnection();
    }

    public void saveUser(User u) {

        if (u != null) {
            Log.d("saveUser", u.toString());
            new UserHelper(this).saveUser(u);
        } else {
            Log.d("saveUser", "log out");
            new UserHelper(this).logOut();
        }
        loggedUser = u;
    }

    @Override
    protected void onStop() {
        // Unregister from connectivity events
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this);
        super.onStop();
    }

    void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle(getString(R.string.lbl_please_wait));
        dialog.setMessage(getString(R.string.lbl_loading_information));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    void showProgressDialog(String title, String content) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    void hideProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
    }


    /**
     * Override this method if you want to manually handle connectivity change events.
     *
     * @param event ConnectivityEvent which holds all data about network connection state.
     */
    @Override
    public void onConnectionChange(ConnectivityEvent event) {
        if (event.getState() == ConnectivityState.CONNECTED)
            isInternetAvaliable = true;
        else
            isInternetAvaliable = false;
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("CheckPlayServices", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
