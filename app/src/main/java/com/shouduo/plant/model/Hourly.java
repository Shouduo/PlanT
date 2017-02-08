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
    public int bright;
    public int temp;
    public int consume;

    public Hourly() {
    }

    public Hourly(String time, int temp, int consume) {
        this.time = time;
        this.temp = temp;
        this.consume = consume;
    }

    /** <br> life cycle. */

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public String getTime() {
        return time;
    }

    public int getTemp() {
        return temp;
    }

    public int getConsume() {
        return consume;
    }

    //    public Hourly buildHourly(Context c, NewHourlyResult result) {
//        time = result.DateTime.split("T")[1].split(":")[0] + c.getString(R.string.of_clock);
//        dayTime = result.IsDaylight;
//        weather = result.IconPhrase;
//        weatherKind = WeatherHelper.getNewWeatherKind(result.WeatherIcon);
//        temp = (int) result.Temperature.Value;
//        consume = result.PrecipitationProbability;
//        return this;
//    }
//
//    Hourly buildHourly(HourlyEntity entity) {
//        time = entity.time;
//        dayTime = entity.dayTime;
//        weather = entity.weather;
//        weatherKind = entity.weatherKind;
//        temp = entity.temp;
//        consume = entity.consume;
//        return this;
//    }

}
