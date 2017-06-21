package me.muapp.android.Classes.FirebaseMessaging;

import me.muapp.android.Classes.Util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.UserHelper;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

/**
 * Created by rulo on 07/06/17.
 */

public class MuappInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.wtf(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        UserHelper helper = new UserHelper(getBaseContext());
        User thisUser = helper.getLoggedUser();
        if (thisUser != null) {
            FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(thisUser.getId())).child("pushToken").setValue(token);
        }
    }
}
