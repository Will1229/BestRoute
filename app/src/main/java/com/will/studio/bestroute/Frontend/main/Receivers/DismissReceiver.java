package com.will.studio.bestroute.frontend.main.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.will.studio.bestroute.frontend.main.Constants;

/**
 * Created by egaozhi on 2016-01-25.
 * Project: BestRoute
 */
public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(Constants.NOTIFICATION_ID, 0);
        Log.d(getClass().getName(), "Dismiss notification with id = " + id);
        notificationManager.cancel(id);
    }
}
