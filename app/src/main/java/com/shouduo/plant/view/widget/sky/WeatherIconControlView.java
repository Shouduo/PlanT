package com.shouduo.plant.view.widget.sky;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.shouduo.plant.utils.DisplayUtils;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public class WeatherIconControlView extends FrameLayout {
    // widget
    private OnWeatherIconChangingListener iconListener;

    // data
    private boolean rose = false;
    private int iconSize;


    /** <br> life cycle. */

    public WeatherIconControlView(Context context) {
        super(context);
        this.initialize();
    }

    public WeatherIconControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public WeatherIconControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeatherIconControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        iconSize = (int) DisplayUtils.dpToPx(getContext(), 120 + 8 * 2);
    }

    /** <br> measure. */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                getChildAt(0).getMeasuredWidth(),
                getChildAt(0).getMeasuredHeight());
    }

    /** <br> layout. */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View circularSkyView;
        View startContainerView;
        View weatherIconContainerView;

        circularSkyView = getChildAt(0);
        startContainerView = getChildAt(1);
        weatherIconContainerView = getChildAt(2);

        circularSkyView.layout(0, 0,
                circularSkyView.getMeasuredWidth(), circularSkyView.getMeasuredHeight());
        startContainerView.layout(0, 0,
                startContainerView.getMeasuredWidth(), startContainerView.getMeasuredHeight());
        weatherIconContainerView.layout(getIconLeft(), getIconTop(),
                getIconLeft() + iconSize, getIconTop() + iconSize);
    }

    /** <br> UI. */
    public void showWeatherIcon() {
        animRise();
    }

    /** <br> data. */
    private int getIconLeft() {
        return (int) ((getMeasuredWidth() - iconSize) * 0.5);
    }

    private int getIconTop() {
        return (int) ((getMeasuredHeight() - iconSize) * 0.5);
    }

    /** <br> animation. */
    private void animRise() {
        if (iconListener != null) {
            iconListener.OnWeatherIconChanging();
        }
    }

    /** <br> interface. */
    public interface OnWeatherIconChangingListener {
        void OnWeatherIconChanging();
    }

    public void setOnWeatherIconChangingListener(OnWeatherIconChangingListener l) {
        iconListener = l;
    }
}
