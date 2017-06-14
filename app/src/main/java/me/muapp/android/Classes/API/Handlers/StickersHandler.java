package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Chat.MuappStickers;

/**
 * Created by rulo on 14/06/17.
 */

public interface StickersHandler extends APIHandler {
    void onSuccess(int responseCode, MuappStickers muappStickers);
}
