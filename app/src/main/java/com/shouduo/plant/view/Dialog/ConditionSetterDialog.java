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

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.xw.repo.BubbleSeekBar;

/**
 * Created by 刘亨俊 on 17.2.21.
 */

public class ConditionSetterDialog extends BaseDialogFragment implements View.OnClickListener {

    //widget
    private CoordinatorLayout container;
    private OnLimitChangedListener listener;

    private View view;
    private AlertDialog.Builder builder;

    private TextView humValue;
    private TextView brightValue;
    private TextView tempValue;
    private BubbleSeekBar humSeekBar;
    private BubbleSeekBar brightSeekBar;
    private BubbleSeekBar tempSeekBar;

    private Button done;
    private Button cancel;

    //data
    private int humLimit;
    private int brightLimit;
    private int tempLimit;

    private static final String TAG = "ConditionSetterDialog";

    /**
     * <br> life cycle.
     */

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_condition_setter, null, false);

        this.initData();
        this.initWidget(view);
        this.onLimitChanged();

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

    private void initData() {
        humLimit = PlanT.getInstance().getHumLimit();
        brightLimit = PlanT.getInstance().getBrightLimit();
        tempLimit = PlanT.getInstance().getTempLimit();
    }

    /**
     * <br> UI.
     */

    private void initWidget(View view) {
        this.container = (CoordinatorLayout) view.findViewById(R.id.dialog_condition_setter_container);

        humValue = (TextView) view.findViewById(R.id.dialog_condition_setter_humidity_value);
        if (humLimit == PlanT.DEFAULT_HUM_LIMIT) {
            humValue.setText("None");
        } else {
            humValue.setText(humLimit + " %");
        }
        humSeekBar = (BubbleSeekBar) view.findViewById(R.id.dialog_condition_setter_humidity_seekbar);
        humSeekBar.setProgress(humLimit);

        brightValue = (TextView) view.findViewById(R.id.dialog_condition_setter_brightness_value);
        if (brightLimit == PlanT.DEFAULT_BRIGHT_LIMIT) {
            brightValue.setText("None");
        } else {
            brightValue.setText(brightLimit + " lux");
        }
        brightSeekBar = (BubbleSeekBar) view.findViewById(R.id.dialog_condition_setter_brightness_seekbar);
        brightSeekBar.setProgress(brightLimit);

        tempValue = (TextView) view.findViewById(R.id.dialog_condition_setter_temperature_value);
        if (tempLimit == PlanT.DEFAULT_TEMP_LIMIT) {
            tempValue.setText("None");
        } else {
            tempValue.setText(tempLimit + " °C");
        }
        tempSeekBar = (BubbleSeekBar) view.findViewById(R.id.dialog_condition_setter_temperature_seekbar);
        tempSeekBar.setProgress(tempLimit);


        done = (Button) view.findViewById(R.id.dialog_condition_setter_done);
        done.setOnClickListener(this);

        cancel = (Button) view.findViewById(R.id.dialog_condition_setter_cancel);
        cancel.setOnClickListener(this);
    }

    /**
     * <br> interface.
     */

    public interface OnLimitChangedListener {
        void limitChanged();
    }

    public void setOnLimitChangedListener(ConditionSetterDialog.OnLimitChangedListener l) {
        this.listener = l;
    }

    // on time changed listener.
    public void onLimitChanged() {
        humSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(int progress) {
                humLimit = progress;
                if (humLimit == PlanT.DEFAULT_HUM_LIMIT) {
                    humValue.setText("None");
                } else {
                    humValue.setText(humLimit + " %");
                }
            }
        });

        brightSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(int progress) {
                brightLimit = progress;
                if (brightLimit == PlanT.DEFAULT_BRIGHT_LIMIT) {
                    brightValue.setText("None");
                } else {
                    brightValue.setText(brightLimit + " lux");
                }
            }
        });

        tempSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(int progress) {
                tempLimit = progress;
                if (tempLimit == PlanT.DEFAULT_TEMP_LIMIT) {
                    tempValue.setText("None");
                } else {
                    tempValue.setText(tempLimit + " °C");
                }
            }
        });


    }


    //on click
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_condition_setter_cancel:
                dismiss();
                break;

            case R.id.dialog_condition_setter_done:
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PlanT.getInstance()).edit();
                editor.putInt("hum_limit", humLimit);
                editor.putInt("bright_limit", brightLimit);
                editor.putInt("temp_limit", tempLimit);
                editor.apply();

                if (listener != null) {
                    listener.limitChanged();
                }
                dismiss();
                break;
        }
    }
}
