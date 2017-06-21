package me.muapp.android.ResultReceivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import me.muapp.android.Classes.Util.Log;

import me.muapp.android.Classes.Util.Constants;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by rulo on 11/05/17.
 */

public class AddressResultReceiver extends ResultReceiver {
    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.
        String mAddressOutput = resultData.getString(Constants.Location.RESULT_DATA_KEY);
        Log.v(TAG, mAddressOutput);

        // Show a toast message if an address was found.
        if (resultCode == Constants.Location.SUCCESS_RESULT) {
            Log.v(TAG, "Address Found!");
        }

    }
}
