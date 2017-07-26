package me.muapp.android.Classes.API.Handlers;

/**
 * Created by rulo on 14/06/17.
 */

public interface BlockHandler extends APIHandler {
    void onSuccess(String jsonObject);

    void onResponseError(Exception x);
}
