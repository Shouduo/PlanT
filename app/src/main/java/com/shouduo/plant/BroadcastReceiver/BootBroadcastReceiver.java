package com.shouduo.plant.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shouduo.plant.PlanT;
import com.shouduo.plant.service.NotificationService;

/**
 * Created by 刘亨俊 on 17.2.21.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PlanT.getInstance().isSendNotification()) {
            Intent notificationService = new Intent(context, NotificationService.class);
            context.startService(notificationService);

            Log.d(TAG, "onReceive: ");
        }
    }
}
