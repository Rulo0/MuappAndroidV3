package me.muapp.android.Classes.Util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import me.muapp.android.Classes.Util.Log;
import android.view.View;

/**
 * Created by rulo on 5/04/17.
 */

public class ProgressUtil {
    View contentView;
    View progressview;
    int shortAnimTime;

    public ProgressUtil(Context context, View contentView, View progressview) {
        this.contentView = contentView;
        this.progressview = progressview;
        this.shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
                contentView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                progressview.setVisibility(show ? View.VISIBLE : View.GONE);
                progressview.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressview.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                progressview.setVisibility(show ? View.VISIBLE : View.GONE);
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        } catch (Exception x) {
            Log.wtf("ProgressUtil", x.getMessage());
            x.printStackTrace();
        }
    }
}
