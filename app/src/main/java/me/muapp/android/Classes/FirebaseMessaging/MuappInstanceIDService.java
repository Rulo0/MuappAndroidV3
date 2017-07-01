package me.muapp.android.Classes.FirebaseMessaging;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.Log;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

/**
 * Created by rulo on 07/06/17.
 */

public class MuappInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.wtf(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        UserHelper helper = new UserHelper(this);
        User thisUser = helper.getLoggedUser();
        if (thisUser != null) {
            new PreferenceHelper(this).putGCMToken(token);
            thisUser.setPushToken(token);
            FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(thisUser.getId())).child("pushToken").setValue(token);
            thisUser.setPushToken(token);
            JSONObject tokenUser = new JSONObject();
            try {
                tokenUser.put("push_token", token);
                new APIService(this).patchUser(tokenUser, null);
            } catch (Exception x) {
            }
        }
    }
}
