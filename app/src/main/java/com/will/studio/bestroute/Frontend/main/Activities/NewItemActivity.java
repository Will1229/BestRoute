package com.will.studio.bestroute.frontend.main.Activities;

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

import com.google.android.gms.maps.model.LatLng;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.GoogleDirectionHelper;
import com.will.studio.bestroute.backend.RouteDataManager;
import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Constants;
import com.will.studio.bestroute.frontend.main.RouteNotificationBuilder;
import com.will.studio.bestroute.frontend.main.TimePickerFragment;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();
    private RouteItem existingRouteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.new_item_toolbar_title);
        setSupportActionBar(toolbar);

        existingRouteItem = (RouteItem) getIntent().getSerializableExtra(Constants
                .EXTRA_NAME_ROUTE_ITEM);
        if (existingRouteItem != null) {
            fillBlanks(existingRouteItem);
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
            Toast.makeText(getApplicationContext(), R.string.new_item_toast_fill_all_blanks,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RouteNotificationBuilder routeNotificationBuilder = new
                RouteNotificationBuilder(getApplicationContext());
        final NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_ID_VALUE,
                routeNotificationBuilder
                        .buildNotification(newItem));

    }

    public void onClickSaveButton(View view) {
        new SaveNewItemAndScheduleAlarmTask().execute();
    }

    public void onClickCancelButton(View view) {
        existingRouteItem = null;
        Intent returnIntent = new Intent();
        setResult(Constants.ACTIVITY_RESULT_CANCEL, returnIntent);
        finish();
    }

    private class SaveNewItemAndScheduleAlarmTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            RouteItem newItem = readFromAllText();
            if (newItem == null) {
                return false;
            }
            LatLng from = GoogleDirectionHelper.getLocationFromAddress(getApplicationContext(),
                    newItem.getFrom());
            LatLng to = GoogleDirectionHelper.getLocationFromAddress(getApplicationContext(),
                    newItem.getTo());

            if (from == null || to == null) {
                return false;
            }

            newItem.setFromLat(from.latitude);
            newItem.setFromLng(from.longitude);
            newItem.setToLat(to.latitude);
            newItem.setToLng(to.longitude);

            String dirPath = getApplicationContext().getFilesDir().getAbsolutePath();
            RouteDataManager routeDataManager = new RouteDataManager(dirPath);
            String filePath = null;
            if (existingRouteItem != null) { // edit existing item
                filePath = existingRouteItem.getFilePath();
            }
            boolean success = routeDataManager.saveItem(newItem, filePath);

            Intent returnIntent = new Intent();
            if (success) {
                returnIntent.putExtra(Constants.UPDATED_ROUTE_ITEM, newItem);
                setResult(Constants.ACTIVITY_RESULT_OK, returnIntent);
                return true;
            } else {
                setResult(Constants.ACTIVITY_RESULT_NOK, returnIntent);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), R.string.new_item_toast_fill_all_blanks,
                        Toast.LENGTH_SHORT).show();
            }
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