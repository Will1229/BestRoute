package com.will.studio.bestroute.frontend.main;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.GoogleDirectionHelper;
import com.will.studio.bestroute.backend.RouteDataManager;
import com.will.studio.bestroute.backend.RouteDataManagerImpl;
import com.will.studio.bestroute.backend.RouteItem;

import java.util.Calendar;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();
    private RouteItem routeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.new_item_toolbar_title);
        setSupportActionBar(toolbar);

        routeItem = (RouteItem) getIntent().getSerializableExtra(CommonDefinitions.EXTRA_NAME_ROUTE_ITEM);
        if (routeItem != null) {
            fillBlanks(routeItem);
        }

    }

    private void fillBlanks(RouteItem routeItem) {
        EditText editText = (EditText) findViewById(R.id.new_item_from);
        editText.setText(routeItem.getFrom());
        editText = (EditText) findViewById(R.id.new_item_to);
        editText.setText(routeItem.getTo());
        TextView textView = (TextView) findViewById(R.id.new_item_time);
        textView.setText(routeItem.getTime());
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public void onClickPreviewNotificationButton(View v) {

        RouteItem newItem = readFromAllText();
        if (newItem == null) {
            Toast.makeText(getApplicationContext(), R.string.new_item_toast_fill_all_blanks, Toast.LENGTH_SHORT).show();
            return;
        }

        String content = "From " + newItem.getFrom() + " to " + newItem.getTo();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(content)
                        .setTicker(getText(R.string.notification_ticker))
                        .setSound(alarmSound);

        Intent resultIntent = new Intent(this, MapViewActivity.class);
        resultIntent.putExtra(MainActivity.ITEM_NAME, newItem);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapViewActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

    }


    public void onClickSaveButton(View view) {
        new SaveNewItemAndScheduleAlarmTask().execute();
    }

    public void onClickCancelButton(View view) {
        routeItem = null;
        Intent returnIntent = new Intent();
        setResult(CommonDefinitions.ACTIVITY_RESULT_CANCEL, returnIntent);
        finish();
    }

    private class SaveNewItemAndScheduleAlarmTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            RouteItem newItem = readFromAllText();
            if (newItem == null) {
                return false;
            }

            LatLng from = GoogleDirectionHelper.getLocationFromAddress(getApplicationContext(), newItem.getFrom());
            LatLng to = GoogleDirectionHelper.getLocationFromAddress(getApplicationContext(), newItem.getTo());

            if (from == null || to == null) {
                return false;
            }

            newItem.setFromLat(from.latitude);
            newItem.setFromLng(from.longitude);
            newItem.setToLat(to.latitude);
            newItem.setToLng(to.longitude);

            RouteDataManager routeDataManager = new RouteDataManagerImpl();
            String dir = getApplicationContext().getFilesDir().getAbsolutePath();
            String filePath = null;
            if (routeItem != null) { // edit existing item
                filePath = routeItem.getFilePath();
            }
            boolean success = routeDataManager.saveItem(dir, newItem, filePath);

            Intent returnIntent = new Intent();
            if (success) {
                scheduleNotification(newItem);
                returnIntent.putExtra(CommonDefinitions.updateItemResult, newItem.getTime());
                setResult(CommonDefinitions.ACTIVITY_RESULT_OK, returnIntent);
                return true;
            } else {
                setResult(CommonDefinitions.ACTIVITY_RESULT_NOK, returnIntent);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.new_item_toast_fill_all_blanks, Toast.LENGTH_SHORT).show();
            }
        }

        private void scheduleNotification(RouteItem routeItem) {

            //build notification builder
            String content = "From " + routeItem.getFrom() + " to " + routeItem.getTo();
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.cast_ic_notification_0)
                            .setContentTitle(getText(R.string.notification_title))
                            .setContentText(content)
                            .setTicker(getText(R.string.notification_ticker))
                            .setSound(alarmSound)
                            .setAutoCancel(true);

            addNaviAction(notificationBuilder);
            addDismissAction(notificationBuilder);

            //build map intent into notification
            Intent mapIntent = new Intent(getApplicationContext(), MapViewActivity.class);
            mapIntent.putExtra(MainActivity.ITEM_NAME, routeItem);
            mapIntent.setAction(CommonDefinitions.MAP_VIEW_ACTION);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(MapViewActivity.class);
            stackBuilder.addNextIntent(mapIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            notificationBuilder.setContentIntent(resultPendingIntent);

            // build notification into notification intent
            Notification notification = notificationBuilder.build();
            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra(CommonDefinitions.NOTIFICATION_ID, CommonDefinitions.NOTIFICATION_ID_VALUE);
            intent.putExtra(CommonDefinitions.NOTIFICATION_NAME, notification);
            intent.setAction(CommonDefinitions.ROUTE_ALARM_ACTION);
            PendingIntent pendingAlarmIntent =
                    PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the alarm
            scheduleAlarm(routeItem, pendingAlarmIntent);
        }

        private void addNaviAction(NotificationCompat.Builder notificationBuilder) {
            Intent naviIntent = new Intent();
            naviIntent.setAction(CommonDefinitions.NAVI_ACTION);
            PendingIntent pendingNaviIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, naviIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.addAction(0, getResources().getString(R.string.notification_navi_button), pendingNaviIntent);
        }

        private void addDismissAction(NotificationCompat.Builder notificationBuilder) {
            Intent dismissIntent = new Intent();
            dismissIntent.setAction(CommonDefinitions.DISMISS_ACTION);
            PendingIntent pendingDismissIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.addAction(0, getResources().getString(R.string.notification_dismiss_button), pendingDismissIntent);
        }

        private void scheduleAlarm(RouteItem routeItem, PendingIntent pendingIntent) {
            String[] times = routeItem.getTime().split(":");
            int hour = Integer.parseInt(times[0]);
            int minute = Integer.parseInt(times[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            long alarmTime = calendar.getTimeInMillis();
            if (alarmTime < System.currentTimeMillis()) {
                calendar.setTimeInMillis(alarmTime + AlarmManager.INTERVAL_DAY);
            }

            // schedule notification intent
            AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }

    private RouteItem readFromAllText() {

        EditText editText = (EditText) findViewById(R.id.new_item_from);
        String from = editText.getText().toString();
        editText = (EditText) findViewById(R.id.new_item_to);
        String to = editText.getText().toString();
        String time = ((TextView) findViewById(R.id.new_item_time)).getText().toString();

        String nullTime = this.getString(R.string.new_item_null_time);

        if (from.length() == 0 || to.length() == 0 || time.equals(nullTime)) {
            return null;
        }

        RouteItem item = new RouteItem(from, to, time);

        Log.d(TAG, "readFromAllText return: " + item.toString());

        return item;
    }


}
