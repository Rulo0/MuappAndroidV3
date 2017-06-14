package me.muapp.android.Classes.Util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.R;

/**
 * Created by rulo on 13/06/17.
 */

public class Tutorials {
    Activity activity;
    Typeface GelPen;
    private static final int titleTextSize = 30;
    private static final int descriptionTextSize = 20;
    private static final int textColor = android.R.color.white;
    private static final int dimColor = android.R.color.black;

    public Tutorials(Activity activity) {
        this.activity = activity;
        AssetManager am = this.activity.getApplicationContext().getAssets();
        GelPen = Typeface.createFromAsset(am, String.format("fonts/%s", "GelPen.ttf"));
    }


    public static class MenuItemTutorial {
        int menuItemId;
        String title;
        String subtitle;
        Integer targetRadius;

        public MenuItemTutorial(int menuItemId, String title, String subtitle, Integer targetRadius) {
            this.menuItemId = menuItemId;
            this.title = title;
            this.subtitle = subtitle;
            this.targetRadius = targetRadius;
        }
    }

    public TapTargetView showTutorialForView(View view, Boolean cancelable, String title, String subtitle, Integer targetRadius, @Nullable TapTargetView.Listener listener) {
        return TapTargetView.showFor(activity,
                TapTarget.forView(view, title, subtitle)
                        .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.colorAccent)   // Specify a color for the target circle
                        .titleTextSize(titleTextSize)                  // Specify the size (in sp) of the title text
                        .titleTextColor(textColor)      // Specify the color of the title text
                        .descriptionTextSize(descriptionTextSize)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(textColor)  // Specify the color of the description text
                        .textColor(textColor)            // Specify a color for both the title and description text
                        .textTypeface(GelPen)  // Specify a typeface for the text
                        .dimColor(dimColor)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(cancelable)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(targetRadius != null ? targetRadius : 60),
                listener
        );
    }

    public TapTargetView showTutorialForMenuItem(Toolbar toolbar, int menuItemId, String title, String subtitle, Integer targetRadius, @Nullable TapTargetView.Listener listener) {
        try {
            Log.wtf("Toolbar", toolbar.getId() + " ~ " + menuItemId);
            return TapTargetView.showFor(activity, TapTarget.forToolbarMenuItem(toolbar, menuItemId, title, subtitle)
                            .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.colorAccent)   // Specify a color for the target circle
                            .titleTextSize(titleTextSize)                  // Specify the size (in sp) of the title text
                            .titleTextColor(textColor)      // Specify the color of the title text
                            .descriptionTextSize(descriptionTextSize)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(textColor)  // Specify the color of the description text
                            .textColor(textColor)            // Specify a color for both the title and description text
                            .textTypeface(GelPen)  // Specify a typeface for the text
                            .dimColor(dimColor)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(targetRadius != null ? targetRadius : 60),
                    listener
            );
        } catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }


    private TapTarget getTapTargetMenuItem(Toolbar toolbar, int menuItemId, String title, String subtitle, Integer targetRadius) {
        return TapTarget.forToolbarMenuItem(toolbar, menuItemId, title, subtitle)
                .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.colorAccent)   // Specify a color for the target circle
                .titleTextSize(titleTextSize)                  // Specify the size (in sp) of the title text
                .titleTextColor(textColor)      // Specify the color of the title text
                .descriptionTextSize(descriptionTextSize)            // Specify the size (in sp) of the description text
                .descriptionTextColor(textColor)  // Specify the color of the description text
                .textColor(textColor)            // Specify a color for both the title and description text
                .textTypeface(GelPen)  // Specify a typeface for the text
                .dimColor(dimColor)            // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(true)                   // Whether to draw a drop shadow or not
                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                .tintTarget(true)                   // Whether to tint the target view's color
                .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                .targetRadius(targetRadius != null ? targetRadius : 60);
    }


    public void showTutorialSequence(Toolbar toolbar, TapTargetSequence.Listener listener, MenuItemTutorial... tutorials) {
        try {
            Log.wtf("showTutorialSequence", "Started");
            List<TapTarget> targets = new ArrayList<>();
            for (MenuItemTutorial mit : tutorials) {
                targets.add(getTapTargetMenuItem(toolbar, mit.menuItemId, mit.title, mit.subtitle, mit.targetRadius));
            }

            new TapTargetSequence(activity)
                    .targets(targets)
                    .listener(listener).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
