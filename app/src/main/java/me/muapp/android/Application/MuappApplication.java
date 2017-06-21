package me.muapp.android.Application;

import android.app.Application;
import android.os.StrictMode;
import me.muapp.android.Classes.Util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;

/**
 * Created by rulo on 21/03/17.
 */

public class MuappApplication extends Application {
    private static final String TAG = "MuappApplication";
    public static String DATABASE_REFERENCE = "prodDB";

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       /* if (!BuildConfig.DEBUG) {
            DATABASE_REFERENCE = "prodDB";
        }*/
        Log.wtf("DATABASE_REFERENCE", DATABASE_REFERENCE);
        ConnectionBuddyConfiguration networkInspectorConfiguration =
                new ConnectionBuddyConfiguration.Builder(this)
                        .registerForMobileNetworkChanges(true)
                        .registerForWiFiChanges(true)
                        .setNotifyImmediately(true)
                        .build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);
    }
}
