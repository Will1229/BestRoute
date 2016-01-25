package com.will.studio.bestroute.frontend.main.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.will.studio.bestroute.frontend.main.Constants;

/**
 * Created by egaozhi on 2016-01-08.
 * Project: BestRoute
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(Constants.NOTIFICATION_ID, 0);
        Notification notification = intent.getParcelableExtra(Constants.NOTIFICATION_NAME);
        notificationManager.notify(id, notification);
        Log.d(getClass().getName(), "Received a broadcast with id: " + id);

//        RouteItem routeItem = (RouteItem) intent.getSerializableExtra(ROUTE_ITEM);
//
//        LatLng from = new LatLng(routeItem.getFromLat(), routeItem.getFromLng());
//        LatLng to = new LatLng(routeItem.getToLat(), routeItem.getToLng());
//
//        GoogleDirection
//                .withServerKey("AIzaSyDPQ1GwAKKQZaxH1cmyVbx0FLDwKqKlJD8")
//                .from(from)
//                .to(to)
//                .transitMode(TransportMode.DRIVING)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction) {
//                        String status = direction.getStatus();
//                        if (status.equals(RequestResult.OK)) {
//                            GoogleDirectionHelper.setDirection(direction);
//
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//                    }
//                });


    }
}
