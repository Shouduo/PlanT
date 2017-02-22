package com.shouduo.plant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.model.Data;
import com.shouduo.plant.model.Hourly;
import com.shouduo.plant.view.activity.BaseActivity;
import com.shouduo.plant.view.activity.MainActivity;
import com.shouduo.plant.view.widget.SafeHandler;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 刘亨俊 on 17.2.17.
 */

public class NotificationService extends Service implements SafeHandler.HandlerContainer {

    private SafeHandler<NotificationService> handler;

    static Timer timer = null;
    static BaseActivity mainActivity = PlanT.getInstance().getMainActivity();
    private final static long period = 1 * 1 * 30 * 1000;
    private final static long delay = 0;
    private final static long muteDuration = 1 * 1 * 60 * 1000;
    private static long muteUntilTime = System.currentTimeMillis();

    private final static int NOTIFICATION_SERVICE_ID = 1001;
    private final static int NOTIFICATION_ID = 2001;
    private static final String TAG = "NotificationService";

    @Override
    public void onCreate() {
        if (timer == null) {
            timer = new Timer();
        }
        if (handler == null) {
            handler = new SafeHandler<>(NotificationService.this);
        }

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //灰色保活
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(NOTIFICATION_SERVICE_ID, new Notification());
        } else {    //(sdk25以上已失效)
            Intent innerIntent = new Intent(this, InnerService.class);
            startService(innerIntent);
            startForeground(NOTIFICATION_SERVICE_ID, new Notification());
        }

        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isDoNotDisturb() && !isMute()) {

//                    Log.d(TAG, "run: ");

                    Data data = new Data(handler);
                    data.refreshData();
                }
            }
        }, delay, period);

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("deprecation")
    private void sendNotification(int id, String tickerText, String contentTitle, String contentText) {

        NotificationManager notificationManager = (NotificationManager)
                NotificationService.this.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(NotificationService.this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent muteIntent = new Intent(this, MuteService.class);
        PendingIntent mutePendingIntent = PendingIntent.getService(this, 0, muteIntent, 0);


        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(contentIntent);
        builder.addAction(R.drawable.ic_notification_paused_white_24dp, "Mute for 8 hours", mutePendingIntent);
        builder.setTicker(tickerText);
        builder.setContentText(contentText);
        builder.setContentTitle(contentTitle);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setStyle(new Notification.BigTextStyle().bigText(contentText));

        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    private boolean isDoNotDisturb() {
        int fromTimeHour = PlanT.getInstance().getFromTimeHour();
        int fromTimeMinute = PlanT.getInstance().getFromTimeMinute();
        int toTimeHour = PlanT.getInstance().getToTimeHour();
        int toTimeMinute = PlanT.getInstance().getToTimeMinute();

//        Log.d(TAG, "fromtime: " + fromTimeHour + fromTimeMinute);
//        Log.d(TAG, "totime: " + toTimeHour + toTimeMinute);

        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(new Date(System.currentTimeMillis()));
        fromCalendar.set(Calendar.HOUR_OF_DAY, fromTimeHour);
        fromCalendar.set(Calendar.MINUTE, fromTimeMinute);

        Calendar toCalendar = Calendar.getInstance();
        if (fromTimeHour > toTimeHour) {       //跨越两天
            toCalendar.setTime(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        } else {
            toCalendar.setTime(new Date(System.currentTimeMillis()));
        }
        toCalendar.set(Calendar.HOUR_OF_DAY, toTimeHour);
        toCalendar.set(Calendar.MINUTE, toTimeMinute);

//        Log.d(TAG, "from: " + fromCalendar.getTimeInMillis());
//        Log.d(TAG, "to: " + toCalendar.getTimeInMillis());
//        Log.d(TAG, "now: " + System.currentTimeMillis());

        if (System.currentTimeMillis() > fromCalendar.getTimeInMillis() &&
                System.currentTimeMillis() < toCalendar.getTimeInMillis()) {
//            Log.d(TAG, "isDoNotDisturb: true");
            return true;
        } else {
            return false;
        }
    }

    private boolean isMute() {
        return System.currentTimeMillis() < muteUntilTime;
    }

    @Override
    public void handleMessage(Message message) {

//        Log.d(TAG, "handleMessage: ");

        switch (message.what) {
            case Data.SERVER_DOWN:
                break;

            case Data.SERVER_GOOD:
                Hourly hourly = DataSupport.findLast(Hourly.class);
                int humLimit = PlanT.getInstance().getHumLimit();
                int brightLimit = PlanT.getInstance().getBrightLimit();
                int tempLimit = PlanT.getInstance().getTempLimit();

                StringBuilder contentText = new StringBuilder();

                if (hourly.hum < humLimit) {
                    contentText.append("The soil humidity is " + hourly.hum + " %");
                }

                if (hourly.bright < brightLimit) {
                    if (contentText.length() != 0) {
                        contentText.append("\n");
                    }
                    contentText.append("The environment brightness is " + hourly.bright + " lux");
                }

                if (hourly.temp < tempLimit) {
                    if (contentText.length() != 0) {
                        contentText.append("\n");
                    }
                    contentText.append("The environment temperature is " + hourly.temp + " °C");
                }

                if (contentText.length() != 0) {
                    sendNotification(NOTIFICATION_ID, "PlanT Alerts", "PlanT Alerts", contentText.toString());
                }
                break;
        }


    }

    //灰色保活内部Service
    public static class InnerService extends Service {
        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(NOTIFICATION_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    //通知按钮Service
    public static class MuteService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            muteUntilTime = System.currentTimeMillis() + muteDuration;

            NotificationManager notificationManager = (NotificationManager)
                    mainActivity.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}
