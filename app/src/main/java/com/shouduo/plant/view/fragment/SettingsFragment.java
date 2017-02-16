package com.shouduo.plant.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.shouduo.plant.R;
import com.shouduo.plant.model.Base;
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
//        initForecastPart(sharedPreferences);
        initNotificationPart(sharedPreferences);
    }

    /**
     * <br> UI.
     */

    private void initBasicPart(SharedPreferences sharedPreferences) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            findPreference(getString(R.string.key_navigationBar_color)).setEnabled(false);
//        } else {
//            findPreference(getString(R.string.key_navigationBar_color)).setEnabled(true);
//        }
        Preference autoSync = findPreference("auto_sync");
        autoSync.setOnPreferenceChangeListener(this);

        Preference setFirstDay = findPreference("first_day");
        setFirstDay.setOnPreferenceChangeListener(this);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String startDate = formatter.format(new Date(DataSupport.findFirst(Base.class).startTime));
        setFirstDay.setSummary(startDate);

        Preference clearAll = findPreference("clear_all");
        clearAll.setOnPreferenceChangeListener(this);

//        Preference language = findPreference(getString(R.string.key_language));
//        language.setSummary(
//                ValueUtils.getLanguage(
//                        getActivity(),
//                        sharedPreferences.getString(getString(R.string.key_language), "follow_system")));
//        language.setOnPreferenceChangeListener(this);
    }

