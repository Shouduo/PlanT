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
        DateSetterDialog.OnDateChangedListener {

    /**
     * <br> life cycle.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initBasicPart(sharedPreferences);
        initNotificationPart(sharedPreferences);
    }

    /**
     * <br> UI.
     */

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

        Preference doNotDisturb = findPreference("do_not_disturb");
        //do not disturb summary text format.
        String fromTimeText;
        String toTimeText;
        int fromTimeHour = sharedPreferences.getInt("from_time_hour", PlanT.DEFAULT_FROM_TIME_HOUR);
        int fromTimeMinute = sharedPreferences.getInt("from_time_minute", 0);
        int toTimeHour = sharedPreferences.getInt("to_time_hour", PlanT.DEFAULT_TO_TIME_HOUR);
        int toTimeMinute = sharedPreferences.getInt("to_time_minute", 0);

        fromTimeText = (fromTimeHour > 12 ? (fromTimeHour - 12) : fromTimeHour) + ":"
                + (fromTimeMinute > 10 ? fromTimeMinute : ("0" + fromTimeMinute))
                + (fromTimeHour > 12 ? " PM" : " AM");

        toTimeText = (toTimeHour > 12 ? (toTimeHour - 12) : toTimeHour) + ":"
                + (toTimeMinute > 10 ? toTimeMinute : ("0" + toTimeMinute))
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

    /**
     * interface.
     */

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
//        if (preference.getKey().equals(getString(R.string.key_language))) {
//            preference.setSummary(ValueUtils.getLanguage(getActivity(), (String) o));
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_restart));
//        } else if (preference.getKey().equals(getString(R.string.key_notification_text_color))) {
//            // notification text color.
//            ServiceHelper.startPollingService(getActivity());
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            preference.setSummary(ValueUtils.getNotificationTextColor(getActivity(), (String) o));
//        }
        return true;
    }

    @Override
    public void timeChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.initNotificationPart(sharedPreferences);


//        if (sharedPreferences.getBoolean(getString(R.string.key_forecast_today), false)) {
//            ServiceHelper.startForecastService(getActivity(), true);
//        }
//        if (sharedPreferences.getBoolean(getString(R.string.key_forecast_tomorrow), false)) {
//            ServiceHelper.startForecastService(getActivity(), false);
//        }
    }

    @Override
    public void dateChanged() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.initBasicPart(sharedPreferences);
    }
}
