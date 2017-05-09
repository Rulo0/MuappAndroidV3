package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.QualificationResult;

/**
 * Created by rulo on 9/05/17.
 */

public interface UserQualificationHandler extends APIHandler {
    void onSuccess(int responseCode, QualificationResult result);
}
