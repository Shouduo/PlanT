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
//    private AnimListener animListener;

    // data
    private boolean rose = false;

//    private int cX;
    private int iconSize;
//    private int radius;

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
//        cX =  getMeasuredWidth() / 2;
    }

    /** <br> measure. */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                getChildAt(0).getMeasuredWidth(),
                getChildAt(0).getMeasuredHeight());

//        radius = (int) (getMeasuredWidth() / 6.8 * 4.0);
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
//        if (rose) {
//            animFall();
//        } else {
//            rose = true;
//            animRise();
//        }

//        getChildAt(2).setVisibility(VISIBLE);

        animRise();
    }

    /** <br> data. */

//    private void calcCX(int startX, int endX, float time) {
//        cX = (int) (startX + (endX - startX) * time);
//    }

    private int getIconLeft() {
        return (int) ((getMeasuredWidth() - iconSize) * 0.5);
    }

    private int getIconTop() {
        return (int) ((getMeasuredHeight() - iconSize) * 0.5);
    }

//    private int getIconCY() {
//        return (int) (getMeasuredHeight()
//                - Math.sqrt(Math.pow(radius, 2) - Math.pow(cX - getMeasuredWidth() / 2.0, 2)));
//    }

    /** <br> animation. */

    private void animRise() {
        if (iconListener != null) {
            iconListener.OnWeatherIconChanging();
        }

//        if (animListener != null) {
//            animListener.canceled = true;
//        }
//        animListener =  new AnimListener(AnimListener.END_TYPE);
//
//        AnimRise animation = new AnimRise();
//        animation.setDuration(800);
//        animation.setInterpolator(new AccelerateDecelerateInterpolator());
//        animation.setAnimationListener(animListener);
//
//        getChildAt(2).setVisibility(VISIBLE);
//        clearAnimation();
//        startAnimation(animation);
    }

//    private void animFall() {
//        if (animListener != null) {
//            animListener.canceled = true;
//        }
//        animListener =  new AnimListener(AnimListener.CONTINUE_TYPE);
//
//        AnimFall animation = new AnimFall();
//        animation.setDuration(400);
//        animation.setInterpolator(new AccelerateDecelerateInterpolator());
//        animation.setAnimationListener(animListener);
//
//        clearAnimation();
//        startAnimation(animation);
//    }
//
//    private class AnimRise extends Animation {
//        // data
//        private int startX;
//        private int endX;
//
//        AnimRise() {
//            startX = (int) (getMeasuredWidth() / 2.0 - radius);
//            endX = (int) (getMeasuredWidth() / 2.0);
//        }
//
//        @Override
//        protected void applyTransformation(float interpolatedTime, Transformation t) {
//            super.applyTransformation(interpolatedTime, t);
//            calcCX(startX, endX, interpolatedTime);
//            requestLayout();
//        }
//    }
//
//    private class AnimFall extends Animation {
//        // data
//        private int startX;
//        private int endX;
//
//        AnimFall() {
//            startX = cX;
//            endX = (int) (getMeasuredWidth() / 2.0 + radius);
//        }
//
//        @Override
//        protected void applyTransformation(float interpolatedTime, Transformation t) {
//            super.applyTransformation(interpolatedTime, t);
//            calcCX(startX, endX, interpolatedTime);
//            requestLayout();
//        }
//    }

//    private class AnimListener implements Animation.AnimationListener {
//        // data
//        private boolean canceled;
//        private int type;
//
//        static final int END_TYPE = 0;
//        static final int CONTINUE_TYPE = 1;
//
//        AnimListener(int type) {
//            this.canceled = false;
//            this.type = type;
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//            // do nothing.
//        }
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            if (!canceled) {
//                switch (type) {
//                    case END_TYPE:
//                        break;
//
//                    case CONTINUE_TYPE:
//                        getChildAt(2).setVisibility(GONE);
//                        animRise();
//                        break;
//                }
//            }
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//            // do nothing.
//        }
//    }

    /** <br> interface. */

    public interface OnWeatherIconChangingListener {
        void OnWeatherIconChanging();
    }

    public void setOnWeatherIconChangingListener(OnWeatherIconChangingListener l) {
        iconListener = l;
    }
}
