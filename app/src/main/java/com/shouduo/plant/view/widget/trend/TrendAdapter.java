package com.shouduo.plant.view.widget.trend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shouduo.plant.R;
import com.shouduo.plant.model.Data;
import com.shouduo.plant.utils.TimeUtils;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class TrendAdapter extends RecyclerView.Adapter<TrendAdapter.ViewHolder> {
    // widget
    private Context context;
    private OnTrendItemClickListener listener;

    // data
    private Data mData;
    private boolean dayTime;
    private int dataType;
    private int viewType;
    private int highest, lowest;

    /** <br> life cycle. */
    public TrendAdapter(Context context, Data data, OnTrendItemClickListener l) {
        this.context = context;
        this.listener = l;
        this.setData(data, TrendItemView.DATA_TYPE_DAILY, TrendItemView.VIEW_TYPE_HUM);
    }

    /** <br> UI. */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData == null) {
            holder.trendItemView.setNullData();
        } else {
            holder.trendItemView.setData(mData, dataType, viewType, position, highest, lowest);
            switch (dataType) {
                case TrendItemView.DATA_TYPE_DAILY:
                    if (position == 0) {
                        holder.textView.setText("Today");
                    } else {
                        holder.textView.setText(mData.dailyList.get(position).week);
                    }
                    break;

                case TrendItemView.DATA_TYPE_HOURLY:
                    holder.textView.setText(mData.hourlyList.get(position).time);
                    break;
            }
        }
    }

    /** <br> data. */
    public void setData(Data data, int dataType, int viewType) {
        this.mData = data;
        this.dayTime = TimeUtils.getInstance(context).isDayTime();
        this.dataType = dataType;
        this.viewType = viewType;

        calcDataRange();
    }

    //计算每组数据最值
    private void calcDataRange() {
        if (mData == null) {
            highest = lowest = 0;
        } else {
            switch (viewType) {
                case TrendItemView.VIEW_TYPE_HUM:
                    switch (dataType) {
                        case TrendItemView.DATA_TYPE_HOURLY:
                            highest = mData.hourlyList.get(0).hum;
                            lowest = mData.hourlyList.get(0).hum;

                            for (int i = 0; i < mData.hourlyList.size(); i ++) {
                                if (mData.hourlyList.get(i).hum > highest) {
                                    highest = mData.hourlyList.get(i).hum;
                                }
                                if (mData.hourlyList.get(i).hum < lowest) {
                                    lowest = mData.hourlyList.get(i).hum;
                                }
                            }
                            break;
                        case TrendItemView.DATA_TYPE_DAILY:
                            highest = mData.dailyList.get(0).consume;
                            lowest = mData.dailyList.get(0).consume;

                            for (int i = 0; i < mData.dailyList.size(); i ++) {
                                if (mData.dailyList.get(i).consume > highest) {
                                    highest = mData.dailyList.get(i).consume;
                                }
                                if (mData.dailyList.get(i).consume < lowest) {
                                    lowest = mData.dailyList.get(i).consume;
                                }
                            }
                            break;
                    }
                    break;
                case TrendItemView.VIEW_TYPE_BRIGHT:
                    switch (dataType) {
                        case TrendItemView.DATA_TYPE_HOURLY:
                            highest = mData.hourlyList.get(0).bright;
                            lowest = mData.hourlyList.get(0).bright;

                            for (int i = 0; i < mData.hourlyList.size(); i ++) {
                                if (mData.hourlyList.get(i).bright > highest) {
                                    highest = mData.hourlyList.get(i).bright;
                                }
                                if (mData.hourlyList.get(i).bright < lowest) {
                                    lowest = mData.hourlyList.get(i).bright;
                                }
                            }
                            break;
                        case TrendItemView.DATA_TYPE_DAILY:
                            highest = mData.dailyList.get(0).bright;
                            lowest = mData.dailyList.get(0).bright;

                            for (int i = 0; i < mData.dailyList.size(); i ++) {
                                if (mData.dailyList.get(i).bright > highest) {
                                    highest = mData.dailyList.get(i).bright;
                                }
                                if (mData.dailyList.get(i).bright < lowest) {
                                    lowest = mData.dailyList.get(i).bright;
                                }
                            }
                            break;
                    }
                    break;
                case TrendItemView.VIEW_TYPE_TEMP:
                    switch (dataType) {
                        case TrendItemView.DATA_TYPE_HOURLY:
                            highest = mData.hourlyList.get(0).temp;
                            lowest = mData.hourlyList.get(0).temp;

                            for (int i = 0; i < mData.hourlyList.size(); i ++) {
                                if (mData.hourlyList.get(i).temp > highest) {
                                    highest = mData.hourlyList.get(i).temp;
                                }
                                if (mData.hourlyList.get(i).temp < lowest) {
                                    lowest = mData.hourlyList.get(i).temp;
                                }
                            }
                            break;
                        case TrendItemView.DATA_TYPE_DAILY:
                            highest = mData.dailyList.get(0).tempDiff;
                            lowest = mData.dailyList.get(0).tempDiff;

                            for (int i = 0; i < mData.dailyList.size(); i ++) {
                                if (mData.dailyList.get(i).tempDiff > highest) {
                                    highest = mData.dailyList.get(i).tempDiff;
                                }
                                if (mData.dailyList.get(i).tempDiff < lowest) {
                                    lowest = mData.dailyList.get(i).tempDiff;
                                }
                            }
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (dataType) {
            case TrendItemView.DATA_TYPE_DAILY:
                return mData == null ? 7 : mData.dailyList.size();

            case TrendItemView.DATA_TYPE_HOURLY:
                if (mData == null) {
                    return 7;
                } else if (mData.hourlyList.size() > 1) {
                    return mData.hourlyList.size();
                } else {
                    return 0;
                }

            default:
                return 7;
        }
    }

    /** <br> interface. */
    public interface OnTrendItemClickListener {
        void onTrendItemClick();
    }

    /** <br> inner class. */
    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // widget
        TrendItemView trendItemView;
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);

            this.trendItemView = (TrendItemView) itemView.findViewById(R.id.item_trend);
            trendItemView.setOnClickListener(this);

            this.textView = (TextView) itemView.findViewById(R.id.item_trend_txt);

            itemView.findViewById(R.id.item_trend_iconBar).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_trend:
                    listener.onTrendItemClick();
                    break;
                default:
                    break;
            }
        }
    }
}
