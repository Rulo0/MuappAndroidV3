package me.muapp.android.Classes.Util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.muapp.android.R;

/**
 * Created by rulo on 4/04/17.
 */

public class DateUtils {
    private static Context context;

    public DateUtils(Context theContext) {
        context = theContext;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public static String getMatchTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return "";
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + context.getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + context.getString(R.string.a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + String.format(context.getString(R.string.minutes_ago), diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + context.getString(R.string.a_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + String.format(context.getString(R.string.hours_ago), diff / HOUR_MILLIS);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + context.getString(R.string.a_day_ago);
        } else if (diff < 7 * DAY_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + context.getString(R.string.week_ago);
        } else if (diff < 30 * DAY_MILLIS) {
            return context.getString(R.string.chat_match_at) + " " + String.format(context.getString(R.string.days_ago), diff / DAY_MILLIS);
        } else {
            return context.getString(R.string.matched_at) + " " + dateFormat.format(new Date(time));
        }
    }

    public static String getMatchCrushTimeAgo(long time, Context ctx, Boolean isCrush) {
        if (isCrush == null)
            isCrush = false;
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return "";
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + context.getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + context.getString(R.string.a_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + String.format(context.getString(R.string.minutes_ago), diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + context.getString(R.string.a_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + String.format(context.getString(R.string.hours_ago), diff / HOUR_MILLIS);
        } else if (diff < 48 * HOUR_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + context.getString(R.string.a_day_ago);
        } else if (diff < 30 * DAY_MILLIS) {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + String.format(context.getString(R.string.days_ago), diff / DAY_MILLIS);
        } else {
            return ctx.getString(isCrush ? R.string.chat_crush_at : R.string.chat_match_at) + " " + String.valueOf(dateFormat.format(new Date(time)));
        }
    }
}
