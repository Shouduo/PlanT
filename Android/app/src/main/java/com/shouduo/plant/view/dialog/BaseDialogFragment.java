package com.shouduo.plant.view.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.shouduo.plant.PlanT;

/**
 * Created by 刘亨俊 on 17.2.13.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        PlanT.getInstance().getTopActivity().getDialogList().add(this);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PlanT.getInstance().getTopActivity().getDialogList().remove(this);
    }

    public abstract View getSnackbarContainer();
}
