package me.muapp.android.Classes.Util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by rulo on 23/03/17.
 */

public class LoginHelper {
    FirebaseAuth mAuth;
    UserHelper userHelper;
    Context context;

    public LoginHelper(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.userHelper = new UserHelper(context);
    }

    public void performFullLogin() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        MuappApplication.getMixpanelAPI().getPeople().identify(String.valueOf(userHelper.getLoggedUser().getId()));
        MuappApplication.getMixpanelAPI().getPeople().set("last_connection", dateFormat.format(new Date()));
        MuappApplication.getMixpanelAPI().getPeople().set("location", "Enable");
        MuappApplication.getMixpanelAPI().getPeople().set("notifications", null);
        loginToQuickBlox();
        loginToFireBase();
    }

    private void loginToQuickBlox() {
        QuickBloxChatHelper.getInstance().loginToChat(context);
    }

    private void loginToFireBase() {
        int userId = userHelper.getLoggedUser().getId();
        final String email = String.format("usermuapp_%s@muapp.me", userId);
        final String pass = String.format("passMuapp_%s", userId);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        signInFirebase(email, pass);
                    }
                });
    }

    private void signInFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    }
                });
    }

}
