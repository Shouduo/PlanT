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

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class Data {
    // data
    public Base base;
    public List<Daily> dailyList;
    public List<Hourly> hourlyList;

    private SafeHandler handler;

    private int isHourlyDataGot = 0;
    private int isDailyDataGot = 0;

    public final static int SERVER_DOWN = -1;
    public final static int SERVER_GOOD = 1;
    public final static String SERVER_ADDRESS = "http://192.168.191.1:8080/PlanT/";

    public Data(SafeHandler handler) {
        this.base = new Base();
        this.dailyList = new ArrayList<>();
        this.hourlyList = new ArrayList<>();
        this.handler = handler;
        getDataFromDatabase();

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

    //计算天数
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

    //从服务器更新数据
    public void refreshData() {
        HttpUtils.sendOkHttpRequest(SERVER_ADDRESS + "Hourly", new okhttp3.Callback() {
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

        HttpUtils.sendOkHttpRequest(SERVER_ADDRESS + "Daily", new okhttp3.Callback() {
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
}
