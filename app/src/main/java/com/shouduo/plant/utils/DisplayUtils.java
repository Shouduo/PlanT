package com.shouduo.plant.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shouduo.plant.R;
import com.shouduo.plant.view.activity.MainActivity;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public class DisplayUtils {


    public static float dpToPx(Context context, int dp) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        return (float) (dp * (dpi / 160.0));
    }

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen","android");
        return r.getDimensionPixelSize(resourceId);
    }

    public static void setStatusBarTranslate(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowTopColor(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean isDay = TimeUtils.getInstance(a).isDayTime();
            int color = a instanceof MainActivity
                    ?
                    ContextCompat.getColor(
                            a,
                            isDay ? R.color.lightPrimary_5 : R.color.darkPrimary_5)
                    :
                    ContextCompat.getColor(a, R.color.colorPrimary);

            ActivityManager.TaskDescription taskDescription;
            Bitmap topIcon = BitmapFactory.decodeResource(a.getResources(), R.mipmap.ic_launcher);
            taskDescription = new ActivityManager.TaskDescription(
                    a.getString(R.string.plant),
                    topIcon,
                    color);
            a.setTaskDescription(taskDescription);
            topIcon.recycle();
        }
    }
}
