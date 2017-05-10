package me.muapp.android.UI.Fragment.Interface;

import me.muapp.android.Classes.Internal.MatchingUser;

/**
 * Created by rulo on 5/05/17.
 */

public interface OnMatchingInteractionListener {
    void onLikeClicked(MatchingUser user);

    void onUnlikeClicked(MatchingUser user);

    void onCrushCliched(MatchingUser user);

    void onReportedUser();
}
