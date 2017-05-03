package me.muapp.android.UI.Fragment.Interface;

import android.widget.ImageView;

/**
 * Created by rulo on 24/03/17.
 */

public interface OnProfileImageSelectedListener {
    void onCameraSelected(ImageView container, int adapterPosition);

    void onGallerySelected(ImageView container, int adapterPosition);

    void onPictureDeleted(ImageView container, int adapterPosition);
}
