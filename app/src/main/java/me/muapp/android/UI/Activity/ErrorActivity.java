package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import me.muapp.android.R;

public class ErrorActivity extends BaseActivity implements View.OnClickListener {
    static final String TAG = "ErrorActivity";
    public static final String ERROR_EXTRA = "ERROR_EXTRA";
    public static final String ERROR_SHOW_EMAIL = "ERROR_SHOW_EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LoginManager.getInstance().logOut();
        saveUser(null);
        if (getIntent().hasExtra(ERROR_EXTRA))
            ((TextView) findViewById(R.id.txt_error_description)).setText(getIntent().getStringExtra(ERROR_EXTRA));

        if (!getIntent().getBooleanExtra(ERROR_SHOW_EMAIL, true)) {
            findViewById(R.id.txt_error_mail).setVisibility(View.GONE);
        }
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
