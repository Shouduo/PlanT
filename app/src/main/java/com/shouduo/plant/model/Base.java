package com.shouduo.plant.model;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class Base {
    // data
    public String cityId;
    public String city;
    public String date;
    public String time;

    /** <br> life cycle. */

    Base() {
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
