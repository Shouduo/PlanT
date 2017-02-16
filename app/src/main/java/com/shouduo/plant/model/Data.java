package com.shouduo.plant.model;

import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shouduo.plant.utils.HttpUtils;
import com.shouduo.plant.view.widget.SafeHandler;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class Data {
    // data
    public Base base;
    //    public RealTime realTime;
//    public int days;
//    public String refreshTime;
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
        this.base = new Base();
        this.dailyList = new ArrayList<>();
        this.hourlyList = new ArrayList<>();
        this.handler = handler;
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
            setRefreshTime();
            message.what = SERVER_GOOD;
        } else {    //更新失败
            getDataFromDatabase();  //恢复hourlyList 或 dailyList
            message.what = SERVER_DOWN;

        }
        handler.sendMessage(message);
        isHourlyDataGot = isDailyDataGot = 0;
    }

    //设置更新数据时间
    private void setRefreshTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd  HH:mm");
        Date currentDate = new Date(System.currentTimeMillis());
        long startTime = DataSupport.findFirst(Base.class).startTime;
        DataSupport.deleteAll(Base.class);
        Base base = new Base();
        base.startTime = startTime;
        base.refreshTime = formatter.format(currentDate);
        base.save();
    }

    public int calcNumberOfDays() {
        Base base = DataSupport.findFirst(Base.class);

        Date startDate = new Date(base.startTime);
        Date endDate = new Date(System.currentTimeMillis());

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        long ONE_DAY_MS = 24 * 60 * 60 * 1000;
        int days = (int) (((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())) / ONE_DAY_MS);
        return days;
    }

    private void setNumberOfDays() {
        Date currentDate = new Date(System.currentTimeMillis());
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
        base = DataSupport.findFirst(Base.class);
        if (base == null) {
            Base base = new Base();
            base.startTime = System.currentTimeMillis();
            base.save();
        }

        if (hourlyList.size() == 0 || dailyList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

//    public String getStartDate() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
//        Date date = new Date(DataSupport.findFirst(Base.class).startTime);
//        String startDate = formatter.format(date);
//        return startDate;
//    }
}
