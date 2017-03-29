package me.muapp.android.Classes.Util;

import android.util.Log;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rulo on 22/03/17.
 */

public class Utils {
    private static final String jsonUseInvitation = "has_use_invitation";

    public static String serializeUser(JSONObject user) {
        try {
            int has_use_invitation = user.getInt(jsonUseInvitation);
            if (has_use_invitation > 0)
                user.put(jsonUseInvitation, true);
            else
                user.put(jsonUseInvitation, false);
        } catch (Exception x) {
        }
        Log.v("serializeUser", user.toString());
        return user.toString();
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }
}
