package com.shouduo.plant.view.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.model.Data;
import com.shouduo.plant.model.Hourly;
import com.shouduo.plant.utils.DisplayUtils;
import com.shouduo.plant.utils.TimeUtils;
import com.shouduo.plant.view.dialog.SmartConfigDialog;
import com.shouduo.plant.view.widget.InkPageIndicator;
import com.shouduo.plant.view.widget.SafeHandler;
import com.shouduo.plant.view.widget.SwipeSwitchLayout;
import com.shouduo.plant.view.widget.VerticalNestedScrollView;
import com.shouduo.plant.view.widget.VerticalSwipeRefreshView;
import com.shouduo.plant.view.widget.sky.SkyView;
import com.shouduo.plant.view.widget.trend.TrendItemView;
import com.shouduo.plant.view.widget.trend.TrendView;

import org.litepal.crud.DataSupport;

public class MainActivity extends BaseActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, SwipeSwitchLayout.OnSwitchListener,
        SwipeRefreshLayout.OnRefreshListener, NestedScrollView.OnScrollChangeListener,
        SafeHandler.HandlerContainer {

    private static final int SETTINGS_ACTIVITY = 1;
    // widget
    private SafeHandler<MainActivity> handler;

    private SkyView skyView;
    private Toolbar toolbar;

    private SwipeSwitchLayout swipeSwitchLayout;
    private InkPageIndicator indicator;
    private VerticalSwipeRefreshView swipeRefreshLayout;
    private VerticalNestedScrollView nestedScrollView;
    private LinearLayout cardContainer;

    private TextView[] titleTexts;
    private TextView refreshTimeText;

    private TrendView humidityTrendView;
    private TrendView brightnessTrendView;
    private TrendView temperatureTrendView;

    //data
    private int scrollTrigger;
    private Data data;

    // animation
    private AnimatorSet viewShowAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isStarted()) {
            setStarted();
            initData();
            initWidget();
            reset();
        } else if (!swipeRefreshLayout.isRefreshing()) {
            reset();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public View getSnackbarContainer() {
        return swipeSwitchLayout;
    }

    // init.
    private void initWidget() {
        this.handler = new SafeHandler<>(this);

        this.skyView = (SkyView) findViewById(R.id.activity_main_skyView);
        initScrollViewPart();

        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_close);
        toolbar.inflateMenu(R.menu.activity_main);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
    }

    private void initScrollViewPart() {

        // get swipe switch layout.
        this.swipeSwitchLayout = (SwipeSwitchLayout) findViewById(R.id.activity_main_switchView);
        swipeSwitchLayout.setOnSwitchListener(this);

        // get indicator.
        this.indicator = (InkPageIndicator) findViewById(R.id.activity_main_indicator);
        indicator.setSwitchView(swipeSwitchLayout);

        // get swipe refresh layout & set color.
        this.swipeRefreshLayout = (VerticalSwipeRefreshView) findViewById(R.id.activity_main_refreshView);
        if (TimeUtils.getInstance(this).isDayTime()) {
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.lightPrimary_3));
        } else {
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.darkPrimary_1));
        }
        swipeRefreshLayout.setOnRefreshListener(this);

        // get nested scroll view & set listener.
        this.nestedScrollView = (VerticalNestedScrollView) findViewById(R.id.activity_main_scrollView);
        nestedScrollView.setOnScrollChangeListener(this);

        swipeSwitchLayout.setIndicator(indicator);
        swipeRefreshLayout.setIndicator(indicator);
        nestedScrollView.setIndicator(indicator);

        // get realTimeWeather container.
        this.cardContainer = (LinearLayout) findViewById(R.id.container_data);
        viewShowAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_in);
        viewShowAnimator.setTarget(cardContainer);

        // get touch layout, set height & get realTime texts.
        RelativeLayout touchLayout = (RelativeLayout) findViewById(R.id.container_data_touchLayout);
        LinearLayout.LayoutParams touchParams = (LinearLayout.LayoutParams) touchLayout.getLayoutParams();
        touchParams.height = scrollTrigger;
        touchLayout.setLayoutParams(touchParams);
        touchLayout.setOnClickListener(this);

        this.titleTexts = new TextView[]{
                (TextView) findViewById(R.id.container_data_number_of_days),
                (TextView) findViewById(R.id.container_data_text_of_days),
                (TextView) findViewById(R.id.container_data_realtime_state_text),
                (TextView) findViewById(R.id.container_data_realtime_state_value)};

        this.refreshTimeText = (TextView) findViewById(R.id.container_data_refresh_time);

        // data card.
        this.initTrendView();
    }

    private void initTrendView() {

        this.humidityTrendView = (TrendView) findViewById(R.id.container_humidity_trendView);
        this.brightnessTrendView = (TrendView) findViewById(R.id.container_brightness_trendView);
        this.temperatureTrendView = (TrendView) findViewById(R.id.container_temperature_trendView);

        humidityTrendView.setSwitchLayout(swipeSwitchLayout);
        brightnessTrendView.setSwitchLayout(swipeSwitchLayout);
        temperatureTrendView.setSwitchLayout(swipeSwitchLayout);

    }

    // reset.
    public void reset() {
        skyView.reset();
        this.resetScrollViewPart();
    }

    private void resetScrollViewPart() {

        setRefreshing(false);
        buildUI();

    }

    // build UI.
    private void setRefreshing(final boolean b) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(b);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void buildUI() {

        DisplayUtils.setWindowTopColor(this);
        skyView.setWeather();

        data = new Data(handler);

        if (PlanT.getInstance().isAutoSync()) {
            data.refreshData();
        }

        if (data.getDataFromDatabase()) {
            Hourly hourly = DataSupport.findLast(Hourly.class);

            titleTexts[0].setText(data.calcNumberOfDays() + "");
            titleTexts[1].setText(data.calcNumberOfDays()>1? "Days" : "Day");
            titleTexts[2].setText("Humidity:\n" +
                                  "Brightness:\n" +
                                  "Temperature:" );
            titleTexts[3].setText(hourly.hum + " %\n"
                                 +hourly.bright + " lux\n"
                                 +hourly.temp + " °C");

            refreshTimeText.setText("Last Sync: " + data.base.refreshTime);

            humidityTrendView.setData(data, TrendItemView.VIEW_TYPE_HUM);
            humidityTrendView.setState(TrendItemView.DATA_TYPE_HOURLY, false);

            brightnessTrendView.setData(data, TrendItemView.VIEW_TYPE_BRIGHT);
            brightnessTrendView.setState(TrendItemView.DATA_TYPE_HOURLY, false);

            temperatureTrendView.setData(data, TrendItemView.VIEW_TYPE_TEMP);
            temperatureTrendView.setState(TrendItemView.DATA_TYPE_HOURLY, false);
        }

        cardContainer.setVisibility(View.VISIBLE);
        viewShowAnimator.start();
    }

    // init.
    private void initData() {

        this.scrollTrigger = (int) (getResources().getDisplayMetrics().widthPixels / 6.8 * 4
                + DisplayUtils.dpToPx(this, 60)
                - DisplayUtils.dpToPx(this, 300 - 256));
    }

    // on click listener.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;

            case R.id.container_data_touchLayout:
            case R.id.activity_main_toolbar:
                skyView.onClickSky();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_manage:
