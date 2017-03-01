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

}
