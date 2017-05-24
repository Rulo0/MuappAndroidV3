package me.muapp.android.UI.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;

public class ManGateInfoActivity extends BaseActivity {
    Button btn_delete_account_man_gate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_gate_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_delete_account_man_gate = (Button) findViewById(R.id.btn_delete_account_man_gate);
        btn_delete_account_man_gate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManGateInfoActivity.this);
                builder.setTitle(getString(R.string.lbl_delete_account))
                        .setMessage(getString(R.string.lbl_delete_account_content))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteUserAccount();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();

            }
        });
    }

    private void deleteUserAccount() {
        showProgressDialog(getString(R.string.lbl_please_wait), getString(R.string.lbl_deleting_your_account));
        new APIService(this).deleteUser(new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                new PreferenceHelper(ManGateInfoActivity.this).clear();
                saveUser(null);
                LoginManager.getInstance().logOut();
                hideProgressDialog();
                Intent intent = new Intent(ManGateInfoActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onSuccess(int responseCode, User user) {

            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {
                hideProgressDialog();
            }
        });
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
}
