package com.shouduo.plant.view.widget.trend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.shouduo.plant.R;
import com.shouduo.plant.model.Data;
import com.shouduo.plant.utils.DisplayUtils;

/**
 * Created by 刘亨俊 on 17.2.1.
 */


public class TrendItemView extends FrameLayout {
    // widget.
    private Paint paint;
    private Path path;
    private Shader shader;

    // data.
    private int[] chart1 = new int[3];
    private int[] chart2 = new int[3];
    private int[] hourlyHumYs = new int[3];
    private int[] hourlyConsumeYs = new int[3];
    private int[] hourlyBrightYs = new int[3];
    private int[] hourlyTempYs = new int[3];
    private int[] dailyConsumeYs = new int[3];
    private int[] dailyBrightYs = new int[3];
    private int[] dailyTempDiffYs = new int[3];

    private int dataType = DATA_TYPE_NULL;
    public static final int DATA_TYPE_NULL = 0;
    public static final int DATA_TYPE_DAILY = 1;
    public static final int DATA_TYPE_HOURLY = -1;

    private int viewType = VIEW_TYPE_NULL;
    public static final int VIEW_TYPE_NULL = 0;
    public static final int VIEW_TYPE_HUM = 1;
    public static final int VIEW_TYPE_BRIGHT = 2;
    public static final int VIEW_TYPE_TEMP = 3;

    private int positionType = POSITION_TYPE_NULL;
    public static final int POSITION_TYPE_NULL = 7;
    public static final int POSITION_TYPE_LEFT = -1;
    public static final int POSITION_TYPE_CENTER = 0;
    public static final int POSITION_TYPE_RIGHT = 1;

    private int[] lineColors;
    private int[] shadowColors;
    private int textColor;

    private float MARGIN_TOP;
    private float MARGIN_BOTTOM;
    private float WEATHER_TEXT_SIZE = 13;
    private float POP_TEXT_SIZE = 11;
    private float TREND_LINE_SIZE = 2;
    private float CHART_LINE_SIZE = 1;
    private float MARGIN_TEXT = 2;

    private static final int DRAW_SPEC_MARGIN_TOP = 48;
    private static final int DRAW_SPEC_USABLE_HEIGHT = 96;
    private static final int DRAW_SPEC_MARGIN_BOTTOM = 60;

    /** <br> life cycle. */
    public TrendItemView(Context context) {
        super(context);
        this.initialize();
    }

    public TrendItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public TrendItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TrendItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        setWillNotDraw(false);

