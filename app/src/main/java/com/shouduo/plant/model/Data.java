package com.shouduo.plant.model;

import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shouduo.plant.utils.HttpUtils;
import com.shouduo.plant.view.widget.SafeHandler;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class Data {
    // data
//    public Base base;
    //    public RealTime realTime;
    public List<Daily> dailyList;
    public List<Hourly> hourlyList;

//    private List<Daily> tempDailyList;
//    private List<Hourly> tempHourlyList;

    private SafeHandler handler;

    private int isHourlyDataGot = 0;
    private int isDailyDataGot = 0;

    public final static int SERVER_DOWN = -1;
    public final static int SERVER_GOOD = 1;
//    private Context mainThread;

//    public static boolean isAutoRefresh = false;
//    public final static String hourlyDataUrl = "http://192.168.1.3/hourly.json";
//    public final static String dailyDataUrl = "http://192.168.1.3/daily.json";


    /**
     * <br> life cycle.
     */

    public Data(SafeHandler handler) {
        this.dailyList = new ArrayList<>();
        this.hourlyList = new ArrayList<>();
//        this.mainThread = context;
        this.handler = handler;

//        Connector.getDatabase();
        getDataFromDatabase();

    }

    public Data mockWeather() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int distance = random.nextInt(100) - 50;
            Daily daily = new Daily();
            daily.setWeek("sat");
            daily.setConsume(50 + distance);
            daily.setBright(50 - distance);
            daily.setTempDiff(10 + distance / 10);
            dailyList.add(daily);
        }

        for (int i = 0; i < 12; i++) {
            int distance = random.nextInt(100) - 50;
            Hourly hourly = new Hourly();
            hourly.setTime("11:00");
            hourly.setHum(50 + distance);
            hourly.setConsume(50 - distance);
            hourly.setBright(50 + distance);
            hourly.setTemp(15 + distance / 10);
            hourlyList.add(hourly);
        }
        return this;
    }

    //将 hourlyList 和 dailyList 的数据保存至本地数据库
    public void setDataToDataBase() {
        Message message = new Message();

        if (isHourlyDataGot + isDailyDataGot == 1 || isHourlyDataGot + isDailyDataGot == -1) {  //等待另一份数据的结果
            return;
        }

        if (isHourlyDataGot + isDailyDataGot == 2) {    //更新成功
//            hourlyList = tempHourlyList;
            DataSupport.deleteAll(Hourly.class);
            for (Hourly hourly : hourlyList) {
                hourly.save();
            }
//            dailyList = tempDailyList;
            DataSupport.deleteAll(Daily.class);
            for (Daily daily : dailyList) {
                daily.save();
            }
            message.what = SERVER_GOOD;
        } else {    //更新失败
            getDataFromDatabase();  //恢复hourlyList 或 dailyList
            message.what = SERVER_DOWN;

        }
        handler.sendMessage(message);
        isHourlyDataGot = isDailyDataGot = 0;
//        tempHourlyList = null;
//        tempDailyList = null;
    }

    //从服务器更新数据
    public void refreshData() {
        HttpUtils.sendOkHttpRequest("http://192.168.191.1/hourly.json", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isHourlyDataGot = -1;
                setDataToDataBase();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                hourlyList = new Gson().fromJson(responseData, new TypeToken<List<Hourly>>() {
                }.getType());
                isHourlyDataGot = 1;
                setDataToDataBase();
            }
        });

        HttpUtils.sendOkHttpRequest("http://192.168.191.1/daily.json", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isDailyDataGot = -1;
                setDataToDataBase();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                dailyList = new Gson().fromJson(responseData, new TypeToken<List<Daily>>() {
                }.getType());
                isDailyDataGot = 1;
                setDataToDataBase();
            }
        });
    }

    //从本地数据库读取数据到 hourlyList 和 dailyList
    public boolean getDataFromDatabase() {
        hourlyList = DataSupport.findAll(Hourly.class);
        dailyList = DataSupport.findAll(Daily.class);

        if (hourlyList.size() == 0 || dailyList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
