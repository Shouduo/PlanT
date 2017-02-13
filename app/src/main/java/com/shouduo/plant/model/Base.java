package com.shouduo.plant.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class Base extends DataSupport{
    // data
    public long startTime;
    public String refreshTime;

    /** <br> life cycle. */

    public Base() {
    }

//    public void buildBase(Location location, NewRealtimeResult result) {
//        cityId = location.cityId;
//        city = location.city;
//        date = result.LocalObservationDateTime.split("T")[0];
//        time = result.LocalObservationDateTime.split("T")[1].split(":")[0]
//                + ":" + result.LocalObservationDateTime.split("T")[1].split(":")[1];
//    }
//
//    void buildBase(WeatherEntity entity) {
//        cityId = entity.cityId;
//        city = entity.city;
//        date = entity.date;
//        time = entity.time;
//    }
}