        this.lineColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.colorChartLine_1),
                ContextCompat.getColor(getContext(), R.color.colorChartLine_2),
                ContextCompat.getColor(getContext(), R.color.colorChartLine_3),
                ContextCompat.getColor(getContext(), R.color.colorLine)};
        this.shadowColors = new int[]{
                Color.argb(50, 176, 176, 176),
                Color.argb(0, 176, 176, 176),
                Color.argb(200, 176, 176, 176)};
        this.textColor = ContextCompat.getColor(getContext(), R.color.colorTextContent);

        this.MARGIN_TOP = calcHeaderHeight(getContext()) + calcDrawSpecMarginTop(getContext());
        this.MARGIN_BOTTOM = calcDrawSpecMarginBottom(getContext());
        this.WEATHER_TEXT_SIZE = DisplayUtils.dpToPx(getContext(), (int) WEATHER_TEXT_SIZE);
        this.POP_TEXT_SIZE = DisplayUtils.dpToPx(getContext(), (int) POP_TEXT_SIZE);
        this.TREND_LINE_SIZE = DisplayUtils.dpToPx(getContext(), (int) TREND_LINE_SIZE);
        this.CHART_LINE_SIZE = DisplayUtils.dpToPx(getContext(), (int) CHART_LINE_SIZE);
        this.MARGIN_TEXT = DisplayUtils.dpToPx(getContext(), (int) MARGIN_TEXT);

        this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);

        this.path = new Path();

        this.shader = new LinearGradient(
                0, MARGIN_TOP,
                0, calcHeaderHeight(getContext()) + calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM,
                shadowColors[0], shadowColors[1],
                Shader.TileMode.CLAMP);
    }

    // measure.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getChildAt(0).measure(
                MeasureSpec.makeMeasureSpec(calcWidth(getContext()), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calcHeaderHeight(getContext()), MeasureSpec.EXACTLY));
        setMeasuredDimension(
                calcWidth(getContext()),
                calcHeaderHeight(getContext()) + calcDrawSpecHeight(getContext()));
    }

    // draw.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTimeLine(canvas);
        switch (viewType) {
            case VIEW_TYPE_HUM:
                switch (dataType) {
                    case DATA_TYPE_HOURLY:
                        drawLineChart(canvas, hourlyHumYs, chart1, "%", lineColors[0]);
                        drawBarChart(canvas, hourlyConsumeYs, chart2, "%", lineColors[0]);
                        break;
                    case DATA_TYPE_DAILY:
                        drawBarChart(canvas, dailyConsumeYs, chart1, "%", lineColors[0]);
                        break;
                }
                break;
            case VIEW_TYPE_BRIGHT:
                switch (dataType) {
                    case DATA_TYPE_HOURLY:
                        drawLineChart(canvas, hourlyBrightYs, chart1, "lux", lineColors[1]);
                        break;
                    case DATA_TYPE_DAILY:
                        drawBarChart(canvas, dailyBrightYs, chart1, "lux", lineColors[1]);
                        break;
                }
                break;
            case VIEW_TYPE_TEMP:
                switch (dataType) {
                    case DATA_TYPE_HOURLY:
                        drawLineChart(canvas, hourlyTempYs, chart1, "°C", lineColors[2]);
                        break;
                    case DATA_TYPE_DAILY:
                        drawBarChart(canvas, dailyTempDiffYs, chart1, "°C", lineColors[2]);
                        break;
                }
                break;
        }
    }

    //绘制时间竖线
    private void drawTimeLine(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(CHART_LINE_SIZE);
        paint.setColor(lineColors[3]);
        canvas.drawLine(
                (float) (getMeasuredWidth() / 2.0), MARGIN_TOP,
                (float) (getMeasuredWidth() / 2.0), getMeasuredHeight() - MARGIN_BOTTOM,
                paint);
    }

    //绘制折线图
    private void drawLineChart(Canvas canvas, int[] dataYs, int[] chart, String unit, int lineColor) {
        switch (positionType) {
            case POSITION_TYPE_NULL:
                return;

            case POSITION_TYPE_LEFT:
                // shadow.
                paint.setShader(shader);
                paint.setStyle(Paint.Style.FILL);
                paint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);

                path.reset();
                path.moveTo((float) (getMeasuredWidth() / 2.0), dataYs[1]);
                path.lineTo(getMeasuredWidth(), dataYs[2]);
                path.lineTo(getMeasuredWidth(), getMeasuredHeight() - MARGIN_BOTTOM);
                path.lineTo((float) (getMeasuredWidth() / 2.0), getMeasuredHeight() - MARGIN_BOTTOM);
                path.close();
                canvas.drawPath(path, paint);

                // line.
                paint.setShader(null);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(TREND_LINE_SIZE);
                paint.setColor(lineColor);
                paint.setShadowLayer(2, 0, 2, shadowColors[2]);

                path.reset();
                path.moveTo((float) (getMeasuredWidth() / 2.0), dataYs[1]);
                path.lineTo(getMeasuredWidth(), dataYs[2]);
                canvas.drawPath(path, paint);
                break;

            case POSITION_TYPE_RIGHT:
                // shadow.
                paint.setShader(shader);
                paint.setStyle(Paint.Style.FILL);
                paint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);

                path.reset();
                path.moveTo(0, dataYs[0]);
                path.lineTo((float) (getMeasuredWidth() / 2.0), dataYs[1]);
                path.lineTo((float) (getMeasuredWidth() / 2.0), getMeasuredHeight() - MARGIN_BOTTOM);
                path.lineTo(0, getMeasuredHeight() - MARGIN_BOTTOM);
                path.close();
                canvas.drawPath(path, paint);

                // line.
                paint.setShader(null);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(TREND_LINE_SIZE);
                paint.setColor(lineColor);
                paint.setShadowLayer(2, 0, 2, shadowColors[2]);

                path.reset();
                path.moveTo(0, dataYs[0]);
                path.lineTo((float) (getMeasuredWidth() / 2.0), dataYs[1]);
                canvas.drawPath(path, paint);
                break;

            case POSITION_TYPE_CENTER:
                // shadow.
                paint.setShader(shader);
                paint.setStyle(Paint.Style.FILL);
                paint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);

                path.reset();
                path.moveTo(0, dataYs[0]);
                path.cubicTo((float) (getMeasuredWidth() / 2.0), dataYs[1],
                        (float) (getMeasuredWidth() / 2.0), dataYs[1],
                        getMeasuredWidth(), dataYs[2]);
                path.lineTo(getMeasuredWidth(), getMeasuredHeight() - MARGIN_BOTTOM);
                path.lineTo(0, getMeasuredHeight() - MARGIN_BOTTOM);
                path.close();
                canvas.drawPath(path, paint);

                // line.
                paint.setShader(null);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(TREND_LINE_SIZE);
                paint.setColor(lineColor);
                paint.setShadowLayer(2, 0, 2, shadowColors[2]);

                path.reset();
                path.moveTo(0, dataYs[0]);
                path.cubicTo((float) (getMeasuredWidth() / 2.0), dataYs[1],
                        (float) (getMeasuredWidth() / 2.0), dataYs[1],
                        getMeasuredWidth(), dataYs[2]);
                canvas.drawPath(path, paint);
                break;
        }

        // text.
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(WEATHER_TEXT_SIZE);
        paint.setShadowLayer(2, 0, 2, shadowColors[2]);
        canvas.drawText(
                chart[1] + unit,
                (float) (getMeasuredWidth() / 2.0),
                dataYs[1] - paint.getFontMetrics().bottom - MARGIN_TEXT,
                paint);
    }
    //绘制柱状图
    private void drawBarChart(Canvas canvas, int[] dataYs, int[] chart, String unit, int lineColor) {
        paint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);

        paint.setColor(lineColor);
        paint.setAlpha((int) (255 * 0.5));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(
                new RectF(
                        (float) (getMeasuredWidth() / 2.0 - TREND_LINE_SIZE * 1.5),
                        dataYs[0],
                        (float) (getMeasuredWidth() / 2.0 + TREND_LINE_SIZE * 1.5),
                        getMeasuredHeight() - MARGIN_BOTTOM),
                TREND_LINE_SIZE * 3, TREND_LINE_SIZE * 3,
                paint);

        paint.setColor(textColor);
        paint.setAlpha((int) (255 * 0.5));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(POP_TEXT_SIZE);
        canvas.drawText(
                chart[1] + unit,
                (float) (getMeasuredWidth() / 2.0),
                getMeasuredHeight() - MARGIN_BOTTOM - paint.getFontMetrics().top + MARGIN_TEXT,
                paint);

        paint.setAlpha(255);
    }

    // init.为减少switch嵌套，数据分为setDailyData()和setHourlyData()两个方法
    public void setData(Data data, int dataType, int viewType, int position, int highest, int lowest) {
        this.dataType = dataType;
        this.viewType = viewType;
        switch (dataType) {
            case DATA_TYPE_DAILY:
                setDailyData(data, viewType, position, highest, lowest);
                break;

            case DATA_TYPE_HOURLY:
                setHourlyData(data, viewType, position, highest, lowest);
                break;
        }
    }

    public void setNullData() {
        this.dataType = DATA_TYPE_NULL;
    }

    // daily.
    private void setDailyData(Data data, int viewType, int position, int highest, int lowest) {
        if (position == 0) {
            positionType = POSITION_TYPE_LEFT;
            switch (viewType) {
                case VIEW_TYPE_HUM:
                    this.chart1 = new int[]{
                            0,
                            data.dailyList.get(position).consume,
                            data.dailyList.get(position + 1).consume};
                    dailyConsumeYs = setBarChartData(chart1);
                    break;
                case VIEW_TYPE_BRIGHT:
                    this.chart1 = new int[]{
                            0,
                            data.dailyList.get(position).bright,
                            data.dailyList.get(position + 1).bright};
                    dailyBrightYs = setBarChartData(chart1);
                    break;
                case VIEW_TYPE_TEMP:
                    this.chart1 = new int[]{
                            0,
                            data.dailyList.get(position).tempDiff,
                            data.dailyList.get(position + 1).tempDiff};
                    dailyTempDiffYs = setBarChartData(chart1);
                    break;
            }

        } else if (position == data.dailyList.size() - 1) {
            positionType = POSITION_TYPE_RIGHT;
            switch (viewType) {
                case VIEW_TYPE_HUM:
                    this.chart1 = new int[]{
                            data.dailyList.get(position - 1).consume,
                            data.dailyList.get(position).consume,
                            0};
                    dailyConsumeYs = setBarChartData(chart1);
                    break;
                case VIEW_TYPE_BRIGHT:
                    this.chart1 = new int[]{
                            data.dailyList.get(position - 1).bright,
                            data.dailyList.get(position).bright,
                            0};
                    dailyBrightYs = setBarChartData(chart1);
                    break;
                case VIEW_TYPE_TEMP:
                    this.chart1 = new int[]{
                            data.dailyList.get(position - 1).tempDiff,
                            data.dailyList.get(position).tempDiff,
                            0};
                    dailyTempDiffYs = setBarChartData(chart1);
                    break;
            }

        } else {
            positionType = POSITION_TYPE_CENTER;
            switch (viewType) {
                case VIEW_TYPE_HUM:
                    this.chart1 = new int[]{
                            data.dailyList.get(position - 1).consume,
                            data.dailyList.get(position).consume,
                            data.dailyList.get(position + 1).consume};
                    dailyConsumeYs = setBarChartData(chart1);
                    break;
                case VIEW_TYPE_BRIGHT:
                    this.chart1 = new int[]{
                            data.dailyList.get(position - 1).bright,
                            data.dailyList.get(position).bright,
                            data.dailyList.get(position + 1).bright};
                    dailyBrightYs = setBarChartData(chart1);
                    break;
                case VIEW_TYPE_TEMP:
                    this.chart1 = new int[]{
                            data.dailyList.get(position - 1).tempDiff,
                            data.dailyList.get(position).tempDiff,
                            data.dailyList.get(position + 1).tempDiff};
                    dailyTempDiffYs = setBarChartData(chart1);
                    break;
            }
        }
    }

    // hourly.
    public void setHourlyData(Data data, int viewType, int position, int highest, int lowest) {

        if (position == 0) {
            positionType = POSITION_TYPE_LEFT;
            switch (viewType) {
                case VIEW_TYPE_HUM:
                    this.chart1 = new int[]{
                            0,
                            data.hourlyList.get(position).hum,
                            data.hourlyList.get(position + 1).hum};
                    hourlyHumYs = setLineChartLeftData(highest, lowest, chart1);

                    this.chart2 = new int[]{
                            0,
                            data.hourlyList.get(position).consume,
                            data.hourlyList.get(position + 1).consume};
                    hourlyConsumeYs = setBarChartData(chart2);
                    break;
                case VIEW_TYPE_BRIGHT:
                    this.chart1 = new int[]{
                            0,
                            data.hourlyList.get(position).bright,
                            data.hourlyList.get(position + 1).bright};
                    hourlyBrightYs = setLineChartLeftData(highest, lowest, chart1);
                    break;
                case VIEW_TYPE_TEMP:
                    this.chart1 = new int[]{
                            0,
                            data.hourlyList.get(position).temp,
                            data.hourlyList.get(position + 1).temp};
                    hourlyTempYs = setLineChartLeftData(highest, lowest, chart1);
                    break;
            }

        } else if (position == data.hourlyList.size() - 1) {
            positionType = POSITION_TYPE_RIGHT;
            switch (viewType) {
                case VIEW_TYPE_HUM:
                    this.chart1 = new int[]{
                            data.hourlyList.get(position - 1).hum,
                            data.hourlyList.get(position).hum,
                            0};
                    hourlyHumYs = setLineChartRightData(highest, lowest, chart1);

                    this.chart2 = new int[]{
                            data.hourlyList.get(position - 1).consume,
                            data.hourlyList.get(position).consume,
                            0};
                    hourlyConsumeYs = setBarChartData(chart2);
                    break;
                case VIEW_TYPE_BRIGHT:
                    this.chart1 = new int[]{
                            data.hourlyList.get(position - 1).bright,
                            data.hourlyList.get(position).bright,
                            0};
                    hourlyBrightYs = setLineChartRightData(highest, lowest, chart1);
                    break;
                case VIEW_TYPE_TEMP:
                    this.chart1 = new int[]{
                            data.hourlyList.get(position - 1).temp,
                            data.hourlyList.get(position).temp,
                            0};
                    hourlyTempYs = setLineChartRightData(highest, lowest, chart1);
                    break;
            }

        } else {
            positionType = POSITION_TYPE_CENTER;
            switch (viewType) {
                case VIEW_TYPE_HUM:
                    this.chart1 = new int[]{
                            data.hourlyList.get(position - 1).hum,
                            data.hourlyList.get(position).hum,
                            data.hourlyList.get(position + 1).hum};
                    hourlyHumYs = setLineChartCenterData(highest, lowest, chart1);

                    this.chart2 = new int[]{
                            data.hourlyList.get(position - 1).consume,
                            data.hourlyList.get(position).consume,
                            data.hourlyList.get(position + 1).consume};
                    hourlyConsumeYs = setBarChartData(chart2);
                    break;
                case VIEW_TYPE_BRIGHT:
                    this.chart1 = new int[]{
                            data.hourlyList.get(position - 1).bright,
                            data.hourlyList.get(position).bright,
                            data.hourlyList.get(position + 1).bright};
                    hourlyBrightYs = setLineChartCenterData(highest, lowest, chart1);
                    break;
                case VIEW_TYPE_TEMP:
                    this.chart1 = new int[]{
                            data.hourlyList.get(position - 1).temp,
                            data.hourlyList.get(position).temp,
                            data.hourlyList.get(position + 1).temp};
                    hourlyTempYs = setLineChartCenterData(highest, lowest, chart1);
                    break;
            }
        }
    }

    private int[] setLineChartLeftData(int highest, int lowest, int[] chart) {
        float[] temps = new float[]{
                chart[0],
                chart[1],
                (float) ((chart[1] + chart[2]) / 2.0)};

        int[] dataYs = new int[3];
        for (int i = 0; i < temps.length; i++) {
            dataYs[i] = (int) (calcHeaderHeight(getContext()) + calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                    - calcDrawSpecUsableHeight(getContext())
                    * (temps[i] - lowest)
                    / (highest - lowest));
        }
        return dataYs;
    }

    private int[] setLineChartRightData(int highest, int lowest, int[] chart) {
        float[] temps = new float[]{
                (float) ((chart[0] + chart[1]) / 2.0),
                chart[1],
                chart[2]};

        int[] dataYs = new int[3];
        for (int i = 0; i < temps.length; i++) {
            dataYs[i] = (int) (calcHeaderHeight(getContext()) + calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                    - calcDrawSpecUsableHeight(getContext())
                    * (temps[i] - lowest)
                    / (highest - lowest));
        }
        return dataYs;
    }

    private int[] setLineChartCenterData(int highest, int lowest, int[] chart) {
        float[] temps = new float[]{
                (float) ((chart[0] + chart[1]) / 2.0),
                chart[1],
                (float) ((chart[1] + chart[2]) / 2.0)};

        int[] dataYs = new int[3];
        for (int i = 0; i < temps.length; i++) {
            dataYs[i] = (int) (calcHeaderHeight(getContext()) + calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                    - calcDrawSpecUsableHeight(getContext())
                    * (temps[i] - lowest)
                    / (highest - lowest));
        }
        return dataYs;
    }

    private int[] setBarChartData(int[] chart) {
        int[] dataYs = new int[1];
        dataYs[0] = (int) (calcHeaderHeight(getContext()) + calcDrawSpecHeight(getContext()) - MARGIN_BOTTOM
                - calcDrawSpecUsableHeight(getContext()) * chart[1] / 100.0);
        return dataYs;
    }

    // size.
    public static int calcWidth(Context context) {
        return (int) ((context.getResources().getDisplayMetrics().widthPixels - 2.0 * DisplayUtils.dpToPx(context, 8)) / 7.0);
    }

    public static int calcHeaderHeight(Context context) {
        return (int) (DisplayUtils.dpToPx(context, 2 * 8 + 14)
                + Math.min(calcWidth(context), DisplayUtils.dpToPx(context, 0))); //trend weather icon height
    }

    public static int calcDrawSpecHeight(Context context) {
        return (int) DisplayUtils.dpToPx(
                context,
                DRAW_SPEC_MARGIN_TOP + DRAW_SPEC_USABLE_HEIGHT + DRAW_SPEC_MARGIN_BOTTOM);
    }

    public static int calcDrawSpecMarginTop(Context context) {
        return (int) DisplayUtils.dpToPx(
                context,
                DRAW_SPEC_MARGIN_TOP);
    }

    public static int calcDrawSpecUsableHeight(Context context) {
        return (int) DisplayUtils.dpToPx(
                context,
                DRAW_SPEC_USABLE_HEIGHT);
    }

    public static int calcDrawSpecMarginBottom(Context context) {
        return (int) DisplayUtils.dpToPx(
                context,
                DRAW_SPEC_MARGIN_BOTTOM);
    }
}

