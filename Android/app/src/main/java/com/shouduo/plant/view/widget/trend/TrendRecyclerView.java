package com.shouduo.plant.view.widget.trend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.model.Data;
import com.shouduo.plant.utils.DisplayUtils;
import com.shouduo.plant.view.widget.SwipeSwitchLayout;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class TrendRecyclerView extends RecyclerView {
    // widget
    private SwipeSwitchLayout switchLayout;
    private Paint paint;

    // data
    private int highest, lowest;
    private int[] tempYs;
    private int limitLineY;
    private boolean canScroll = false;

    private float MARGIN_BOTTOM;
    private float CHART_LINE_SIZE = 1;
    private float TEXT_SIZE = 10;
    private float MARGIN_TEXT = 2;

    /** <br> life cycle. */
    public TrendRecyclerView(Context context) {
        super(context);
        this.initialize();
    }

    public TrendRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public TrendRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize();
    }

    private void initialize() {
        setWillNotDraw(false);

        this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextSize(TEXT_SIZE);

        this.MARGIN_BOTTOM = TrendItemView.calcDrawSpecMarginBottom(getContext());
        this.TEXT_SIZE = DisplayUtils.dpToPx(getContext(), (int) TEXT_SIZE);
        this.CHART_LINE_SIZE = DisplayUtils.dpToPx(getContext(), (int) CHART_LINE_SIZE);
        this.MARGIN_TEXT = DisplayUtils.dpToPx(getContext(), (int) MARGIN_TEXT);
    }

    /** <br> touch. */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (canScroll) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (switchLayout != null) {
                        switchLayout.requestDisallowInterceptTouchEvent(true);  //禁用外层switchLayout滑动
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (switchLayout != null) {
                        switchLayout.requestDisallowInterceptTouchEvent(false);
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(e);
    }

    /** <br> UI. */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (tempYs == null) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(CHART_LINE_SIZE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorLine));
        canvas.drawLine(        //绘制标度横线
                0, tempYs[0],
                getMeasuredWidth(), tempYs[0],
                paint);
        canvas.drawLine(
                0, tempYs[1],
                getMeasuredWidth(), tempYs[1],
                paint);
    }

    public void setSwitchLayout(SwipeSwitchLayout v) {
        this.switchLayout = v;
    }

    /** <br> data. */
    public void setData(Data data, int dataType, int viewType) {
        if (data == null) {
            return;
        }
        if (dataType == TrendItemView.DATA_TYPE_DAILY) {
            if (canScroll = data.dailyList.size() > 7) {
                smoothScrollToPosition(data.dailyList.size() - 1);
            }
        } else {
            if (canScroll = dataType == TrendItemView.DATA_TYPE_HOURLY && data.hourlyList.size() > 7) {
                smoothScrollToPosition(data.hourlyList.size() - 1);
            }
        }

        calcTempYs(data, dataType, viewType);
        invalidate();
    }

    private void calcTempYs(Data data, int dataType, int viewType) {

        tempYs = new int[]{
                (int) (TrendItemView.calcHeaderHeight(getContext()) + TrendItemView.calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                        - TrendItemView.calcDrawSpecUsableHeight(getContext()) * 3 / 4),
                (int) (TrendItemView.calcHeaderHeight(getContext()) + TrendItemView.calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                        - TrendItemView.calcDrawSpecUsableHeight(getContext()) * 1 / 4)
        };
    }

    private void calcLimitLineY(Data data, int dataType, int viewType) {
        if (dataType == TrendItemView.DATA_TYPE_DAILY) {
            return;
        }

        highest = lowest = 0;
        int limit = 0;
        switch (viewType) {
            case TrendItemView.VIEW_TYPE_HUM:
                for (int i = 0; i < data.hourlyList.size(); i++) {
                    if (data.hourlyList.get(i).hum > highest) {
                        highest = data.hourlyList.get(i).hum;
                    }
                    if (data.hourlyList.get(i).hum < lowest) {
                        lowest = data.hourlyList.get(i).hum;
                    }
                }
                limit = PlanT.getInstance().getHumLimit();
                break;
            case TrendItemView.VIEW_TYPE_BRIGHT:
                for (int i = 0; i < data.hourlyList.size(); i++) {
                    if (data.hourlyList.get(i).bright > highest) {
                        highest = data.hourlyList.get(i).bright;
                    }
                    if (data.hourlyList.get(i).bright < lowest) {
                        lowest = data.hourlyList.get(i).bright;
                    }
                }
                limit = PlanT.getInstance().getBrightLimit();
                break;
            case TrendItemView.VIEW_TYPE_TEMP:
                for (int i = 0; i < data.hourlyList.size(); i++) {
                    if (data.hourlyList.get(i).temp > highest) {
                        highest = data.hourlyList.get(i).temp;
                    }
                    if (data.hourlyList.get(i).temp < lowest) {
                        lowest = data.hourlyList.get(i).temp;
                    }
                }
                limit = PlanT.getInstance().getTempLimit();
                break;
            default:
                break;
        }

        limitLineY = (int) (TrendItemView.calcHeaderHeight(getContext()) + TrendItemView.calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                - TrendItemView.calcDrawSpecUsableHeight(getContext()) * (limit - lowest) / (highest - lowest));
    }
}

