package com.shouduo.plant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.model.Hourly;
import com.shouduo.plant.view.activity.BaseActivity;
import com.shouduo.plant.view.activity.MainActivity;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 刘亨俊 on 17.2.17.
 */

public class NotificationService extends Service {

    static Timer timer = null;
    static BaseActivity mainActivity = PlanT.getInstance().getMainActivity();
    long period = 1 * 1 * 30 * 1000;
    long delay = 0;
    static long muteUntilTime = System.currentTimeMillis();
    static long muteDuration = 1 * 1 * 60 * 1000;

    private final static int NOTIFICATION_SERVICE_ID = 1001;
    private static final String TAG = "NotificationService";

    private void sendNotification(int id, String tickerText, String contentTitle, String contentText) {

        NotificationManager notificationManager = (NotificationManager)
                NotificationService.this.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(NotificationService.this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent muteIntent = new Intent(this, MuteService.class);
        PendingIntent mutePendingIntent = PendingIntent.getService(this, 0, muteIntent, 0);

        if (id == 1) {
            builder.setSmallIcon(R.drawable.ic_humidity);
        } else if (id == 2) {
            builder.setSmallIcon(R.drawable.ic_brightness);
        } else if (id == 3) {
            builder.setSmallIcon(R.drawable.ic_temperature);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        builder.setContentIntent(contentIntent);
        builder.addAction(R.drawable.ic_notification_paused_white_24dp, "Mute for 8 hours", mutePendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setTicker(tickerText);
        builder.setContentText(contentText);
        builder.setContentTitle(contentTitle);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_MAX);

        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    @Override
    public void onCreate() {
        if (timer == null) {
            timer = new Timer();
        }

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //灰色保活
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(NOTIFICATION_SERVICE_ID, new Notification());
        } else {
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
                    Log.d(TAG, "run: " );
                    Hourly hourly = DataSupport.findLast(Hourly.class);
                    int humLimit = PlanT.getInstance().getHumLimit();
                    int brightLimit = PlanT.getInstance().getBrightLimit();
                    int tempLimit = PlanT.getInstance().getTempLimit();

                    if (hourly.hum < 50) {
                        sendNotification(1, "Humidity is low",
                                "The Soil Humidity is lower than " + humLimit,
                                "Water your plant!");
                    }

                    if (hourly.bright < brightLimit) {
                        sendNotification(2, "Brightness is low",
                                "The Environment Brightness is lower than " + brightLimit,
                                "Move your plant under the sunshine.");
                    }

                    if (hourly.temp < tempLimit) {
                        sendNotification(3, "Temperature is low",
                                "The Environment Temperature is lower than " + tempLimit,
                                "Move your plant inside the house");
                    }
                }
            }
        }, delay, period);

        return super.onStartCommand(intent, flags, startId);
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
        if (fromTimeHour > toTimeHour) {       //from today to tomorrow
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
            Log.d(TAG, "isDoNotDisturb: true" );
            return true;
        } else {
            return false;
        }
    }

    private boolean isMute() {
//        if (System.currentTimeMillis() < muteUntilTime) {
//            return true;
//        }
        return System.currentTimeMillis() < muteUntilTime;
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
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
            notificationManager.cancelAll();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}
