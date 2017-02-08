package com.shouduo.plant.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class Daily extends DataSupport{
    // data
//    public String date;
    public String week;
//    public String[] weathers;
//    public String[] weatherKinds;
//    public int[] temps;
//    public String[] windDirs;
//    public String[] windSpeeds;
//    public String[] windLevels;
//    public String[] astros;
//    public int[] precipitations;
    public int consume;
    public int bright;
    public int tempDiff;

    public Daily() {
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

//    public int[] getTemps() {
//        return temps;
//    }
//
//    public void setTemps(int[] temps) {
//        this.temps = temps;
//    }

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

    public int getTempDiff() {
        return tempDiff;
    }

    public void setTempDiff(int tempDiff) {
        this.tempDiff = tempDiff;
    }

//    public Daily buildDaily(Context c, NewDailyResult.DailyForecasts forecast) {
//        date = forecast.Date.split("T")[0];
//        week = WeatherHelper.getWeek(c, date);
//        weathers = new String[] {
//                forecast.Day.IconPhrase,
//                forecast.Night.IconPhrase};
//        weatherKinds = new String[] {
//                WeatherHelper.getNewWeatherKind(forecast.Day.Icon),
//                WeatherHelper.getNewWeatherKind(forecast.Night.Icon)};
//        temps = new int[] {
//                (int) forecast.Temperature.Maximum.Value,
//                (int) forecast.Temperature.Minimum.Value};
//        windDirs = new String[] {
//                forecast.Day.Wind.Direction.Localized,
//                forecast.Night.Wind.Direction.Localized};
//        windSpeeds = new String[] {forecast.Day.Wind.Speed.Value + "km/h", forecast.Night.Wind.Speed.Value + "km/h"};
//        windLevels = new String[] {
//                WeatherHelper.getWindLevel(c, forecast.Day.Wind.Speed.Value),
//                WeatherHelper.getWindLevel(c, forecast.Night.Wind.Speed.Value)};
//        astros = new String[] {
//                forecast.Sun.Rise.split("T")[1].split(":")[0]
//                        + ":" + forecast.Sun.Rise.split("T")[1].split(":")[1],
//                forecast.Sun.Set.split("T")[1].split(":")[0]
//                        + ":" + forecast.Sun.Set.split("T")[1].split(":")[1]};
//        precipitations = new int[] {forecast.Day.PrecipitationProbability, forecast.Night.PrecipitationProbability};
//        return this;
//    }
//
//    Daily buildDaily(DailyEntity entity) {
//        date = entity.date;
//        week = entity.week;
//        weathers = new String[] {
//                entity.daytimeWeather,
//                entity.nighttimeWeather};
//        weatherKinds = new String[] {
//                entity.daytimeWeatherKind,
//                entity.nighttimeWeatherKind};
//        temps = new int[] {
//                entity.maxiTemp,
//                entity.miniTemp};
//        windDirs = new String[] {entity.daytimeWindDir, entity.nighttimeWindDir};
//        windSpeeds = new String[] {entity.daytimeWindSpeed, entity.nighttimeWindSpeed};
//        windLevels = new String[] {entity.daytimeWindLevel, entity.nighttimeWindLevel};
//        astros = new String[] {
//                entity.sunrise,
//                entity.sunset};
//        precipitations = new int[] {entity.daytimePrecipitations, entity.nighttimePrecipitations};
//        return this;
//    }
}