//                startActivity(new Intent(this, ConfigActivity.class));
                SmartConfigDialog smartConfigDialog = new SmartConfigDialog();
                smartConfigDialog.show(getFragmentManager(), null);
                break;

            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_ACTIVITY);
                break;

            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SETTINGS_ACTIVITY:
                reset();
                break;
            default:
                break;
        }
    }

    // on swipe listener(swipe switch layout).
    @Override
    public void swipeTakeEffect(int direction) {

    }

    // on refresh listener.
    @Override
    public void onRefresh() {
        data.refreshData();

    }

    // on scroll changed listener.
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        skyView.setTranslationY((float) (-Math.min(1, 1.0 * scrollY / scrollTrigger)
                * skyView.getMeasuredHeight()));
        toolbar.setTranslationY((float) (-Math.min(1, 1.0 * scrollY / scrollTrigger)
                * toolbar.getMeasuredHeight()));
        toolbar.setAlpha((float) (1.0 - scrollY / (scrollTrigger - DisplayUtils.dpToPx(this, 75))));
        for (int i = 0; i < titleTexts.length; i++) {
            titleTexts[i].setAlpha((float) (1.0 - scrollY / (scrollTrigger - DisplayUtils.dpToPx(this, 25))));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scrollY > skyView.getMeasuredHeight() * 0.71) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.lightPrimary_5));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    // handle data refreshing result.
    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Data.SERVER_DOWN:
                Toast.makeText(this, "Sync failed, Try again later", Toast.LENGTH_SHORT).show();
                break;
            case Data.SERVER_GOOD:
                reset();
                break;
        }
        setRefreshing(false);
    }
}
