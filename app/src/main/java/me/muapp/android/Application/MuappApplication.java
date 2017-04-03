package me.muapp.android.Application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.zplesac.connectionbuddy.ConnectionBuddy;
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration;

import io.realm.Realm;
import me.muapp.android.Classes.Quickblox.QuickbloxHelper;

/**
 * Created by rulo on 21/03/17.
 */

public class MuappApplication extends Application {
    private static final String TAG = "MuappApplication";
    public static MixpanelAPI mixpanelAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        //MIXPANEL TOKEN
        Realm.init(this);
        QuickbloxHelper.init(getApplicationContext());

        String projectToken = "c344cb123c0c544f0c617dc0185c7a5b";
        mixpanelAPI = MixpanelAPI.getInstance(this, projectToken);

        ConnectionBuddyConfiguration networkInspectorConfiguration =
                new ConnectionBuddyConfiguration.Builder(this)
                        .registerForMobileNetworkChanges(true)
                        .registerForWiFiChanges(true)
                        .setNotifyImmediately(true)
                        .build();
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration);
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mixpanelAPI.timeEvent("App Close");
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mixpanelAPI.track("App Close");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}
