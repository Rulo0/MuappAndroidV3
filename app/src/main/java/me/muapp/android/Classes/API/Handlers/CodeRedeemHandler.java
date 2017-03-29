package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.CodeRedeemResponse;

/**
 * Created by rulo on 22/03/17.
 */

public interface CodeRedeemHandler extends APIHandler {
    void onSuccess(int responseCode, CodeRedeemResponse redeemResponse);
}
