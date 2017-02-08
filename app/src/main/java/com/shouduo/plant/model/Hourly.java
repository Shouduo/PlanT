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
    public int consume;
    public int bright;
    public int temp;

    public Hourly() {
    }


    /** <br> life cycle. */
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHum() {
        return hum;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getBright() {
        return bright;
    }

    public void setBright(int bright) {
        this.bright = bright;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    //    public Hourly buildHourly(Context c, NewHourlyResult result) {
//        time = result.DateTime.split("T")[1].split(":")[0] + c.getString(R.string.of_clock);
//        dayTime = result.IsDaylight;
//        weather = result.IconPhrase;
//        weatherKind = WeatherHelper.getNewWeatherKind(result.WeatherIcon);
//        tempDiff = (int) result.Temperature.Value;
//        consume = result.PrecipitationProbability;
//        return this;
//    }
//
//    Hourly buildHourly(HourlyEntity entity) {
//        time = entity.time;
//        dayTime = entity.dayTime;
//        weather = entity.weather;
//        weatherKind = entity.weatherKind;
//        tempDiff = entity.tempDiff;
//        consume = entity.consume;
//        return this;
//    }

}
