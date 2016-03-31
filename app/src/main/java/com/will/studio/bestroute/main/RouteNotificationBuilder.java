package com.will.studio.bestroute.main;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.routeData.RouteItem;
import com.will.studio.bestroute.activities.MapViewActivity;
import com.will.studio.bestroute.receivers.DismissReceiver;
import com.will.studio.bestroute.receivers.NaviReceiver;

/**
 * Created by egaozhi on 2016-01-29.
 * Project: BestRoute
 */
public class RouteNotificationBuilder {

    Context context;

    public RouteNotificationBuilder(Context context) {
        this.context = context;
    }

    public Notification buildNotification(RouteItem routeItem) {

        //build notification builder
        Direction direction = routeItem.getDirection();
        if (direction == null) {
            return null;
        }

        Leg leg = direction.getRouteList().get(0).getLegList().get(0);

        String duration;
        boolean withTraffic = false;
        if (leg.getDurationInTraffic() != null) {
            duration = leg.getDurationInTraffic().getText();
            withTraffic = true;
        } else {
            duration = leg.getDuration().getText();
        }
        String title = duration + " " + context.getText(R.string.notification_title) + " " +
                routeItem.getTo();
        if (!withTraffic) {
            title += " (traffic info unavailable)";
        }
        String ticker = context.getText(R.string.notification_ticker) + " " + title;

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_directions_car)
                        .setTicker(ticker)
                        .setSound(alarmSound)
                        .setAutoCancel(false);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] contents = new String[]{
                "From " + routeItem.getFrom(),
                "Current best route is " + leg.getDistance().getText(),
                "Click to show the route on map"
        };
        inboxStyle.setBigContentTitle(title);
        for (String line : contents) {
            inboxStyle.addLine(line);
        }
        notificationBuilder.setStyle(inboxStyle);

        //build map_menu intent into notification
        Intent mapIntent = new Intent(context, MapViewActivity.class);
        mapIntent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MapViewActivity.class);
        stackBuilder.addNextIntent(mapIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationBuilder.addAction(R.drawable.ic_navigation, context.getResources().getString
                (R.string
                        .notification_navi_button), buildNaviAction(routeItem));
        notificationBuilder.addAction(R.drawable.ic_close, context.getResources().getString(R.string
                .notification_dismiss_button), buildDismissAction());

        return notificationBuilder.build();
    }

    private PendingIntent buildDismissAction() {
        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        dismissIntent.putExtra(Constants.NOTIFICATION_ID, Constants
                .NOTIFICATION_ID_VALUE);
        return PendingIntent.getBroadcast(context, Constants
                        .NOTIFICATION_REQUEST_CODE, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent buildNaviAction(RouteItem routeItem) {
        Intent naviIntent = new Intent(context, NaviReceiver.class);
        naviIntent.putExtra(Constants.NOTIFICATION_ID, Constants
                .NOTIFICATION_ID_VALUE);
        naviIntent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
        return PendingIntent.getBroadcast(context, Constants
                        .NOTIFICATION_REQUEST_CODE,
                naviIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
