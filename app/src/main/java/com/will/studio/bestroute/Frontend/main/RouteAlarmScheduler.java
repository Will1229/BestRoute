package com.will.studio.bestroute.frontend.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Receivers.NotificationReceiver;

import java.util.Calendar;

/**
 * Created by egaozhi on 2016-01-29.
 * Project: BestRoute
 */
public class RouteAlarmScheduler {

    private Context context;
    private AlarmManager alarmMgr;

    public RouteAlarmScheduler(Context context) {
        this.context = context;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void cancelAlarm(RouteItem routeItem) {
        PendingIntent pendingAlarmIntent = getPendingIntent(routeItem);
        Log.d(getClass().getName(), "cancel alarm requestCode = " + routeItem.getAlarmRequestCode
                ());
        alarmMgr.cancel(pendingAlarmIntent);
    }

    public void scheduleAlarm(RouteItem routeItem) {
        String[] times = routeItem.getTime().split(":");
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // skip immediate alarm in case user sets a time in the past.
        long alarmTime = calendar.getTimeInMillis();
        if (alarmTime < System.currentTimeMillis()) {
            calendar.setTimeInMillis(alarmTime + AlarmManager.INTERVAL_DAY);
        }

        PendingIntent pendingAlarmIntent = getPendingIntent(routeItem);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
        Log.d(getClass().getName(), "create alarm requestCode = " + routeItem.getAlarmRequestCode
                ());
    }

    private PendingIntent getPendingIntent(RouteItem routeItem) {
        int requestCode = routeItem.getAlarmRequestCode();
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
        intent.putExtra(Constants.NOTIFICATION_ID, Constants.NOTIFICATION_ID_VALUE);
        return PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