//    private void initForecastPart(SharedPreferences sharedPreferences) {
//        // set today forecast time & todayForecastType.
//        Preference todayForecastTime = findPreference(getString(R.string.key_forecast_today_time));
//        todayForecastTime.setSummary(
//                sharedPreferences.getString(
//                        getString(R.string.key_forecast_today_time),
//                        "fuck"));
//
//        // set tomorrow forecast time & tomorrowForecastType.
//        Preference tomorrowForecastTime = findPreference(getString(R.string.key_forecast_tomorrow_time));
//        tomorrowForecastTime.setSummary(
//                sharedPreferences.getString(
//                        getString(R.string.key_forecast_tomorrow_time),
//                        "you"));
//
//        if (sharedPreferences.getBoolean(getString(R.string.key_forecast_today), false)) {
//            // open today forecast.
//            // set item enable.
//            todayForecastTime.setEnabled(true);
//        } else {
//            // set item enable.
//            todayForecastTime.setEnabled(false);
//        }
//
//        if (sharedPreferences.getBoolean(getString(R.string.key_forecast_tomorrow), false)) {
//            // open tomorrow forecast.
//            tomorrowForecastTime.setEnabled(true);
//        } else {
//            tomorrowForecastTime.setEnabled(false);
//        }
//    }

    private void initNotificationPart(SharedPreferences sharedPreferences) {
        // notification text color.
//        ListPreference notificationTextColor = (ListPreference) findPreference(getString(R.string.key_notification_text_color));
//        notificationTextColor.setSummary(
//                ValueUtils.getNotificationTextColor(
//                        getActivity(),
//                        sharedPreferences.getString(getString(R.string.key_notification_text_color), "grey")));
//        notificationTextColor.setOnPreferenceChangeListener(this);

//        // notification background.
//        CheckBoxPreference notificationBackground = (CheckBoxPreference) findPreference(getString(R.string.key_notification_background));
//
//        // notification can be cleared.
//        CheckBoxPreference notificationClearFlag = (CheckBoxPreference) findPreference(getString(R.string.key_notification_can_be_cleared));
//
//        // notification hide icon.
//        CheckBoxPreference notificationIconBehavior = (CheckBoxPreference) findPreference(getString(R.string.key_notification_hide_icon));
//
//        // notification hide in lock screen.
//        CheckBoxPreference notificationHideBehavior = (CheckBoxPreference) findPreference(getString(R.string.key_notification_hide_in_lockScreen));
//
//        // notification hide big view.
//        CheckBoxPreference notificationHideBigView = (CheckBoxPreference) findPreference(getString(R.string.key_notification_hide_big_view));

        Preference conditions = findPreference("conditions");
        Preference doNotDisturb = findPreference("do_not_disturb");

        if (sharedPreferences.getBoolean("send_notification", false)) {
            // open notification.
//            notificationTextColor.setEnabled(true);
//            notificationBackground.setEnabled(true);
//            notificationClearFlag.setEnabled(true);
//            notificationIconBehavior.setEnabled(true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                notificationHideBehavior.setEnabled(true);
//            } else {
//                notificationHideBehavior.setEnabled(false);
//            }
//            notificationHideBigView.setEnabled(true);
            conditions.setEnabled(true);
            doNotDisturb.setEnabled(true);
        } else {
            // close notification.
//            notificationTextColor.setEnabled(false);
//            notificationBackground.setEnabled(false);
//            notificationClearFlag.setEnabled(false);
//            notificationIconBehavior.setEnabled(false);
//            notificationHideBehavior.setEnabled(false);
//            notificationHideBigView.setEnabled(false);
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
            case "send_notification":
                initNotificationPart(sharedPreferences);
                break;
            case "first_day":
                DateSetterDialog dialog = new DateSetterDialog();
                dialog.setOnDateChangedListener(this);
                dialog.show(getFragmentManager(), null);

                break;
            default:
                break;
        }

//        if (preference.getKey().equals(getString(R.string.key_navigationBar_color))) {
//            // navigation bar color.
//            GeometricWeather.getInstance().setColorNavigationBar();
//            DisplayUtils.setNavigationBarColor(getActivity(), true);
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_fahrenheit))) {
//            // ℉
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_restart));
//        } else if (preference.getKey().equals(getString(R.string.key_permanent_service))) {
//            // permanent service.
//            ServiceHelper.startPermanentService(getActivity());
//            if (sharedPreferences.getBoolean(getString(R.string.key_permanent_service), false)) {
//                ServiceHelper.stopPollingService(getActivity());
//                ServiceHelper.stopForecastService(getActivity(), true);
//                ServiceHelper.stopForecastService(getActivity(), false);
//            } else {
//                ServiceHelper.startupAllService(getActivity());
//            }
//        } else if (preference.getKey().equals(getString(R.string.key_forecast_today))) {
//            // forecast today.
//            initForecastPart(sharedPreferences);
//            if (sharedPreferences.getBoolean(getString(R.string.key_forecast_today), false)) {
//                ServiceHelper.startForecastService(getActivity(), true);
//            } else {
//                ServiceHelper.stopForecastService(getActivity(), true);
//            }
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_forecast_today_time))) {
//            // set today forecast time.
//            TimeSetterDialog dialog = new TimeSetterDialog();
//            dialog.setModel(true);
//            dialog.setOnTimeChangedListener(this);
//            dialog.show(getFragmentManager(), null);
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_forecast_tomorrow))) {
//            // timing forecast tomorrow.
//            initForecastPart(sharedPreferences);
//            if (sharedPreferences.getBoolean(getString(R.string.key_forecast_tomorrow), false)) {
//                ServiceHelper.startForecastService(getActivity(), false);
//            } else {
//                ServiceHelper.stopForecastService(getActivity(), false);
//            }
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_forecast_tomorrow_time))) {
//            // set tomorrow forecast time.
//            TimeSetterDialog dialog = new TimeSetterDialog();
//            dialog.setModel(false);
//            dialog.setOnTimeChangedListener(this);
//            dialog.show(getFragmentManager(), null);
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_notification))) {
//            // notification switch.
//            initNotificationPart(sharedPreferences);
//            if (sharedPreferences.getBoolean(getString(R.string.key_notification), false)) {
//                // open notification.
//                ServiceHelper.startPollingService(getActivity());
//                SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            } else {
//                // close notification.
//                NormalNotificationUtils.cancelNotification(getActivity());
//                ServiceHelper.stopPollingService(getActivity());
//            }
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_notification_background))) {
//            // notification background.
//            ServiceHelper.startPollingService(getActivity());
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_notification_can_be_cleared))) {
//            // notification clear flag.
//            ServiceHelper.startPollingService(getActivity());
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_notification_hide_icon))) {
//            ServiceHelper.startPollingService(getActivity());
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_notification_hide_in_lockScreen))) {
//            ServiceHelper.startPollingService(getActivity());
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            return true;
//        } else if (preference.getKey().equals(getString(R.string.key_notification_hide_big_view))) {
//            ServiceHelper.startPollingService(getActivity());
//            SnackbarUtils.showSnackbar(getString(R.string.feedback_refresh_notification_now));
//            return true;
//        }
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
//        this.initForecastPart(sharedPreferences);


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
