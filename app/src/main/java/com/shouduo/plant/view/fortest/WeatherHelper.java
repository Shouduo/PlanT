package com.shouduo.plant.view.fortest;

import com.shouduo.plant.R;

/**
 * Data kind tools.
 * */

public class WeatherHelper {
    // widget
//    private NewWeather newWeather;

    // data
    private static final String KIND_CLEAR = "CLEAR";
    private static final String KIND_PARTLY_CLOUDY = "PARTLY_CLOUDY";
    private static final String KIND_CLOUDY = "CLOUDY";
    private static final String KIND_RAIN = "RAIN";
    private static final String KIND_SNOW = "SNOW";
    private static final String KIND_WIND = "WIND";
    private static final String KIND_FOG = "FOG";
    private static final String KIND_HAZE = "HAZE";
    private static final String KIND_SLEET = "SLEET";
    private static final String KIND_HAIL = "HAIL";
    private static final String KIND_THUNDER = "THUNDER";
    private static final String KIND_THUNDERSTORM = "THUNDERSTORM";

    /** <br> life cycle. */

/*    public WeatherHelper() {
        newWeather = null;
    }*/

    /** <br> data. */

/*    public void requestWeather(Context c, Location location, OnRequestWeatherListener l) {
        newWeather = NewWeather.getService().requestNewWeather(c, location, l);
    }

    public void cancel() {
        if (newWeather != null) {
            newWeather.cancel();
        }
    }*/

 /*   public static String getNewWeatherKind(int icon) {
        if (icon == 1 || icon == 2 || icon == 3
                || icon == 30 || icon == 33 || icon == 34 || icon == 35) {
            return KIND_CLEAR;
        } else if (icon == 4 || icon == 6 || icon == 36 || icon == 38) {
            return KIND_PARTLY_CLOUDY;
        } else if (icon == 5 || icon == 7 || icon == 8
                || icon == 37) {
            return KIND_CLOUDY;
        } else if (icon == 11) {
            return KIND_FOG;
        } else if (icon == 12 || icon == 13 || icon == 14 || icon == 18
                || icon == 39 || icon == 40) {
            return KIND_RAIN;
        } else if (icon == 15 || icon == 16 || icon == 17 || icon == 41 || icon == 42) {
            return KIND_THUNDERSTORM;
        } else if (icon == 19 || icon == 20 || icon == 21 || icon == 22 || icon == 23 || icon == 24
                || icon == 31 || icon == 43 || icon == 44) {
            return KIND_SNOW;
        } else if (icon == 25) {
            return KIND_HAIL;
        } else if (icon == 26 || icon == 29) {
            return KIND_SLEET;
        } else if (icon == 32) {
            return KIND_WIND;
        } else {
            return KIND_CLOUDY;
        }
    }*/

    public static int[] getWeatherIcon() {
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

    public static int getMiniWeatherIcon(String weatherInfo, boolean dayTime) {
        int imageId;
        switch (weatherInfo) {
            default:
                imageId = R.drawable.weather_cloud_mini;
                break;
        }
        return imageId;
    }

/*    @SuppressLint("SimpleDateFormat")
    public static String getWeek(Context c, String dateTxt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(dateTxt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1){
            return c.getString(R.string.week_7);
        } else if (day == 2) {
            return c.getString(R.string.week_1);
        } else if (day == 3) {
            return c.getString(R.string.week_2);
        } else if (day == 4) {
            return c.getString(R.string.week_3);
        } else if (day == 5) {
            return c.getString(R.string.week_4);
        } else if (day == 6) {
            return c.getString(R.string.week_5);
        } else {
            return c.getString(R.string.week_6);
        }
    }*/

/*    public static String getWindLevel(Context c, double speed) {
        if (speed <= 2) {
            return c.getString(R.string.wind_0);
        } else if (speed <= 6) {
            return c.getString(R.string.wind_1);
        } else if (speed <= 12) {
            return c.getString(R.string.wind_2);
        } else if (speed <= 19) {
            return c.getString(R.string.wind_3);
        } else if (speed <= 30) {
            return c.getString(R.string.wind_4);
        } else if (speed <= 40) {
            return c.getString(R.string.wind_5);
        } else if (speed <= 51) {
            return c.getString(R.string.wind_6);
        } else if (speed <= 62) {
            return c.getString(R.string.wind_7);
        } else if (speed <= 75) {
            return c.getString(R.string.wind_8);
        } else if (speed <= 87) {
            return c.getString(R.string.wind_9);
        } else if (speed <= 103) {
            return c.getString(R.string.wind_10);
        } else if (speed <= 117) {
            return c.getString(R.string.wind_11);
        } else {
            return c.getString(R.string.wind_12);
        }
    }*/

/*    public static int getWindColorResId(String speed) {
        double s = Double.parseDouble(speed.split("km/h")[0]);
        if (s <= 30) {
            return 0;
        } else if (s <= 51) {
            return 0;
        } else if (s <= 75) {
            return 0;
        } else if (s <= 103) {
            return R.color.colorLevel_4;
        } else if (s <= 117) {
            return R.color.colorLevel_5;
        } else {
            return R.color.colorLevel_6;
        }
    }*/
/*
    public static int getConsume(int consume) {
        if (consume < 3) {
            return 10;
        } else if (consume < 6) {
            return 30;
        } else if (consume < 9) {
            return 60;
        } else {
            return 90;
        }
    }
*/
/*    public static String getAqiQuality(Context c, int index) {
        if (index <= 50) {
            return c.getString(R.string.aqi_1);
        } else if (index <= 100) {
            return c.getString(R.string.aqi_2);
        } else if (index <= 150) {
            return c.getString(R.string.aqi_3);
        } else if (index <= 200) {
            return c.getString(R.string.aqi_4);
        } else if (index <= 300) {
            return c.getString(R.string.aqi_5);
        } else {
            return c.getString(R.string.aqi_6);
        }
    }

    public static int getAqiColorResId(int index) {
        if (index <= 50) {
            return 0;
        } else if (index <= 100) {
            return 0;
        } else if (index <= 150) {
            return 0;
        } else if (index <= 200) {
            return R.color.colorLevel_4;
        } else if (index <= 300) {
            return R.color.colorLevel_5;
        } else {
            return R.color.colorLevel_6;
        }
    }*/

    /** <br> listener. */

/*    public interface OnRequestWeatherListener {
        void requestWeatherSuccess(Data weather, Location requestLocation);
        void requestWeatherFailed(Location requestLocation);
    }*/
}
