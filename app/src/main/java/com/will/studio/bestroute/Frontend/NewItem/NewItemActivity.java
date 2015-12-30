package com.will.studio.bestroute.Frontend.NewItem;

import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.will.studio.bestroute.Backend.RouteDataManager;
import com.will.studio.bestroute.Backend.RouteDataManagerImpl;
import com.will.studio.bestroute.Backend.RouteItem;
import com.will.studio.bestroute.Frontend.main.MainActivity;
import com.will.studio.bestroute.Frontend.main.MapViewActivity;
import com.will.studio.bestroute.R;

public class NewItemActivity extends AppCompatActivity {

    public static final String TAG = NewItemActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        toolbar.setTitle(R.string.new_item_toolbar_title);
        setSupportActionBar(toolbar);

        final Intent mapViewIntent = new Intent(this, MapViewActivity.class);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteItem newItem;
                switch (v.getId()) {
                    case R.id.save_button:
                        newItem = readFromAllText();
                        if (newItem != null) {
                            RouteDataManager routeDataManager = new RouteDataManagerImpl();
                            String dir = getApplicationContext().getFilesDir().getAbsolutePath();
                            boolean success = routeDataManager.saveItem(dir, newItem);
                            if (success) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        } else {
                            Toast.makeText(NewItemActivity.this, R.string.empty_new_item, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.cancel_button:
                        finish();
                        break;
                    case R.id.test_button:
                        newItem = readFromAllText();
                        if (newItem != null) {
                            mapViewIntent.putExtra(MainActivity.ITEM_NAME, newItem);
                            startActivity(mapViewIntent);
                        } else {
                            Toast.makeText(NewItemActivity.this, R.string.empty_new_item, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        final Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(onClickListener);

        final Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(onClickListener);

        final Button testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(onClickListener);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    public void previewNotification(View v) {

        RouteItem newItem = readFromAllText();
        if (newItem == null) {
            Toast.makeText(NewItemActivity.this, R.string.empty_new_item, Toast.LENGTH_SHORT).show();
            return;
        }

        String title = "Your route plan is updated";
        String content = "From " + newItem.getFrom() + " to " + newItem.getTo();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle(title)
                        .setContentText(content)
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


    private RouteItem readFromAllText() {

        EditText editText = (EditText) findViewById(R.id.New_Item_From);
        String from = editText.getText().toString();
        editText = (EditText) findViewById(R.id.New_Item_To);
        String to = editText.getText().toString();
        String time = ((TextView) findViewById(R.id.New_Item_Time)).getText().toString();

        if (from.length() == 0 || to.length() == 0 || time.length() == 0) {
            return null;
        }

        RouteItem item = new RouteItem(from, to, time);

        Log.d(TAG, "readFromAllText return: " + item.toString());

        return item;
    }


}
