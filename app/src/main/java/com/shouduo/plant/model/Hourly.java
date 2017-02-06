package com.shouduo.plant.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class Hourly extends DataSupport{
    // data
    public String time;
//    public boolean dayTime;
//    public String weather;
//    public String weatherKind;
    public int hum;
    public int temp;
    public int bright;
    public int precipitation;

    public Hourly() {
    }

    public Hourly(String time, int temp, int precipitation) {
        this.time = time;
        this.temp = temp;
        this.precipitation = precipitation;
    }

    /** <br> life cycle. */

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public String getTime() {
        return time;
    }

    public int getTemp() {
        return temp;
    }

    public int getPrecipitation() {
        return precipitation;
    }

    //    public Hourly buildHourly(Context c, NewHourlyResult result) {
//        time = result.DateTime.split("T")[1].split(":")[0] + c.getString(R.string.of_clock);
//        dayTime = result.IsDaylight;
//        weather = result.IconPhrase;
//        weatherKind = WeatherHelper.getNewWeatherKind(result.WeatherIcon);
//        temp = (int) result.Temperature.Value;
//        precipitation = result.PrecipitationProbability;
//        return this;
//    }
//
//    Hourly buildHourly(HourlyEntity entity) {
//        time = entity.time;
//        dayTime = entity.dayTime;
//        weather = entity.weather;
//        weatherKind = entity.weatherKind;
//        temp = entity.temp;
//        precipitation = entity.precipitation;
//        return this;
//    }

}
