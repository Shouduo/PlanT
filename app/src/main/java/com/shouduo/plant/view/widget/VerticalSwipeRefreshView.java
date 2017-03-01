package com.shouduo.plant.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public class VerticalSwipeRefreshView extends SwipeRefreshLayout {
    // widget
    private InkPageIndicator indicator;

    // data
    private float initialX, initialY;
    private int touchSlop;

    private boolean isBeingDragged = false;
    private boolean isHorizontalDragged = false;
    private boolean moveActionStarted = false;

    /** <br> life cycle. */
    public VerticalSwipeRefreshView(Context context) {
        super(context);
        this.initialize();
    }

    public VerticalSwipeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    private void initialize() {
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /** <br> touch. */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                isHorizontalDragged = false;
                initialX = ev.getX();
                initialY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isBeingDragged && !isHorizontalDragged) {
                    if (Math.abs(ev.getX() - initialX) > touchSlop
                            || Math.abs(ev.getY() - initialY) > touchSlop) {
                        isBeingDragged = true;
                        if (Math.abs(ev.getX() - initialX) > Math.abs(ev.getY() - initialY)) {
                            isHorizontalDragged = true;
                        }
                    } else {
                        initialX = ev.getX();
                        initialY = ev.getY();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                isHorizontalDragged = false;
                break;
        }

        return result && !isHorizontalDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (!moveActionStarted && indicator != null) {
                moveActionStarted = true;
                indicator.setDisplayState(true);
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            moveActionStarted = false;
            if (indicator != null) {
                indicator.setDisplayState(false);
            }
        }
        return super.onTouchEvent(ev) && !isHorizontalDragged;
    }

    /** <br> UI. */
    public void setIndicator(InkPageIndicator indicator) {
        this.indicator = indicator;
    }

}
