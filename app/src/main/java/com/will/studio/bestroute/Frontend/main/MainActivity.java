package com.will.studio.bestroute.Frontend.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.will.studio.bestroute.Backend.RouteDataManager;
import com.will.studio.bestroute.Backend.RouteDataManagerImpl;
import com.will.studio.bestroute.Backend.RouteItem;
import com.will.studio.bestroute.Frontend.NewItem.NewItemActivity;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.Frontend.settings.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_new_item);
        setSupportActionBar(toolbar);

        // TODO implement floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView textView = (TextView) findViewById(R.id.textView2);
        RouteDataManager routeDataManager = new RouteDataManagerImpl();
        String dir = getApplicationContext().getFilesDir().getAbsolutePath();
        ArrayList<RouteItem> itemList = routeDataManager.readAllData(dir);
        if (itemList.size() > 0) {
            textView.setText(itemList.get(0).toString());
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume!\n");
        super.onResume();
        TextView textView = (TextView) findViewById(R.id.textView2);
        RouteDataManager routeDataManager = new RouteDataManagerImpl();
        String dir = getApplicationContext().getFilesDir().getAbsolutePath();
        ArrayList<RouteItem> itemList = routeDataManager.readAllData(dir);
        if (itemList.size() > 0) {
            Log.d(TAG, "setText " + itemList.get(0).toString());
            textView.setText(itemList.get(0).toString());
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addNewItem) {
            Intent intent = new Intent(this, NewItemActivity.class);
            startActivity(intent);
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
}
