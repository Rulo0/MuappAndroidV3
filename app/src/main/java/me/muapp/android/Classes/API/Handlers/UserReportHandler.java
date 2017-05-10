package me.muapp.android.Classes.API.Handlers;

import me.muapp.android.Classes.Internal.ReportResult;

/**
 * Created by rulo on 9/05/17.
 */

public interface UserReportHandler extends APIHandler {
    void onSuccess(int responseCode, ReportResult result);
}
