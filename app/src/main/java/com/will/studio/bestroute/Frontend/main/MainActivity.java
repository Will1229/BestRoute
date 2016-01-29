package com.will.studio.bestroute.frontend.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.GoogleDirectionHelper;
import com.will.studio.bestroute.backend.RouteDataManager;
import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Receivers.DismissReceiver;
import com.will.studio.bestroute.frontend.main.Receivers.NaviReceiver;
import com.will.studio.bestroute.frontend.main.Receivers.NotificationReceiver;
import com.will.studio.bestroute.frontend.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RouteDataManager routeDataManager;
    private Activity currentActivity = null;
    private AlarmManager alarmMgr;
    private int currentItemIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_current_items);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent newItemIntent = new Intent(this, NewItemActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(newItemIntent, Constants.UPDATE_ITEM_REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String dirPath = getApplicationContext().getFilesDir().getAbsolutePath();
        routeDataManager = new RouteDataManager(dirPath);
        routeDataManager.restoreAllItemsFromDisc();

        refreshRouteItems();

        ListView listView = (ListView) findViewById(R.id.main_list);
        registerForContextMenu(listView);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0) {
                    return;
                }
                currentItemIdx = position;
                currentActivity.openContextMenu(parent);
            }

        };

        listView.setOnItemClickListener(listener);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item_floating_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        ArrayList<RouteItem> itemList = routeDataManager.getAllItems();
        final RouteItem routeItem = itemList.get(currentItemIdx);

        switch (item.getItemId()) {
            case R.id.delete_item:
                cancelAlarm(routeItem);
                routeDataManager.deleteItem(routeItem.getFilePath());
                routeDataManager.restoreAllItemsFromDisc();
                refreshRouteItems();
                return true;
            case R.id.edit_item:
                cancelAlarm(routeItem);
                Intent intent = new Intent(this, NewItemActivity.class);
                intent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
                startActivityForResult(intent, Constants.UPDATE_ITEM_REQUEST_CODE);
                return true;
            case R.id.back_item:
                return true;
            case R.id.call_google_map:
                getDirectionAndShow(routeItem);
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void cancelAlarm(RouteItem routeItem) {
        int requestCode = routeItem.getAlarmRequestCode();
        Intent intent = buildNotificationIntent(routeItem);
        PendingIntent pendingAlarmIntent =
                PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(getClass().getName(), "cancel alarm requestCode = " + requestCode);
        alarmMgr.cancel(pendingAlarmIntent);
    }

    private void scheduleAlarm(RouteItem routeItem) {
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

        int requestCode = routeItem.getAlarmRequestCode();
        Intent intent = buildNotificationIntent(routeItem);
        PendingIntent pendingAlarmIntent =
                PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
        Log.d(getClass().getName(), "create alarm requestCode = " + requestCode);
    }

    private void getDirectionAndShow(final RouteItem routeItem) {
        LatLng from = GoogleDirectionHelper.getLocationFromAddress(this, routeItem.getFrom());
        LatLng to = GoogleDirectionHelper.getLocationFromAddress(this, routeItem.getTo());
        if (from == null || to == null) {
            Toast.makeText(MainActivity.this, getText(R.string.invalid_address), Toast
                    .LENGTH_SHORT).show();
            return;
        }

        // TODO: make them settable in new item activity
        GoogleDirection
                .withServerKey("AIzaSyDPQ1GwAKKQZaxH1cmyVbx0FLDwKqKlJD8")
                .from(from)
                .to(to)
                .departureTime("now")
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction) {
                        String status = direction.getStatus();
                        if (status.equals(RequestResult.OK)) {
                            GoogleDirectionHelper.setDirection(direction);
                            final Intent mapViewIntent = new Intent(getApplicationContext(),
                                    MapViewActivity.class);
                            mapViewIntent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
                            startActivity(mapViewIntent);
                        } else {
                            Toast.makeText(MainActivity.this, getText(R.string.direction_nok),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(MainActivity.this, getText(R.string.direction_failure),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeDataManager.restoreAllItemsFromDisc();
        refreshRouteItems();
    }

    private void refreshRouteItems() {
        ArrayList<RouteItem> itemList = routeDataManager.getAllItems();
        ArrayList<String> itemHeadlineList = new ArrayList<>();

        for (RouteItem i : itemList
                ) {
            itemHeadlineList.add(" From " + i.getFrom()
                    + " to " + i.getTo()
                    + " at " + i.getTime());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                itemHeadlineList);
        ListView view = (ListView) findViewById(R.id.main_list);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete_all_plans) {
            //TODO: delete all alarms
            routeDataManager.deleteAllItems();
            refreshRouteItems();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addNewItem) {
            Intent newItemIntent = new Intent(this, NewItemActivity.class);
            startActivityForResult(newItemIntent, Constants.UPDATE_ITEM_REQUEST_CODE);
            return true;

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.UPDATE_ITEM_REQUEST_CODE) {
            if (resultCode == Constants.ACTIVITY_RESULT_OK) {
                RouteItem routeItem = (RouteItem) data.getSerializableExtra(Constants
                        .UPDATED_ROUTE_ITEM);
                new ScheduleAlarmsTask().execute(routeItem);
                Toast.makeText(MainActivity.this, getText(R.string.success_to_save_new_item) +
                        routeItem.getTime(), Toast.LENGTH_LONG).show();
            } else if (resultCode == Constants.ACTIVITY_RESULT_NOK) {
                Toast.makeText(MainActivity.this, getText(R.string.fail_to_save_new_item), Toast
                        .LENGTH_SHORT).show();
            } else if (resultCode == Constants.ACTIVITY_RESULT_CANCEL) {
                return;
            }
            refreshRouteItems();
        }
    }

    private Intent buildNotificationIntent(RouteItem routeItem) {

        //build notification builder
        String content = "From " + routeItem.getFrom() + " to " + routeItem.getTo();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_notifications)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(content)
                        .setTicker(getText(R.string.notification_ticker))
                        .setSound(alarmSound)
                        .setAutoCancel(true);

        //build map intent into notification
        Intent mapIntent = new Intent(getApplicationContext(), MapViewActivity.class);
        mapIntent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MapViewActivity.class);
        stackBuilder.addNextIntent(mapIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationBuilder.addAction(R.drawable.ic_navigation, getResources().getString(R.string
                .notification_navi_button), buildNaviAction(routeItem));
        notificationBuilder.addAction(R.drawable.ic_close, getResources().getString(R.string
                .notification_dismiss_button), buildDismissAction());

        // build notification into notification intent
        Notification notification = notificationBuilder.build();
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_ID, Constants.NOTIFICATION_ID_VALUE);
        intent.putExtra(Constants.NOTIFICATION_NAME, notification);
        intent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);

        return intent;
    }

    private PendingIntent buildDismissAction() {
        Intent dismissIntent = new Intent(this, DismissReceiver.class);
        dismissIntent.putExtra(Constants.NOTIFICATION_ID, Constants
                .NOTIFICATION_ID_VALUE);
        return PendingIntent.getBroadcast(getApplicationContext(), Constants
                        .NOTIFICATION_REQUEST_CODE, dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent buildNaviAction(RouteItem routeItem) {
        Intent naviIntent = new Intent(this, NaviReceiver.class);
        naviIntent.putExtra(Constants.NOTIFICATION_ID, Constants
                .NOTIFICATION_ID_VALUE);
        naviIntent.putExtra(Constants.EXTRA_NAME_ROUTE_ITEM, routeItem);
        return PendingIntent.getBroadcast(getApplicationContext(), Constants
                        .NOTIFICATION_REQUEST_CODE,
                naviIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private class ScheduleAlarmsTask extends AsyncTask<RouteItem, Integer, Void> {

        @Override
        protected Void doInBackground(RouteItem... routeItems) {
            for (RouteItem item : routeItems
                    ) {
                scheduleAlarm(item);
            }
            return null;
        }
    }
}
