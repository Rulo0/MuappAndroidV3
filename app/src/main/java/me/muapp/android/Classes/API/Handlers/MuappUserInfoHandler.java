package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.MatchingUser;

/**
 * Created by rulo on 22/03/17.
 */

public interface MuappUserInfoHandler extends APIHandler {
    void onSuccess(int responseCode, MatchingUser muappuser);
}
