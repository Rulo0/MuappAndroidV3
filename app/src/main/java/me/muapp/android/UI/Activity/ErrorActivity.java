package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;

import me.muapp.android.R;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "ErrorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoginManager.getInstance().logOut();
        findViewById(R.id.btn_error_ok).setOnClickListener(this);
        findViewById(R.id.txt_error_mail).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_error_ok:
                onBackPressed();
                break;
            case R.id.txt_error_mail:
                sendEmail();
                break;
        }
    }

    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        email.setData(Uri.parse("mailto:"));
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_contact)});
        email.setType("message/frc822");
        startActivity(Intent.createChooser(email, "Email"));
    }
}
