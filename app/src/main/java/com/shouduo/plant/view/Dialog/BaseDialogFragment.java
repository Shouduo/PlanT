package com.shouduo.plant.view.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

/**
 * Created by 刘亨俊 on 17.2.13.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        GeometricWeather.getInstance().getTopActivity().getDialogList().add(this);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        GeometricWeather.getInstance().getTopActivity().getDialogList().remove(this);
    }

    public abstract View getSnackbarContainer();
}
