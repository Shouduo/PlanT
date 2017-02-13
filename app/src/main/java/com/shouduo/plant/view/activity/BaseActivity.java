package com.shouduo.plant.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.shouduo.plant.utils.DisplayUtils;
import com.shouduo.plant.view.Dialog.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public abstract class BaseActivity extends AppCompatActivity {
    // widget
    private List<BaseDialogFragment> dialogList;

    // data
    private boolean started = false;

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        GeometricWeather.getInstance().addActivity(this);
        DisplayUtils.setWindowTopColor(this);
        DisplayUtils.setStatusBarTranslate(getWindow());
//        DisplayUtils.setNavigationBarColor(this, TimeUtils.getInstance(this).isDayTime());

        this.dialogList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        GeometricWeather.getInstance().removeActivity();
    }

    public void setStarted() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    public abstract View getSnackbarContainer();

    public List<BaseDialogFragment> getDialogList() {
        return dialogList;
    }

    public View provideSnackbarContainer() {
        if (dialogList.size() > 0) {
            return dialogList.get(dialogList.size() - 1).getSnackbarContainer();
        } else {
            return getSnackbarContainer();
        }
    }

}
