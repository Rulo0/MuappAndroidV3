package me.muapp.android.UI.Adapter.UserPhotos;

/**
 * Created by rulo on 27/03/17.
 */

interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
