package com.shouduo.plant;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

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
    private boolean autoSync;
    private int humLimit;
    private int brightLimit;
    private int tempLimit;
    private boolean colorNavigationBar;
    private boolean fahrenheit;

    public static final String DEFAULT_TODAY_FORECAST_TIME = "07:00";
    public static final String DEFAULT_TOMORROW_FORECAST_TIME = "21:00";

    /** <br> life cycle. */

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName();
        if (!TextUtils.isEmpty(processName)
                && processName.equals(this.getPackageName())) {
            initialize();
        }
    }

    private void initialize() {
        instance = this;
        activityList = new ArrayList<>();
        LitePal.initialize(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        colorNavigationBar = sharedPreferences.getBoolean(getString(R.string.key_navigationBar_color), false);
//        LanguageUtils.setLanguage(this, sharedPreferences.getString(getString(R.string.key_language), "follow_system"));
//        fahrenheit = sharedPreferences.getBoolean(getString(R.string.key_fahrenheit), false);
        autoSync = sharedPreferences.getBoolean("auto_sync", false);
        humLimit = sharedPreferences.getInt("hum_limit", 0);
        brightLimit = sharedPreferences.getInt("bright_limit", 0);
        tempLimit = sharedPreferences.getInt("temp_limit", -50);
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
        return autoSync;
    }

    public int getHumLimit() {
        return humLimit;
    }

    public int getBrightLimit() {
        return brightLimit;
    }

    public int getTempLimit() {
        return tempLimit;
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
