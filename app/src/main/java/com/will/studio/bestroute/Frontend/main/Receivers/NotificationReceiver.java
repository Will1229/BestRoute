package com.will.studio.bestroute.frontend.main.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;
import com.will.studio.bestroute.backend.GoogleDirectionHelper;
import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Constants;

/**
 * Created by egaozhi on 2016-01-08.
 * Project: BestRoute
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final NotificationManager notificationManager = (NotificationManager) context
                .getSystemService
                        (Context.NOTIFICATION_SERVICE);
        final int id = intent.getIntExtra(Constants.NOTIFICATION_ID, 0);
        final Notification notification = intent.getParcelableExtra(Constants.NOTIFICATION_NAME);

        Log.d(getClass().getName(), "Received a broadcast with id: " + id);

        RouteItem routeItem = (RouteItem) intent.getSerializableExtra(Constants
                .EXTRA_NAME_ROUTE_ITEM);
        if (routeItem == null) {
            Log.d(getClass().getName(), "routeItem is null, return");
            return;
        }

        LatLng from = new LatLng(routeItem.getFromLat(), routeItem.getFromLng());
        LatLng to = new LatLng(routeItem.getToLat(), routeItem.getToLng());

        GoogleDirection
                .withServerKey("AIzaSyDPQ1GwAKKQZaxH1cmyVbx0FLDwKqKlJD8")
                .from(from)
                .to(to)
                .transitMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            Log.d(getClass().getName(), "onDirectionSuccess OK");
                            GoogleDirectionHelper.setDirection(direction);
                            notificationManager.notify(id, notification);
                        } else {
                            Log.d(getClass().getName(), "onDirectionSuccess not OK");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.d(getClass().getName(), "onDirectionFailure: " + t.getMessage());
                    }
                });

    }
}
