package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.MatchingResult;

/**
 * Created by rulo on 22/03/17.
 */

public interface MatchingUsersHandler extends APIHandler {
    void onSuccess(int responseCode, MatchingResult result);
}
