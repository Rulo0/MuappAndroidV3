package me.muapp.android.UI.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.eralp.circleprogressview.CircleProgressView;

import java.io.File;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.MuappUserInfoHandler;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;
import me.muapp.android.UI.Fragment.Interface.OnUserReportedListener;
import me.muapp.android.UI.Fragment.ReportUserDialogFragment;
import me.muapp.android.UI.Fragment.ViewUserProfileFragment;

import static me.muapp.android.UI.Activity.ChatActivity.PROFILE_VIEW_RESULT;
import static me.muapp.android.UI.Fragment.CandidatesFragment.CANDIDATE_KEY_RESULT;
import static me.muapp.android.UI.Fragment.CandidatesFragment.CANDIDATE_PROFILE_VIEW_RESULT;

public class ViewProfileActivity extends BaseActivity implements MuappUserInfoHandler, OnFragmentInteractionListener, OnProfileScrollListener, View.OnClickListener, OnUserReportedListener {
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String FROM_CRUSH = "FROM_CRUSH";
    public static final String FROM_MATCH = "FROM_MATCH";
    public static final String FROM_GATE = "FROM_GATE";
    public static final String IS_LIKED_BY_ME = "IS_LIKED_BY_ME";
    public static final String CANDIDATE_PROGRESS = "CANDIDATE_PROGRESS";
    ProgressUtil progressUtil;
    MatchingUser user;
    TextView txt_profile_view_name, txt_candidate_profile_progress;
    ImageView img_profile_view_verified;
    RelativeLayout container_actions_profile, container_gate_thumbs_profile;
    Boolean fromCrush, fromMatch, fromGate, isLikedByMe;
    ImageButton btn_muapp_profile, btn_no_muapp_profile, btn_profile_gate_accept, btn_profile_gate_deny;
    ProgressBar view_profile_progress;
    CircleProgressView candidate_profile_progress;
    int candidateProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_view);
        setSupportActionBar(toolbar);
        candidate_profile_progress = (CircleProgressView) findViewById(R.id.candidate_profile_progress);
        txt_profile_view_name = (TextView) findViewById(R.id.txt_profile_view_name);
        txt_candidate_profile_progress = (TextView) findViewById(R.id.txt_candidate_profile_progress);
        img_profile_view_verified = (ImageView) findViewById(R.id.img_profile_view_verified);
        container_actions_profile = (RelativeLayout) findViewById(R.id.container_actions_profile);
        container_gate_thumbs_profile = (RelativeLayout) findViewById(R.id.container_gate_thumbs_profile);
        btn_profile_gate_accept = (ImageButton) findViewById(R.id.btn_profile_gate_accept);
        btn_profile_gate_deny = (ImageButton) findViewById(R.id.btn_profile_gate_deny);
        int userId = getIntent().getIntExtra(USER_ID, -1);
        if (userId < 0)
            finish();
        Log.wtf("UserId", userId + "");
        txt_profile_view_name.setText(getIntent().getStringExtra(USER_NAME));
        fromCrush = getIntent().getBooleanExtra(FROM_CRUSH, false);
        fromMatch = getIntent().getBooleanExtra(FROM_MATCH, false);
        fromGate = getIntent().getBooleanExtra(FROM_GATE, false);
        isLikedByMe = getIntent().getBooleanExtra(IS_LIKED_BY_ME, false);
        candidateProgress = getIntent().getIntExtra(CANDIDATE_PROGRESS, 0);
        new APIService(this).getFullUser(userId, this);
        view_profile_progress = (ProgressBar) findViewById(R.id.view_profile_progress);
        btn_muapp_profile = (ImageButton) findViewById(R.id.btn_muapp_profile);
        btn_no_muapp_profile = (ImageButton) findViewById(R.id.btn_no_muapp_profile);
        btn_muapp_profile.setOnClickListener(this);
        btn_no_muapp_profile.setOnClickListener(this);
        btn_profile_gate_accept.setOnClickListener(this);
        btn_profile_gate_deny.setOnClickListener(this);
        candidate_profile_progress.setProgress(candidateProgress);
        txt_candidate_profile_progress.setText(candidateProgress + "%");
        progressUtil = new ProgressUtil(this, findViewById(R.id.content_profile_view), view_profile_progress);
        progressUtil.showProgress(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_view_menu, menu);
        MenuItem uncrush = menu.findItem(R.id.action_uncrush_profile);
        MenuItem unmatch = menu.findItem(R.id.action_unmatch_profile);
        MenuItem report = menu.findItem(R.id.action_report_profile);
        uncrush.setVisible(false);
        unmatch.setVisible(false);
        if (fromMatch)
            unmatch.setVisible(true);
        if (fromCrush)
            uncrush.setVisible(true);
        if (User.Gender.getGender(loggedUser.getGender()) != User.Gender.Female)
            report.setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }


    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onFailure(boolean isSuccessful, String responseString) {
        finish();
    }

    @Override
    public void onSuccess(int responseCode, final MatchingUser muappuser) {
        user = muappuser;
        Log.w("GettingUser", muappuser.toString());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_profile_view, ViewUserProfileFragment.newInstance(muappuser));
        ft.commit();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(ViewProfileActivity.this).load(user.getAlbum().get(0)).downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        showControls(true);
                        progressUtil.showProgress(false);
                        if (muappuser.getFakeAccount() != null && muappuser.getFakeAccount()) {
                            img_profile_view_verified.setImageResource(R.drawable.ic_verified_profile);
                            img_profile_view_verified.setVisibility(View.VISIBLE);
                            img_profile_view_verified.setPadding(0, 0, dpToPx(4), 0);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        showControls(true);
                        progressUtil.showProgress(false);
                        if (muappuser.getFakeAccount() != null && muappuser.getFakeAccount()) {
                            img_profile_view_verified.setImageResource(R.drawable.ic_verified_profile);
                            img_profile_view_verified.setVisibility(View.VISIBLE);
                            img_profile_view_verified.setPadding(0, 0, dpToPx(4), 0);
                        }
                    }
                });
            }
        });


    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }

    private void showConfirmDialog(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle(item.getTitle())
                .setMessage(item.getItemId() == R.id.action_unmatch_profile ? R.string.lbl_confirm_unmatch : R.string.lbl_confirm_uncrush)
                .setCancelable(true)
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra(PROFILE_VIEW_RESULT, false);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        })
                .setNegativeButton(
                        android.R.string.cancel, null).create().show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_unmatch_profile:
            case R.id.action_uncrush_profile:
                showConfirmDialog(item);
                break;
            case R.id.action_report_profile:
                ReportUserDialogFragment reportUserDialogFragment = ReportUserDialogFragment.newInstance(user.getId());
                reportUserDialogFragment.setOnUserReportedListener(this);
                reportUserDialogFragment.show(getSupportFragmentManager(), "REPORT");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showControls(final Boolean show) {
        if (fromCrush && !isLikedByMe) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.animViewScale(ViewProfileActivity.this, container_actions_profile, show);
                }
            });
        }

        if (fromGate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.animViewScale(ViewProfileActivity.this, container_gate_thumbs_profile, show);
                }
            });
        }
    }

    @Override
    public void onScrollToTop() {
        Log.wtf("VIEW PROFILE", "Scroll top");
        showControls(true);
    }

    @Override
    public void onScroll() {
        Log.wtf("VIEW PROFILE", "Scroll");
        showControls(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_profile_gate_accept || v.getId() == R.id.btn_profile_gate_deny) {
            Log.wtf("Clicked", "AcceptOrDeny");
            Intent profileReturnIntent = new Intent();
            profileReturnIntent.putExtra(CANDIDATE_PROFILE_VIEW_RESULT, v.getId() == R.id.btn_profile_gate_accept);
            profileReturnIntent.putExtra(CANDIDATE_KEY_RESULT, user.getId());
            setResult(Activity.RESULT_OK, profileReturnIntent);
            finish();
        } else {
            Bundle params = new Bundle();
            params.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
            params.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.User_Profile_Crushed.toString());
            mFirebaseAnalytics.logEvent(v.getId() == R.id.btn_muapp_profile ? Analytics.Muapp.MUAPP_EVENT.Muapp.toString() : Analytics.Muapp.MUAPP_EVENT.Dismiss.toString(), params);
            Intent profileReturnIntent = new Intent();
            profileReturnIntent.putExtra(PROFILE_VIEW_RESULT, v.getId() == R.id.btn_muapp_profile);
            setResult(Activity.RESULT_OK, profileReturnIntent);
            finish();
        }
    }

    @Override
    public void onReport() {
        String reportScreen = Analytics.Report.REPORT_SCREEN.User_Profile_New.toString();
        if (fromCrush) {
            reportScreen = Analytics.Report.REPORT_SCREEN.User_Profile_Crushed.toString();
        } else if (fromMatch) {
            reportScreen = Analytics.Report.REPORT_SCREEN.User_Profile_Matched.toString();
        }

        Bundle reportBundle = new Bundle();
        reportBundle.putString(Analytics.Report.REPORT_PROPERTY.Screen.toString(), reportScreen);
        mFirebaseAnalytics.logEvent(Analytics.Report.REPORT_EVENT.Report.toString(), reportBundle);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.lbl_thank_you)
                .setCancelable(false)
                .setMessage(R.string.lbl_your_report_will_be_analized)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent profileReturnIntent = new Intent();
                        profileReturnIntent.putExtra(PROFILE_VIEW_RESULT, false);
                        profileReturnIntent.putExtra(CANDIDATE_KEY_RESULT, user.getId());
                        setResult(Activity.RESULT_OK, profileReturnIntent);
                        finish();
                    }
                });
        builder.create().show();
    }
}
