package com.shouduo.plant;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.shouduo.plant.service.NotificationService;
import com.shouduo.plant.view.activity.BaseActivity;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘亨俊 on 17.2.14.
 */

public class PlanT extends Application {
    // data
    private List<BaseActivity> activityList;
    private SharedPreferences sharedPreferences;
//    private boolean autoSync;
//    private boolean sendNotification;
//    private int humLimit;
//    private int brightLimit;
//    private int tempLimit;
//    private int fromTimeHour;
//    private int fromTimeMinute;
//    private int toTimeHour;
//    private int toTimeMinute;
//
//    private boolean colorNavigationBar;
//    private boolean fahrenheit;

    public static final boolean DEFAULT_AUTO_SYNC = false;
    public static final boolean DEFAULT_SEND_NOTIFICATION = false;
    public static final int DEFAULT_HUM_LIMIT = 0;
    public static final int DEFAULT_BRIGHT_LIMIT = 0;
    public static final int DEFAULT_TEMP_LIMIT = -50;
    public static final int DEFAULT_FROM_TIME_HOUR = 23;
    public static final int DEFAULT_FROM_TIME_MINUTE = 0;
    public static final int DEFAULT_TO_TIME_HOUR = 8;
    public static final int DEFAULT_TO_TIME_MINUTE = 0;

    private static final String TAG = "PlanT";

    /** <br> life cycle. */

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName();
        if (!TextUtils.isEmpty(processName)
                && processName.equals(this.getPackageName())) {
            initialize();
        }
        if (isSendNotification()) {
            Intent notificationService = new Intent(this, NotificationService.class);
            startService(notificationService);
        }
    }

    private void initialize() {
        instance = this;
        activityList = new ArrayList<>();
        LitePal.initialize(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance());
//        colorNavigationBar = sharedPreferences.getBoolean(getString(R.string.key_navigationBar_color), false);
//        LanguageUtils.setLanguage(this, sharedPreferences.getString(getString(R.string.key_language), "follow_system"));
//        fahrenheit = sharedPreferences.getBoolean(getString(R.string.key_fahrenheit), false);
    }

    /** <br> data. */

    public void addActivity(BaseActivity a) {
        activityList.add(a);
    }

    public void removeActivity() {
        activityList.remove(activityList.size() - 1);
    }

    public BaseActivity getTopActivity() {
        if (activityList.size() == 0) {
            return null;
        }
        return activityList.get(activityList.size() - 1);
    }

    public BaseActivity getMainActivity() {
        if (activityList.size() == 0) {
            return null;
        }
        return activityList.get(0);
    }

//    public void recreateMainActivity() {
//        activityList.get(0).recreate();
//    }

    public boolean isAutoSync() {
        return sharedPreferences.getBoolean("auto_sync", DEFAULT_AUTO_SYNC);
    }

    public boolean isSendNotification() {
        return sharedPreferences.getBoolean("send_notification", DEFAULT_SEND_NOTIFICATION);
    }

    public int getHumLimit() {
        return sharedPreferences.getInt("hum_limit", DEFAULT_HUM_LIMIT);
    }

    public int getBrightLimit() {
        return sharedPreferences.getInt("bright_limit", DEFAULT_BRIGHT_LIMIT);
    }

    public int getTempLimit() {
        return sharedPreferences.getInt("temp_limit", DEFAULT_TEMP_LIMIT);
    }

    public int getFromTimeHour() {
        return sharedPreferences.getInt("from_time_hour", DEFAULT_FROM_TIME_HOUR);
    }

    public int getFromTimeMinute() {
        return sharedPreferences.getInt("from_time_minute", DEFAULT_FROM_TIME_MINUTE);
    }

    public int getToTimeHour() {
        return sharedPreferences.getInt("to_time_hour", DEFAULT_TO_TIME_HOUR);
    }

    public int getToTimeMinute() {
        return sharedPreferences.getInt("to_time_minute", DEFAULT_TO_TIME_MINUTE);
    }

    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** <br> singleton. */

    private static PlanT instance;

    public static PlanT getInstance() {
        return instance;
    }
}
