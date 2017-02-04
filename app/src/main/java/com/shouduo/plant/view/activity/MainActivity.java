package com.shouduo.plant.view.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shouduo.plant.R;
import com.shouduo.plant.model.History;
import com.shouduo.plant.model.Weather;
import com.shouduo.plant.utils.DisplayUtils;
import com.shouduo.plant.utils.TimeUtils;
import com.shouduo.plant.view.widget.InkPageIndicator;
import com.shouduo.plant.view.widget.SafeHandler;
import com.shouduo.plant.view.widget.SwipeSwitchLayout;
import com.shouduo.plant.view.widget.VerticalNestedScrollView;
import com.shouduo.plant.view.widget.VerticalSwipeRefreshView;
import com.shouduo.plant.view.widget.sky.SkyView;
import com.shouduo.plant.view.widget.trend.TrendItemView;
import com.shouduo.plant.view.widget.trend.TrendRecyclerView;
import com.shouduo.plant.view.widget.trend.TrendView;

public class MainActivity extends BaseActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener, SwipeSwitchLayout.OnSwitchListener,
        SwipeRefreshLayout.OnRefreshListener, NestedScrollView.OnScrollChangeListener,
        SafeHandler.HandlerContainer {

    // widget
    private SafeHandler<MainActivity> handler;

    //    private StatusBarView statusBar;
    private SkyView skyView;
    private Toolbar toolbar;

    private SwipeSwitchLayout swipeSwitchLayout;
    private InkPageIndicator indicator;
    private VerticalSwipeRefreshView swipeRefreshLayout;
    private VerticalNestedScrollView nestedScrollView;
    private LinearLayout weatherContainer;

    private TextView[] titleTexts;

    private TextView refreshTime;
    private FrameLayout locationIconBtn;
    private ImageView locationIcon;
    private TextView locationText;

    private TextView overviewTitle;
    private TrendView trendView;
    private TextView lifeInfoTitle;
//    private IndexListView indexListView;

    //data
    private int scrollTrigger;

    // animation
    private AnimatorSet viewShowAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isStarted()) {
            setStarted();
            initData();
            initWidget();
            reset();
        } else if (!swipeRefreshLayout.isRefreshing()) {
//            Weather memory = DatabaseHelper.getInstance(this).readWeather(locationNow);
//            if (locationNow.weather != null && memory != null
//                    && !memory.base.time.equals(locationNow.weather.base.time)) {
//                locationNow.weather = memory;
//                locationNow.history = DatabaseHelper.getInstance(this).readHistory(memory);
            reset();
//            }
        }
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

/*        this.statusBar = (StatusBarView) findViewById(R.id.activity_main_statusBar);
        this.setStatusBarColor();*/

        this.skyView = (SkyView) findViewById(R.id.activity_main_skyView);
        initScrollViewPart();

        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_close);
        toolbar.inflateMenu(R.menu.activity_main);
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
    }

/*    public void setStatusBarColor() {
        if (TimeUtils.getInstance(this).isDayTime()) {
            statusBar.setBackgroundColor(Color.TRANSPARENT);
        } else {
            statusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.darkPrimary_5));
        }
    }*/

    private void initScrollViewPart() {

        // get swipe switch layout.
        this.swipeSwitchLayout = (SwipeSwitchLayout) findViewById(R.id.activity_main_switchView);
//        swipeSwitchLayout.setData(locationList, locationNow);
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
        this.weatherContainer = (LinearLayout) findViewById(R.id.container_weather);
        viewShowAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_in);
        viewShowAnimator.setTarget(weatherContainer);

        // get touch layout, set height & get realTime texts.
        RelativeLayout touchLayout = (RelativeLayout) findViewById(R.id.container_weather_touchLayout);
        LinearLayout.LayoutParams touchParams = (LinearLayout.LayoutParams) touchLayout.getLayoutParams();
        touchParams.height = scrollTrigger;
        touchLayout.setLayoutParams(touchParams);
        touchLayout.setOnClickListener(this);

        this.titleTexts = new TextView[]{
                (TextView) findViewById(R.id.container_weather_realtime_tempTxt),
                (TextView) findViewById(R.id.container_weather_realtime_weatherTxt),
                (TextView) findViewById(R.id.container_weather_realtime_aqiTxt)};

        // realTimeWeather card.
        this.initWeatherCard();

        //get life info
