package com.will.studio.bestroute.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.will.studio.bestroute.routeData.RouteDataManager;
import com.will.studio.bestroute.routeData.RouteItem;
import com.will.studio.bestroute.main.RouteAlarmScheduler;

import java.util.ArrayList;

/**
 * Created by egaozhi on 2016-01-07.
 * Project: BestRoute
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            restoreAllAlarms(context);
        }
    }

    private void restoreAllAlarms(Context context) {
        RouteDataManager routeDataManager = new RouteDataManager(context.getFilesDir()
                .getAbsolutePath());
        routeDataManager.restoreAllItemsFromDisc();
        ArrayList<RouteItem> routeItemList = routeDataManager.getAllItems();
        RouteAlarmScheduler routeAlarmScheduler = new RouteAlarmScheduler(context);
        for (RouteItem item : routeItemList
                ) {
            if (item.isSwitchedOn()) {
                routeAlarmScheduler.scheduleAlarm(item);
            }
        }
    }
}
