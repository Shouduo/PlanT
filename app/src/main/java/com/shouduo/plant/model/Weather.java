package com.shouduo.plant.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class Weather {
    // data
//    public Base base;
    //    public RealTime realTime;
    public List<Daily> dailyList;
    public List<Hourly> hourlyList;
//    public Aqi aqi;
//    public Index index;
//    public List<Alert> alertList;

    /**
     * <br> life cycle.
     */

    public Weather() {
//        this.base = new Base();
//        this.realTime = new RealTime();
        this.dailyList = new ArrayList<>();
        this.hourlyList = new ArrayList<>();
//        this.aqi = new Aqi();
//        this.index = new Index();
//        this.alertList = new ArrayList<>();

    }

    public Weather mockWeather() {

        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            int distance = random.nextInt(6) - 3;
            Daily daily = new Daily();
            daily.setTemps(new int[]{15 + distance, 10 + distance});
            daily.setWeek("sat");
            dailyList.add(daily);
        }

        for (int i = 0; i < 24; i++) {
            Hourly hourly = new Hourly();
            hourly.setTime("11:00");
            hourly.setTemp(20);
            hourly.setPrecipitation(30);
            hourlyList.add(hourly);
        }

        return this;
    }

//    public static Weather buildWeatherPrimaryData(WeatherEntity entity) {
//        Weather weather = new Weather();
//        weather.base.buildBase(entity);
//        weather.realTime.buildRealTime(entity);
////        weather.aqi.buildAqi(entity);
////        weather.index.buildIndex(entity);
//        return weather;
//    }
//
//    public Weather buildWeatherDailyList(List<DailyEntity> list) {
//        for (int i = 0; i < list.size(); i ++) {
//            dailyList.add(new Daily().buildDaily(list.get(i)));
//        }
//        return this;
//    }
//
//    public Weather buildWeatherHourlyList(List<HourlyEntity> list) {
//        for (int i = 0; i < list.size(); i ++) {
//            hourlyList.add(new Hourly().buildHourly(list.get(i)));
//        }
//        return this;
//    }
//
//    public Weather buildWeatherAlarmList(List<AlarmEntity> list) {
//        for (int i = 0; i < list.size(); i ++) {
//            alertList.add(new Alert().buildAlert(list.get(i)));
//        }
//        return this;
//    }
}