//        this.indexListView = (IndexListView) findViewById(R.id.container_weather_lifeInfoView);
    }


    private void initWeatherCard() {
        this.refreshTime = (TextView) findViewById(R.id.container_weather_time_text_live);

        findViewById(R.id.container_weather_locationContainer).setOnClickListener(this);

        this.locationIconBtn = (FrameLayout) findViewById(R.id.container_weather_location_iconButton);
        locationIconBtn.setOnClickListener(this);
        this.locationIcon = (ImageView) findViewById(R.id.container_weather_location_icon);
        this.locationText = (TextView) findViewById(R.id.container_weather_location_text_live);

        this.overviewTitle = (TextView) findViewById(R.id.container_weather_overviewTitle);

        this.trendView = (TrendView) findViewById(R.id.container_weather_trendView);
        ((TrendRecyclerView) findViewById(R.id.container_trend_view_recyclerView)).setSwitchLayout(swipeSwitchLayout);

        this.lifeInfoTitle = (TextView) findViewById(R.id.container_weather_lifeInfoTitle);
    }

    // reset.

    public void reset() {
        skyView.reset();
        this.resetScrollViewPart();
    }

    private void resetScrollViewPart() {
        weatherContainer.setVisibility(View.GONE);
        nestedScrollView.scrollTo(0, 0);

        swipeSwitchLayout.reset();
        swipeSwitchLayout.setEnabled(true);

        setRefreshing(false);
        buildUI();

/*        if (locationNow.weather == null) {
            setRefreshing(true);
            onRefresh();
        } else {
            setRefreshing(false);
            buildUI();
        }*/
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
/*        Weather weather = locationNow.weather;
        if (weather == null) {
            return;
        } else {
            TimeUtils.getInstance(this).getDayTime(this, weather, true);
        }*/

//        setStatusBarColor();
        DisplayUtils.setWindowTopColor(this);
//        DisplayUtils.setNavigationBarColor(this, TimeUtils.getInstance(this).isDayTime());

        skyView.setWeather();

        titleTexts[0].setText("Hello");
        titleTexts[1].setText("world");
        titleTexts[2].setText("bye~");

        refreshTime.setText("normal");
        locationText.setText("dayday");

/*        if (weather.alertList.size() == 0) {
            locationIconBtn.setEnabled(false);
            locationIcon.setImageResource(R.drawable.ic_location);
        } else {
            locationIconBtn.setEnabled(true);
            locationIcon.setImageResource(R.drawable.ic_alert);
        }*/

        if (TimeUtils.getInstance(this).isDayTime()) {
            overviewTitle.setTextColor(ContextCompat.getColor(this, R.color.lightPrimary_3));
            lifeInfoTitle.setTextColor(ContextCompat.getColor(this, R.color.lightPrimary_3));
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.lightPrimary_3));
        } else {
            overviewTitle.setTextColor(ContextCompat.getColor(this, R.color.darkPrimary_1));
            lifeInfoTitle.setTextColor(ContextCompat.getColor(this, R.color.darkPrimary_1));
            swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.darkPrimary_1));
        }

        Weather weather = new Weather().getWeather();
        History history = new History().mockHistory();

        trendView.setData(weather, history);
//        trendView.setData(locationNow.weather, locationNow.history);
        trendView.setState(TrendItemView.DATA_TYPE_DAILY, false);
//        indexListView.setData(locationNow.weather);

        weatherContainer.setVisibility(View.VISIBLE);
        viewShowAnimator.start();
    }

    // init.

    private void initData() {
/*        readLocationList();
        readIntentData(getIntent());

        this.weatherHelper = new WeatherHelper();
        this.locationHelper = new LocationHelper(this);*/

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

            case R.id.container_weather_touchLayout:
            case R.id.activity_main_toolbar:
                skyView.onClickSky();
                break;

            case R.id.container_weather_location_iconButton:
//                IntentHelper.startAlertActivity(this, locationNow.weather);
                break;

            case R.id.container_weather_locationContainer:
//                IntentHelper.startManageActivityForResult(this);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_manage:
//                IntentHelper.startManageActivityForResult(this);
                break;

            case R.id.action_settings:
                skyView.setWeather();
                break;

            case R.id.action_about:
//                IntentHelper.startAboutActivity(this);
                break;
        }
        return true;
    }

    // on swipe listener(swipe switch layout).


    @Override
    public void swipeTakeEffect(int direction) {

/*        swipeSwitchLayout.setEnabled(false);
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).equals(locationNow)) {
                int position = direction == SwipeSwitchLayout.DIRECTION_LEFT ?
                        i + 1 : i - 1;
                if (position < 0) {
                    position = locationList.size() - 1;
                } else if (position > locationList.size() - 1) {
                    position = 0;
                }
                setLocationAndReset(locationList.get(position));
                return;
            }
        }
        setLocationAndReset(locationList.get(0));*/
    }

    // on refresh listener.

    @Override
    public void onRefresh() {
/*        locationHelper.cancel();
        weatherHelper.cancel();

        if (locationNow.isLocal()) {
            locationHelper.requestLocation(this, locationNow, this);
        } else {
            weatherHelper.requestWeather(this, locationNow, this);
        }*/
    }

    // on scroll changed listener.

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        skyView.setTranslationY((float) (-Math.min(1, 1.0 * scrollY / scrollTrigger)
                * skyView.getMeasuredHeight()));
        toolbar.setTranslationY((float) (-Math.min(1, 1.0 * scrollY / scrollTrigger)
                * toolbar.getMeasuredHeight()));
        toolbar.setAlpha((float) (1.0 - scrollY / 400.0));
        for (int i = 0; i < 3; i++) {
            titleTexts[i].setAlpha((float) (1.0 - scrollY / 660.0));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (scrollY > 815) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.lightPrimary_5));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
//
//        overviewTitle.setText(scrollTrigger + "\n"
//                + scrollY);
    }

    // handler container.

    @Override
    public void handleMessage(Message message) {
        /*switch (message.what) {
          *//*  case MESSAGE_WHAT_STARTUP_SERVICE:
                WidgetUtils.refreshWidgetInNewThread(this, locationList.get(0));
                NotificationUtils.refreshNotificationInNewThread(this, locationList.get(0));
                ServiceHelper.startupAllService(this);*//*
                break;
        }*/
    }
}
