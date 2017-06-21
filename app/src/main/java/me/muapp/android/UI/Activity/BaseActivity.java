package me.muapp.android.UI.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import me.muapp.android.Classes.Util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.cache.ConnectionBuddyCache;
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener;
import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.models.ConnectivityState;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

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
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            ConnectionBuddyCache.clearLastNetworkState(this);
        }
        loggedUser = new UserHelper(this).getLoggedUser();
        preferenceHelper = new PreferenceHelper(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //for FirebasePresence
        if (loggedUser != null && loggedUser.getId() != null) {
            DatabaseReference usrPresence = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(loggedUser.getId())).child("online");
            usrPresence.onDisconnect().setValue(false, 0d);
            usrPresence.setValue(true);
        }
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
            setUserProperties(u);
        } else {
            Log.d("saveUser", "log out");
            new UserHelper(this).logOut();
        }
        loggedUser = u;
    }

    private void setUserProperties(User u) {
        try {
            SimpleDateFormat formatText = new SimpleDateFormat("yyyy-MM-dd");
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Age.name(), String.valueOf(u.getAge()));
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Birthday.name(), u.getBirthday());
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Candidate.name(), u.getPending() ? "True" : "False");
            //mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Created_At.name(), String.valueOf(u.getAge()));
            //mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Crushes.name(), String.valueOf(u.getAge()));
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Education.name(), u.getEducation());
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.First_Name.name(), u.getFirstName());
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Gender.name(), User.Gender.getGender(u.getGender()) == User.Gender.Female ? "Female" : "Male");
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Invitation_Code.name(), u.getCodeUser());
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Last_Connection.name(), formatText.format(new Date()));
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Last_Name.name(), u.getLastName());
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Location.name(), String.valueOf(u.getAge()));
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Matches.name(), String.valueOf(u.getAge()));
            //mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Muapps.name(), String.valueOf(u.getAge()));
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Notifications.name(), String.valueOf(u.getAge()));
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Phone.name(), String.valueOf(u.getAge()));
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Rate.name(), u.getAverage());
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Used_Code.name(), String.valueOf(u.getAge()));
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.User_ID.name(), String.valueOf(u.getId()));
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Visits.name(), String.valueOf(u.getAge()));
            // mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Voice_Note.name(), String.valueOf(u.getAge()));
            mFirebaseAnalytics.setUserProperty(Analytics.UserProperties.Properties.Work.name(), String.valueOf(u.getWork()));
        } catch (Exception x) {
        }
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
