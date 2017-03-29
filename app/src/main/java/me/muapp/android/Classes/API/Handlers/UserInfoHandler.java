package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.User;

/**
 * Created by rulo on 22/03/17.
 */

public interface UserInfoHandler extends APIHandler {
    void onSuccess(int responseCode, String userResponse);

    void onSuccess(int responseCode, User user);
}
