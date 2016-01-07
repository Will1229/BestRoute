package com.will.studio.bestroute.frontend.main;

import android.app.AlarmManager;
import android.app.DialogFragment;
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

import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.RouteDataManager;
import com.will.studio.bestroute.backend.RouteDataManagerImpl;
import com.will.studio.bestroute.backend.RouteItem;

import java.util.Calendar;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.new_item_toolbar_title);
        setSupportActionBar(toolbar);
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
        finish();
    }

    private class SaveNewItemAndScheduleAlarmTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            RouteItem newItem = readFromAllText();
            if (newItem != null) {
                RouteDataManager routeDataManager = new RouteDataManagerImpl();
                String dir = getApplicationContext().getFilesDir().getAbsolutePath();
                boolean success = routeDataManager.saveItem(dir, newItem);
                if (success) {
                    setAlarm(newItem.getTime());

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(MainActivity.newItemResult, newItem.getTime());
                    setResult(RESULT_OK, returnIntent);

                    return true;
                }
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

        private void setAlarm(String time) {
            String[] times = time.split(":");
            int hour = Integer.parseInt(times[0]);
            int minute = Integer.parseInt(times[1]);
            AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            // Set the alarm to start at approximately 2:00 p.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            // setRepeating() lets you specify a precise custom interval--in this case,
            // 20 minutes.
            alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);

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
