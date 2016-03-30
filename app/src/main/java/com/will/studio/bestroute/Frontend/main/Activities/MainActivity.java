package com.will.studio.bestroute.frontend.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.will.studio.bestroute.frontend.main.Constants;
import com.will.studio.bestroute.frontend.main.RouteAlarmScheduler;
import com.will.studio.bestroute.frontend.main.RouteItemListAdapter;
import com.will.studio.bestroute.frontend.settings.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RouteDataManager routeDataManager;
    private RouteAlarmScheduler routeAlarmScheduler;
    private ProgressBar progressBar;
    private int currentItemIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeAlarmScheduler = new RouteAlarmScheduler(getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_current_items);
        toolbar.setTitle(R.string.main_activity_title);
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

        ListView listView = (ListView) findViewById(R.id.main_item_list);
        registerForContextMenu(listView);

        final Activity currentActivity = this;
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long
                    id) {
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
    protected void onResume() {
        super.onResume();
        progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
        progressBar.setVisibility(View.GONE);

        routeDataManager.restoreAllItemsFromDisc();
        refreshRouteItems();
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
                routeAlarmScheduler.cancelAlarm(routeItem);
                routeDataManager.deleteItem(routeItem);
                routeDataManager.restoreAllItemsFromDisc();
                refreshRouteItems();
                return true;
            case R.id.edit_item:
                routeAlarmScheduler.cancelAlarm(routeItem);
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

    private void getDirectionAndShow(final RouteItem routeItem) {
        progressBar.setVisibility(View.VISIBLE);

        LatLng from = new LatLng(routeItem.getFromLat(), routeItem.getFromLng());
        LatLng to = new LatLng(routeItem.getToLat(), routeItem.getToLng());
        // TODO: make them settable in new item activity
        GoogleDirection
                .withServerKey(Constants.APP_KEY)
                .from(from)
                .to(to)
                .departureTime("now")
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
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
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void refreshRouteItems() {
        ArrayList<RouteItem> itemList = routeDataManager.getAllItems();
        RouteItemListAdapter adapter = new RouteItemListAdapter(this, itemList);
        ListView view = (ListView) findViewById(R.id.main_item_list);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        TextView textView = (TextView) findViewById(R.id.main_top_text);
        if (itemList.isEmpty()) {
            textView.setText(R.string.main_top_text_title_empty);
        } else {
            textView.setText(R.string.main_top_text_title);
        }
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
            ArrayList<RouteItem> itemList = routeDataManager.getAllItems();
            for (RouteItem i : itemList
                    ) {
                routeAlarmScheduler.cancelAlarm(i);
            }
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

    private class ScheduleAlarmsTask extends AsyncTask<RouteItem, Integer, Void> {

        @Override
        protected Void doInBackground(RouteItem... routeItems) {
            for (RouteItem item : routeItems
                    ) {
                routeAlarmScheduler.scheduleAlarm(item);
            }
            return null;
        }
    }
}
