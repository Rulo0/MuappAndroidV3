package me.muapp.android.UI.Fragment.Interface;

import me.muapp.android.Classes.Internal.Candidate;

/**
 * Created by rulo on 10/05/17.
 */

public interface OnCandidateInteractionListener {
    void onLike(Candidate candidate);

    void onUnlike(Candidate candidate);
}
