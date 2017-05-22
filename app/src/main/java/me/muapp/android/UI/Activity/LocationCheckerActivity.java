package me.muapp.android.UI.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;

public class LocationCheckerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_LOCATION = 48;
    boolean shouldSendToSettings;
    boolean shouldRedirectToConfirm = false;
    public static String SHOULD_REDIRECT_TO_CONFIRM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_checker);
        shouldRedirectToConfirm = getIntent().getBooleanExtra(SHOULD_REDIRECT_TO_CONFIRM, false);
        findViewById(R.id.btn_request_location).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.hasLocationPermissions(this)) {
            redirectToNext();
        }
    }

    private void redirectToNext() {
        startActivity(new Intent(this, shouldRedirectToConfirm ? ConfirmUserActivity.class : MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (!shouldSendToSettings) {
            if (checkAndRequestPermissions()) {
                redirectToNext();
            }
        } else {
            Intent settingsIntent = new Intent();
            settingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            settingsIntent.setData(uri);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingsIntent);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionFine != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionCoarse != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_LOCATION);
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionsOk = true;
        switch (requestCode) {
            case REQUEST_LOCATION:
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionsOk = false;
                        // user rejected the permission
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            // user also CHECKED "never ask again"
                            shouldSendToSettings = true;
                            break;
                        }
                    }
                }
                if (permissionsOk)
                    redirectToNext();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
