package com.shouduo.plant.view.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.model.Base;
import com.shouduo.plant.model.Daily;
import com.shouduo.plant.model.Hourly;

import org.litepal.crud.DataSupport;

/**
 * Created by 刘亨俊 on 17.2.16.
 */

public class ClearAllDialog extends BaseDialogFragment
        implements DialogInterface.OnClickListener {

    private static final String TAG = "ClearAllDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Clear All Data?");
        builder.setMessage("The operation can not be undo.");
        builder.setPositiveButton("Confirm", this);
        builder.setNegativeButton("Cancel", this);
//        return builder.create();

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorTextDark2nd));

        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View getSnackbarContainer() {
        return null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case -1:    //confrim
                DataSupport.deleteAll(Hourly.class);
                DataSupport.deleteAll(Daily.class);
                DataSupport.deleteAll(Base.class);
//                Toast.makeText(getActivity(), "Data Cleared!", Toast.LENGTH_SHORT).show();
                PlanT.getInstance().recreateMainActivity();
                break;
            case -2:    //cancel
                break;
            default:
                break;
        }
//        Log.d(TAG, "onClick: " + which);
    }
}
