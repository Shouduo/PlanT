package com.shouduo.plant.model;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class History {
    // data
//    public String cityId;
//    public String city;
//    public String date;

    public int maxiTemp;
    public int miniTemp;

    public History mockHistory() {
        maxiTemp = 15;
        miniTemp = 10;
        return this;
    }
    /** <br> life cycle. */

//    public static History buildHistory(Weather weather) {
//        History history = new History();
//        history.cityId = weather.base.cityId;
//        history.city = weather.base.city;
//        history.date = weather.base.date;
//        history.maxiTemp = weather.dailyList.get(0).temps[0];
//        history.miniTemp = weather.dailyList.get(0).temps[1];
//
//        return history;
//    }
//
//    public static History buildHistory(HistoryEntity entity) {
//        History history = new History();
//        history.cityId = entity.cityId;
//        history.city = entity.city;
//        history.date = entity.date;
//        history.maxiTemp = entity.maxiTemp;
//        history.miniTemp = entity.miniTemp;
//
//        return history;
//    }
}
