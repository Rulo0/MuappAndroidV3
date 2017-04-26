package me.muapp.android.UI.Fragment.Interface;

import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by rulo on 24/03/17.
 */

public interface OnImageSelectedListener {
    void onImageSelected(String url, Uri uri, int mediaType, ImageView container);
}
