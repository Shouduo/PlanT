package com.shouduo.plant.model;

import org.litepal.crud.DataSupport;

/**
 * Created by 刘亨俊 on 17.2.2.
 */

public class Daily extends DataSupport{
    // data
    public String date;
    public int consume;
    public int bright;
    public int tempDiff;

    public Daily() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getBright() {
        return bright;
    }

    public void setBright(int bright) {
        this.bright = bright;
    }

    public int getTempDiff() {
        return tempDiff;
    }

    public void setTempDiff(int tempDiff) {
        this.tempDiff = tempDiff;
    }

}
