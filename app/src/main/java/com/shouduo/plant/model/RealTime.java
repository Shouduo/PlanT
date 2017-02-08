package com.shouduo.plant.model;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class RealTime {
    // data
    public String weather;
    public String weatherKind;
    public int temp;
    public int sensibleTemp;
    public String windDir;
    public String windSpeed;
    public String windLevel;
    public String simpleForecast;

    /** <br> life cycle. */

    RealTime() {
    }

//    public void buildRealTime(Context c, NewRealtimeResult result) {
//        weather = result.WeatherText;
//        weatherKind = WeatherHelper.getNewWeatherKind(result.WeatherIcon);
//        tempDiff = (int) result.Temperature.Metric.Value;
//        sensibleTemp = (int) result.RealFeelTemperature.Metric.Value;
//        windDir = result.Wind.Direction.Localized;
//        windSpeed = result.Wind.Speed.Metric.Value + "km/h";
//        windLevel = WeatherHelper.getWindLevel(c, result.Wind.Speed.Metric.Value);
//    }
//
//    public void buildRealTime(NewDailyResult result) {
//        simpleForecast = result.Headline.Text;
//    }
//
//    void buildRealTime(WeatherEntity entity) {
//        weather = entity.realTimeWeather;
//        weatherKind = entity.realTimeWeatherKind;
//        tempDiff = entity.realTimeTemp;
//        sensibleTemp = entity.realTimeSensibleTemp;
//        windDir = entity.realTimeWindDir;
//        windSpeed = entity.realTimeWindSpeed;
//        windLevel = entity.realTimeWindLevel;
//        simpleForecast = entity.realTimeSimpleForecast;
//    }
}
