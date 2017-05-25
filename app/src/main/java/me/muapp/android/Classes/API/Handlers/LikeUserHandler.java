package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.LikeUserResult;

/**
 * Created by rulo on 9/05/17.
 */

public interface LikeUserHandler extends APIHandler {
    void onSuccess(int responseCode, LikeUserResult result);
}
