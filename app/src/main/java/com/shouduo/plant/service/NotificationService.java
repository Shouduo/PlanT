package com.shouduo.plant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.R;
import com.shouduo.plant.view.activity.BaseActivity;
import com.shouduo.plant.view.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 刘亨俊 on 17.2.17.
 */

public class NotificationService extends Service {

    static Timer timer = null;
    static BaseActivity mainActivity = PlanT.getInstance().getMainActivity();
//    private NotificationManager notificationManager = (NotificationManager)
//            mainActivity.getSystemService(NOTIFICATION_SERVICE);
    private static final String TAG = "NotificationService";

    public static void cleanAllNotification() {
        NotificationManager notificationManager = (NotificationManager)
                mainActivity.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void addNotification(int delayTime, String tickerText,
                                       String contetnTitle, String contentText) {
        Intent intent = new Intent(mainActivity, NotificationService.class);
        intent.putExtra("delayTime", delayTime);
        intent.putExtra("tickerText", tickerText);
        intent.putExtra("contentTitle", contetnTitle);
        intent.putExtra("contentText", contentText);
        mainActivity.startService(intent);

        Log.d(TAG, "addNotification: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        long period = 1 * 1 * 60 * 1000;
        int delay = intent.getIntExtra("delayTime", 0);
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NotificationManager notificationManager = (NotificationManager)
                        NotificationService.this.getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(NotificationService.this);
                Intent notificationIntent = new Intent(NotificationService.this, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(NotificationService.this, 0,
                        notificationIntent, 0);
                builder.setContentIntent(contentIntent);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                builder.setTicker(intent.getStringExtra("tickerText"));
                builder.setContentText(intent.getStringExtra("contentText"));
                builder.setContentTitle(intent.getStringExtra("contentTitle"));
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setPriority(Notification.PRIORITY_MAX);
                Notification notification = builder.build();
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }
        }, delay, period);

        Log.d(TAG, "onStartCommand: ");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
