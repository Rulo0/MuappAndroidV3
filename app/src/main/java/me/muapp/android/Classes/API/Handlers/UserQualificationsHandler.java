package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;

/**
 * Created by rulo on 22/03/17.
 */

public interface UserQualificationsHandler extends APIHandler {
    void onSuccess(int responseCode, UserQualifications qualifications);
}
