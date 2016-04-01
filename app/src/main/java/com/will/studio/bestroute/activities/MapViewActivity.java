package com.will.studio.bestroute.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.model.Bound;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.main.Constants;
import com.will.studio.bestroute.routeData.GoogleDirectionHelper;
import com.will.studio.bestroute.routeData.RouteItem;

import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RouteItem routeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        routeItem = (RouteItem) getIntent().getSerializableExtra(Constants.EXTRA_NAME_ROUTE_ITEM);
        if (routeItem == null) {
            Toast.makeText(getApplicationContext(), "routeItem is null", Toast.LENGTH_SHORT).show();
            return;
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id
                .map_view);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        MenuItem item = menu.findItem(R.id.map_traffic_switch_item);
        item.setActionView(R.layout.map_traffic_switch);
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Direction direction = GoogleDirectionHelper.getDirection();
        if (direction == null) {
            Toast.makeText(getApplicationContext(), getText(R.string.direction_failure), Toast
                    .LENGTH_SHORT).show();
            return;
        }
        if (!direction.getStatus().equals(RequestResult.OK)) {
            Toast.makeText(getApplicationContext(), getText(R.string.direction_nok), Toast
                    .LENGTH_SHORT).show();
            return;
        }

        Leg leg = direction.getRouteList().get(0).getLegList().get(0);
        String duration;
        if (leg.getDurationInTraffic() != null) {
            duration = leg.getDurationInTraffic().getText();
        } else {
            duration = "probably " + leg.getDuration().getText();
        }
        buildActionBar(duration, googleMap);

        List<Step> stepList = leg.getStepList();
        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(
                MapViewActivity.this, stepList, 5, Color.BLUE, 3, Color.BLUE);
        for (PolylineOptions polylineOption : polylineOptionList) {
            googleMap.addPolyline(polylineOption);
        }

        LatLng from = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation()
                .getLongitude());
        LatLng to = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation()
                .getLongitude());

        Route route = direction.getRouteList().get(0);
        Bound bound = route.getBound();
        final LatLngBounds bounds = new LatLngBounds(
                bound.getSouthwestCoordination().getCoordination(),
                bound.getNortheastCoordination().getCoordination());

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200), 1000,
                        null);
            }
        });

        googleMap.addMarker(new MarkerOptions()
                .position(from)
                .title("Start: " + routeItem.getFrom())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        googleMap.addMarker(new MarkerOptions()
                .position(to)
                .title("Destination: " + routeItem.getTo())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                .showInfoWindow();

        // TODO: make them settable in options
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        Boolean showTraffic = PreferenceManager.getDefaultSharedPreferences(this).getBoolean
                ("traffic_switch", true);
        googleMap.setTrafficEnabled(showTraffic);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);


    }

    private void buildActionBar(final String duration, final GoogleMap map) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        toolbar.setTitle(getApplication().getString(R.string.map_summary, duration));

        Menu menu = toolbar.getMenu();
        getMenuInflater().inflate(R.menu.map_menu, menu);
        MenuItem item = menu.findItem(R.id.map_traffic_switch_item);
        item.setActionView(R.layout.map_traffic_switch);

        SwitchCompat switchCompat = (SwitchCompat) item.getActionView().findViewById(R.id
                .map_traffic_switch);

        Boolean showTraffic = PreferenceManager.getDefaultSharedPreferences(this).getBoolean
                ("traffic_switch", true);
        switchCompat.setChecked(showTraffic);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    map.setTrafficEnabled(true);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string
                            .map_traffic_on_toast), Toast.LENGTH_SHORT).show();
                } else {
                    map.setTrafficEnabled(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string
                            .map_traffic_off_toast), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
