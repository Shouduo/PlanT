package com.shouduo.plant.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shouduo.plant.utils.HttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public Weather getWeather() {
        HttpUtils.sendOkHttpRequest("http://10.0.2.2/hourly.json", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Weather", "getWeather: fail");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("Weather", responseData);
                parseJSONWithGSON(responseData);
            }
        });

        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            int distance = random.nextInt(6) - 3;
            Daily daily = new Daily();
            daily.setTemps(new int[]{15 + distance, 10 + distance});
            daily.setWeek("sat");
            dailyList.add(daily);
        }

        return this;
    }

    public void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        hourlyList = gson.fromJson(jsonData, new TypeToken<List<Hourly>>(){}.getType());
//        for (Hourly hourly : hourlyList) {
//            Log.d("Weather", hourly.getTime() + "");
//            Log.d("Weather", hourly.getTemp() + "");
//            Log.d("Weather", hourly.getPrecipitation() + "");
//        }
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
