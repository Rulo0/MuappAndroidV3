package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import me.muapp.android.R;
import me.muapp.android.UI.Fragment.OauthInstagramDialog;

public class AddPhotoActivity extends BaseActivity {
    Button btn_spotify_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        btn_spotify_connect = (Button) findViewById(R.id.btn_spotify_connect);
        btn_spotify_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OauthInstagramDialog instagramDialog = new OauthInstagramDialog();
                instagramDialog.setUserId(loggedUser.getId());
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                instagramDialog.show(ft, "dialog");
            }
        });
    }
}
