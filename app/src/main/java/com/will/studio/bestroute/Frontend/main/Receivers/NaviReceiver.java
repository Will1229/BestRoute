package com.will.studio.bestroute.frontend.main.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Constants;

/**
 * Created by egaozhi on 2016-01-25.
 * Project: BestRoute
 */
public class NaviReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RouteItem routeItem = (RouteItem) intent.getSerializableExtra(Constants
                .EXTRA_NAME_ROUTE_ITEM);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + routeItem.getTo());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mapIntent);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(Constants.NOTIFICATION_ID, 0);
        notificationManager.cancel(id);
    }
}
