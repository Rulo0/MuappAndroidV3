package me.muapp.android.Classes.FirebaseMessaging;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

/**
 * Created by rulo on 07/06/17.
 */

public class MuappJobService extends JobService {

    private static final String TAG = "MuappJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}