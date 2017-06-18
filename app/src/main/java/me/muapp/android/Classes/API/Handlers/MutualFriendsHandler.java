package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.MutualFriends;

/**
 * Created by rulo on 14/06/17.
 */

public interface MutualFriendsHandler extends APIHandler {
    void onSuccess(int responseCode, MutualFriends mutualFriends);
}
