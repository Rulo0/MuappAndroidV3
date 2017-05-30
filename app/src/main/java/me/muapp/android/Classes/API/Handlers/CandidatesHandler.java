package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.CandidatesResult;

/**
 * Created by rulo on 22/03/17.
 */

public interface CandidatesHandler extends APIHandler {
    void onSuccess(int responseCode, CandidatesResult result);
}
