package com.will.studio.bestroute.Frontend.main;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.will.studio.bestroute.Backend.RouteDataManager;
import com.will.studio.bestroute.Backend.RouteDataManagerImpl;
import com.will.studio.bestroute.Backend.RouteItem;
import com.will.studio.bestroute.Frontend.NewItem.NewItemActivity;
import com.will.studio.bestroute.Frontend.settings.SettingsActivity;
import com.will.studio.bestroute.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ITEM_NAME = "current_item";

    private RouteDataManager routeDataManager;
    private String dir;
    private final int newItemRequestCode = 1;
    private Activity currentActivity = null;
    private int currentItemIdx = 0;

    public MainActivity() {
        routeDataManager = new RouteDataManagerImpl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_current_items);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent newItemIntent = new Intent(this, NewItemActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(newItemIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dir = getApplicationContext().getFilesDir().getAbsolutePath();
        routeDataManager.restoreAllItems(dir);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item_floating_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ArrayList<RouteItem> itemList = routeDataManager.getAllItems(dir);
        RouteItem routeItem = itemList.get(currentItemIdx);

        final Intent mapViewIntent = new Intent(this, MapViewActivity.class);

        switch (item.getItemId()) {
            case R.id.delete_item:
                routeItem.delete();
                routeDataManager.restoreAllItems(dir);
                refreshRouteItems();
                return true;
            case R.id.edit_item:
                break;
            case R.id.back_item:
                return true;
            case R.id.call_google_map:
                mapViewIntent.putExtra(ITEM_NAME, routeItem);
                startActivity(mapViewIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeDataManager.restoreAllItems(dir);
        refreshRouteItems();
    }

    private void refreshRouteItems() {
        ArrayList<RouteItem> itemList = routeDataManager.getAllItems(dir);
        ArrayList<String> itemNameList = new ArrayList<>();

        for (RouteItem i : itemList
                ) {
            itemNameList.add(i.getFrom());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                itemNameList);
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
            routeDataManager.deleteAllItems(dir);
            refreshRouteItems();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addNewItem) {
            Intent newItemIntent = new Intent(this, NewItemActivity.class);
            startActivityForResult(newItemIntent, newItemRequestCode);
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
        if (requestCode == newItemRequestCode) {
            if (resultCode == RESULT_OK) {
                refreshRouteItems();
            }
        }
    }


}
