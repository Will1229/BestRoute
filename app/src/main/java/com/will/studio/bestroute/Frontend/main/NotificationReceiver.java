package com.will.studio.bestroute.frontend.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by egaozhi on 2016-01-08.
 */
public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Log.d(getClass().getName(), "Received a broadcast with action: " + action);

        if (action == null) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getIntExtra(CommonDefinitions.NOTIFICATION_ID, 0);
        Notification notification = intent.getParcelableExtra(CommonDefinitions.NOTIFICATION_NAME);

        switch (action) {
            case CommonDefinitions.ROUTE_ALARM_ACTION: {
                notificationManager.notify(id, notification);
                break;
            }
            case CommonDefinitions.NAVI_ACTION:

                break;
            case CommonDefinitions.DISMISS_ACTION: {
                Log.d(getClass().getName(), "In dismiss action!");
                notificationManager.cancel(id);
                break;
            }
        }

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
