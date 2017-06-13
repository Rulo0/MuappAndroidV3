package me.muapp.android.Classes.Util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

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


    public void showTutorialForView(View view, Boolean cancelable, String title, String subtitle) {
        TapTargetView.showFor(activity,
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
                        .targetRadius(60));
    }
}
