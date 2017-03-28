package com.shouduo.plant.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;

import com.shouduo.plant.R;
import com.shouduo.plant.model.Base;

import org.litepal.crud.DataSupport;

import java.util.Calendar;

/**
 * Created by 刘亨俊 on 17.2.15.
 */

public class DateSetterDialog extends BaseDialogFragment
        implements View.OnClickListener, DatePicker.OnDateChangedListener {
    // widget
    private CoordinatorLayout container;
    private OnDateChangedListener listener;

    // data
    private int year;
    private int month;
    private int day;

    /** <br> life cycle. */
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date_setter, null, false);
        this.initData();
        this.initWidget(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        //trim the blank bar at the sides of datePicker.
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialog) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                final Window window = getDialog().getWindow();
                lp.copyFrom(window.getAttributes());
                final View picker = window.findViewById(R.id.dialog_date_setter_date_picker);
                lp.width = picker.getWidth() + 80;
                window.setAttributes(lp);
            }
        });
        return alertDialog;
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> data. */
    private void initData() {
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DATE);
    }

    /** <br> UI. */
    private void initWidget(View view) {
        this.container = (CoordinatorLayout) view.findViewById(R.id.dialog_date_setter_container);

        Button done = (Button) view.findViewById(R.id.dialog_date_setter_done);
        done.setOnClickListener(this);

        Button cancel = (Button) view.findViewById(R.id.dialog_date_setter_cancel);
        cancel.setOnClickListener(this);

        DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_date_setter_date_picker);
        datePicker.setMaxDate(System.currentTimeMillis());
        datePicker.init(year, month, day, this);
    }

    /** <br> interface. */
    public interface OnDateChangedListener {
        void dateChanged();
    }

    public void setOnDateChangedListener(OnDateChangedListener l) {
        this.listener = l;
    }

    // on time changed listener.
    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // on click.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_date_setter_cancel:
                dismiss();
                break;

            case R.id.dialog_date_setter_done:

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, day);

                String refreshTime = DataSupport.findFirst(Base.class).refreshTime;
                DataSupport.deleteAll(Base.class);
                Base base = new Base();
                base.refreshTime = refreshTime;
                base.startTime = calendar.getTimeInMillis();
                base.save();

                if (listener != null) {
                    listener.dateChanged();
                }

                dismiss();
                break;
        }
    }
}
