package com.shouduo.plant.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.model.Base;
import com.shouduo.plant.service.NotificationService;
import com.shouduo.plant.view.Dialog.ClearAllDialog;
import com.shouduo.plant.view.Dialog.ConditionSetterDialog;
import com.shouduo.plant.view.Dialog.DateSetterDialog;
import com.shouduo.plant.view.Dialog.TimeSetterDialog;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 刘亨俊 on 17.2.13.
 */

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, TimeSetterDialog.OnTimeChangedListener,
        DateSetterDialog.OnDateChangedListener, ConditionSetterDialog.OnLimitChangedListener{

    /** <br> life cycle. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance());
        initBasicPart(sharedPreferences);
        initNotificationPart(sharedPreferences);
    }

    /** <br> UI. */
    private void initBasicPart(SharedPreferences sharedPreferences) {
        Preference autoSync = findPreference("auto_sync");
        autoSync.setOnPreferenceChangeListener(this);

        Preference setFirstDay = findPreference("first_day");
        setFirstDay.setOnPreferenceChangeListener(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String startDate = formatter.format(new Date(DataSupport.findFirst(Base.class).startTime));
        setFirstDay.setSummary(startDate);

        Preference clearAll = findPreference("clear_all");
        clearAll.setOnPreferenceChangeListener(this);
    }

    private void initNotificationPart(SharedPreferences sharedPreferences) {

        Preference conditions = findPreference("conditions");
        //conditions summary text format.
        int humLimit = sharedPreferences.getInt("hum_limit", 0);
        int brightLimit = sharedPreferences.getInt("bright_limit", 0);
        int tempLimit = sharedPreferences.getInt("temp_limit", -50);
        String humSummary = "Soil Humidity belows " + humLimit + " %";
        String brightSummary = "Environment Brightness belows " + brightLimit + " lux";
        String tempSummary = "Environment Temperature belows " + tempLimit + " °C";
        StringBuilder summary = new StringBuilder();
        if (humLimit > 0) {
            summary.append(humSummary);
        }
        if (brightLimit > 0) {
            if (summary.length() != 0) {
                summary.append("\n");
            }
            summary.append(brightSummary);
        }
        if (tempLimit > -50) {
            if (summary.length() != 0) {
                summary.append("\n");
            }
            summary.append(tempSummary);
        }
        conditions.setSummary(summary.toString());


        Preference doNotDisturb = findPreference("do_not_disturb");
        //do not disturb summary text format.
        String fromTimeText;
        String toTimeText;
        int fromTimeHour = sharedPreferences.getInt("from_time_hour", PlanT.DEFAULT_FROM_TIME_HOUR);
        int fromTimeMinute = sharedPreferences.getInt("from_time_minute", 0);
        int toTimeHour = sharedPreferences.getInt("to_time_hour", PlanT.DEFAULT_TO_TIME_HOUR);
        int toTimeMinute = sharedPreferences.getInt("to_time_minute", 0);

        fromTimeText = (fromTimeHour > 12 ? (fromTimeHour - 12) : fromTimeHour) + ":"
                + (fromTimeMinute > 9 ? fromTimeMinute : ("0" + fromTimeMinute))
                + (fromTimeHour > 12 ? " PM" : " AM");

        toTimeText = (toTimeHour > 12 ? (toTimeHour - 12) : toTimeHour) + ":"
                + (toTimeMinute > 9 ? toTimeMinute : ("0" + toTimeMinute))
                + (toTimeHour > 12 ? " PM" : " AM");

        doNotDisturb.setSummary(fromTimeText + " - " + toTimeText);

        if (sharedPreferences.getBoolean("send_notification", false)) {     //send notification enabled.
            conditions.setEnabled(true);
            doNotDisturb.setEnabled(true);
        } else {    //send notificaiton disabled.
            conditions.setEnabled(false);
            doNotDisturb.setEnabled(false);
        }
    }

    /** <br> interface. */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance());

        switch (preference.getKey()) {
            case "first_day":
                DateSetterDialog dateSetterDialog = new DateSetterDialog();
                dateSetterDialog.setOnDateChangedListener(this);
                dateSetterDialog.show(getFragmentManager(), null);
                break;

            case "clear_all":
                ClearAllDialog clearAllDialog = new ClearAllDialog();
                clearAllDialog.show(getFragmentManager(), null);
                break;

            case "send_notification":
                initNotificationPart(sharedPreferences);
                Intent intent = new Intent(getActivity(), NotificationService.class);
                if (sharedPreferences.getBoolean("send_notification", false)) {
                    getActivity().startService(intent);
                } else {
                    getActivity().stopService(intent);
                }
                break;

            case "conditions":
                ConditionSetterDialog conditionSetterDialog = new ConditionSetterDialog();
                conditionSetterDialog.setOnLimitChangedListener(this);
                conditionSetterDialog.show(getFragmentManager(), null);
                break;

            case "do_not_disturb":
                TimeSetterDialog timeSetterDialog = new TimeSetterDialog();
                timeSetterDialog.setOnTimeChangedListener(this);
                timeSetterDialog.show(getFragmentManager(), null);
                break;

            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return true;
    }

    @Override
    public void timeChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance());
        this.initNotificationPart(sharedPreferences);
    }

    @Override
    public void dateChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance());
        this.initBasicPart(sharedPreferences);
    }

    @Override
    public void limitChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance());
        this.initNotificationPart(sharedPreferences);
    }
}
