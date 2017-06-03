package com.shouduo.plant.view.widget.trend;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shouduo.plant.R;
import com.shouduo.plant.model.Data;
import com.shouduo.plant.utils.DisplayUtils;
import com.shouduo.plant.view.widget.SwipeSwitchLayout;

/**
 * Created by 刘亨俊 on 17.2.1.
 */

public class TrendView extends FrameLayout
        implements TrendAdapter.OnTrendItemClickListener {
    // widget
    private TrendRecyclerView recyclerView;
    private TextView bottomText;
    private SwipeSwitchLayout swipeSwitchLayout;

    // data
    private TrendAdapter adapter;
    private Data mData;

    private int dataType = TrendItemView.DATA_TYPE_DAILY;

    private boolean animating = false;

    // animator
    private AnimatorSet viewIn;
    private AnimatorSet viewOut;
    private int viewType;

    /** <br> life cycle. */
    public TrendView(Context context) {
        super(context);
        this.initialize();
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public TrendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TrendView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.container_trend_view, null);
        addView(view);

        this.adapter = new TrendAdapter(getContext(), null, this);

        this.recyclerView = (TrendRecyclerView) findViewById(R.id.container_trend_view_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        this.bottomText = (TextView) findViewById(R.id.container_trend_view_bottom_text);
        bottomText.setText("--hourly");

        viewIn = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.view_in);
        viewIn.setInterpolator(new AccelerateDecelerateInterpolator());
        viewIn.addListener(viewInListener);
        viewIn.setTarget(recyclerView);

        viewOut = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.view_out);
        viewOut.setInterpolator(new AccelerateDecelerateInterpolator());
        viewOut.addListener(viewOutListener);
        viewOut.setTarget(recyclerView);
    }

    public void setSwitchLayout(SwipeSwitchLayout switchLayout) {
        this.swipeSwitchLayout = switchLayout;
        recyclerView.setSwitchLayout(swipeSwitchLayout);
    }

    /** <br> UI. */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels - 2.0 * DisplayUtils.dpToPx(getContext(), 8));
        int height = TrendItemView.calcHeaderHeight(getContext()) + TrendItemView.calcDrawSpecHeight(getContext());
        getChildAt(0).measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    /** data. */
    public void setData(Data data, int viewType) {
        if (data != null) {
            this.mData = data;
            this.viewType = viewType;
        }
    }

    public void setState(int dataType, boolean animate) {
        if (animate) {
            if (dataType == this.dataType || animating) {
                return;
            }
            this.dataType = dataType;
            viewOut.start();
        } else {
            viewIn.cancel();
            viewOut.cancel();
            this.dataType = dataType;

            adapter.setData(mData, this.dataType, viewType);
            adapter.notifyDataSetChanged();

            recyclerView.setData(mData, this.dataType, viewType);
        }
    }

    /** <br> interface. */
    @Override
    public void onTrendItemClick() {
        switch (dataType) {
            case TrendItemView.DATA_TYPE_DAILY:
                setState(TrendItemView.DATA_TYPE_HOURLY, true);
                bottomText.setText("--hourly");
                break;

            case TrendItemView.DATA_TYPE_HOURLY:
                setState(TrendItemView.DATA_TYPE_DAILY, true);
                bottomText.setText("--daily");
                break;
        }
    }

    private AnimatorListenerAdapter viewOutListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationStart(Animator animation) {
            animating = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animating = false;

            adapter.setData(mData, dataType, viewType);
            adapter.notifyDataSetChanged();

            recyclerView.setData(mData, dataType, viewType);

            viewIn.start();
        }
    };

    private AnimatorListenerAdapter viewInListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationStart(Animator animation) {
            animating = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animating = false;
        }
    };
}
