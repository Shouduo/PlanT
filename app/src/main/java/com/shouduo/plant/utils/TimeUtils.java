package com.shouduo.plant.utils;

import android.content.Context;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public class TimeUtils {
    // data
    private boolean dayTime;
    private static final String PREFERENCE_NAME = "time_preference";
    private static final String KEY_DAY_TIME = "day_time";

    /** <br> data. */

    private TimeUtils(Context context) {
        getLastDayTime(context);
    }

    private TimeUtils getLastDayTime(Context context) {
        dayTime = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_DAY_TIME, true);
        return this;
    }

    public boolean isDayTime() {
        return dayTime;
    }

    /** <br> singleton. */

    private static TimeUtils instance;

    public static synchronized TimeUtils getInstance(Context context) {
        synchronized (TimeUtils.class) {
            if (instance == null) {
                instance = new TimeUtils(context);
            }
        }
        return instance;
    }
}
