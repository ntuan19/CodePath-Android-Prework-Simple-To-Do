package com.jamesvuong.todoapp.utils;

import android.content.Context;

import com.jamesvuong.todoapp.R;

import java.text.SimpleDateFormat;

/**
 * Created by jvuonger on 9/22/16.
 */

public class TimeUtils {
    /**
     * Derived from Google I/O's 2015 App
     * https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/util/TimeUtils.java
     *
     * Returns "Today", "Tomorrow", "Yesterday", or a short date format.
     */
    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {
        long localTimestamp, localTime;
        long now = System.currentTimeMillis();

        long dayOrd = timestamp / 86400000L;
        long nowOrd = now / 86400000L;
        int daysAgo = (int) (dayOrd - nowOrd);

        if (dayOrd == nowOrd) {
            return context.getString(R.string.day_title_today);
        } else if (dayOrd == nowOrd - 1) {
            return context.getString(R.string.day_title_yesterday);
        } else if (dayOrd == nowOrd + 1) {
            return context.getString(R.string.day_title_tomorrow);
        } else if (daysAgo > -14 && daysAgo < 14) {
            String relDays;
            if(daysAgo > 0) {
                relDays = "in " + daysAgo + " days";
            } else {
                relDays = Math.abs(daysAgo) + " days ago";
            }
            return relDays;
        } else {
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd, yyyy");
            return fmtOut.format(timestamp);
        }
    }
}
