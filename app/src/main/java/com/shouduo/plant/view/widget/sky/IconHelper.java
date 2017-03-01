package com.shouduo.plant.view.widget.sky;

import com.shouduo.plant.R;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public class IconHelper {

    public static int[] getPlantIcon() {
        int[] imageId = new int[4];

            imageId[0] = R.drawable.plant_icon_pot;
            imageId[1] = R.drawable.plant_icon_leave_right;
            imageId[2] = R.drawable.plant_icon_leave_left;
            imageId[3] = R.drawable.plant_icon;

        return imageId;
    }

    public static int[] getAnimatorId() {
        int[] animatorId = new int[3];

            animatorId[0] = 0;
            animatorId[1] = R.animator.weather_cloudy_2;
            animatorId[2] = R.animator.weather_cloudy_1;

        return animatorId;
    }
}
