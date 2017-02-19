package com.shouduo.plant.view.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.shouduo.plant.R;

import java.util.Calendar;

/**
 * Created by 刘亨俊 on 17.2.13.
 */

public class TimeSetterDialog extends BaseDialogFragment
        implements View.OnClickListener, TimePicker.OnTimeChangedListener {
    // widget
    private CoordinatorLayout container;
    private OnTimeChangedListener listener;

    private View view;
    private AlertDialog.Builder builder;
    private TextView title;
    private TimePicker fromTimePicker;
    private TimePicker toTimePicker;
    private Button done;
    private Button cancel;

    private static final String TAG = "TimeSetterDialog";

    // data
    private int hour;
    private int minute;
    private int fromTimeHour;
    private int fromTimeMinute;
    private int toTimeHour;
    private int toTimeMinute;
    private boolean isFrom = true;

    /**
     * <br> life cycle.
     */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time_setter, null, false);
        this.initData();
        this.initWidget(view);

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /**
     * <br> data.
     */

    public void setModel(boolean isFrom) {
        this.isFrom = isFrom;
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    /**
     * <br> UI.
     */

    private void initWidget(View view) {
        this.container = (CoordinatorLayout) view.findViewById(R.id.dialog_time_setter_container);

        title = (TextView) view.findViewById(R.id.dialog_time_setter_title);


        fromTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_setter_from_time_picker);
//        fromTimePicker.setIs24HourView(true);
        fromTimePicker.setOnTimeChangedListener(this);

        toTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_setter_to_time_picker);
//        toTimePicker.setIs24HourView(true);
        toTimePicker.setOnTimeChangedListener(this);
        toTimePicker.setEnabled(false);
        toTimePicker.setVisibility(View.GONE);

        done = (Button) view.findViewById(R.id.dialog_time_setter_done);
        done.setOnClickListener(this);
        done.setText("Next");

        cancel = (Button) view.findViewById(R.id.dialog_time_setter_cancel);
        cancel.setOnClickListener(this);
    }

    /**
     * <br> interface.
     */

    public interface OnTimeChangedListener {
        void timeChanged();
    }

    public void setOnTimeChangedListener(OnTimeChangedListener l) {
        this.listener = l;
    }

    // on time changed listener.

    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    // on click.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_time_setter_cancel:
                dismiss();
                break;

            case R.id.dialog_time_setter_done:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                if (isFrom) {
                    fromTimeHour = hour;
                    fromTimeMinute = minute;

                    //change timepicker in dialog.
                    fromTimePicker.setEnabled(false);
                    fromTimePicker.setVisibility(View.GONE);
                    toTimePicker.setEnabled(true);
                    toTimePicker.setVisibility(View.VISIBLE);

                    title.setText("To:");

                    isFrom = false;
                } else {
                    toTimeHour = hour;
                    toTimeMinute = minute;

                    editor.putInt("from_time_hour", fromTimeHour);
                    editor.putInt("from_time_minute", fromTimeMinute);
                    editor.putInt("to_time_hour", toTimeHour);
                    editor.putInt("to_time_minute", toTimeMinute);
                    editor.apply();

                    if (listener != null) {
                        listener.timeChanged();
                    }
                    dismiss();
                }
                break;
        }
    }
}
