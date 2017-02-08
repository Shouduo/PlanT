package com.shouduo.plant.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shouduo.plant.utils.HttpUtils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

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

//    public Weather mockWeather() {
//
//        Random random = new Random();
//        for (int i = 0; i < 15; i++) {
//            int distance = random.nextInt(6) - 3;
//            Daily daily = new Daily();
//            daily.setTemps(new int[]{15 + distance, 10 + distance});
//            daily.setWeek("sat");
//            dailyList.add(daily);
//        }
//
//        for (int i = 0; i < 24; i++) {
//            Hourly hourly = new Hourly();
//            hourly.setTime("11:00");
//            hourly.setTempDiff(20);
//            hourly.setConsume(30);
//            hourlyList.add(hourly);
//        }
//
//        return this;
//    }

    public void getWeather() {
        HttpUtils.sendOkHttpRequest("http://192.168.1.3/hourly.json", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(, "Server's down", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                hourlyList = new Gson().fromJson(responseData, new TypeToken<List<Hourly>>() {
                }.getType());
                for (Hourly hourly : hourlyList) {
                    hourly.save();
                }
            }
        });

        HttpUtils.sendOkHttpRequest("http://192.168.1.3/daily.json", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(, "Server's down", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                dailyList = new Gson().fromJson(responseData, new TypeToken<List<Daily>>() {
                }.getType());
                for (Daily daily : dailyList) {
                    daily.save();
                }
            }
        });


//        return this;
    }

//    public List parseJSONWithGSON(String jsonData) {
//        Gson gson = new Gson();
//        ArrayList list = gson.fromJson(jsonData, new TypeToken<List<Hourly>>() {
//        }.getType());
//        return list;
//    }

    public Weather getWeatherFromDatabase() {
        hourlyList = DataSupport.findAll(Hourly.class);
        dailyList = DataSupport.findAll(Daily.class);

//        Random random = new Random();
//        for (int i = 0; i < 12; i++) {
//            int distance = random.nextInt(6) - 3;
//            Daily daily = new Daily();
//            daily.setTemps(new int[]{15 + distance, 10 + distance});
//            daily.setWeek("sat");
//            dailyList.add(daily);
//        }
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
