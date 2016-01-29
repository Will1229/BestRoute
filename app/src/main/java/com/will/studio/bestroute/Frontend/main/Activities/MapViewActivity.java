package com.will.studio.bestroute.frontend.main.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.will.studio.bestroute.R;
import com.will.studio.bestroute.backend.GoogleDirectionHelper;
import com.will.studio.bestroute.backend.RouteItem;
import com.will.studio.bestroute.frontend.main.Constants;

import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private RouteItem routeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // move to common definitions.
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

        Route route = direction.getRouteList().get(0);
        TextView view = (TextView) findViewById(R.id.map_view_text);

        Leg leg = direction.getRouteList().get(0).getLegList().get(0);

        String duration;
        String summary = "you will arrive" + routeItem.getTo() + "in ";

        if (leg.getDurationInTraffic() != null) {
            duration = leg.getDurationInTraffic().getText();
            summary += duration;
        } else {
            duration = leg.getDuration().getText();
            summary += duration + "probably";
        }

        view.setText(summary);

        List<Step> stepList = leg.getStepList();
        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(
                MapViewActivity.this, stepList, 5, Color.GREEN, 3, Color.BLUE);
        for (PolylineOptions polylineOption : polylineOptionList) {
            googleMap.addPolyline(polylineOption);
        }

        LatLng from = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation()
                .getLongitude());
        LatLng to = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation()
                .getLongitude());

        Bound bound = route.getBound();
        final LatLngBounds bounds = new LatLngBounds(
                bound.getSouthwestCoordination().getCoordination(),
                bound.getNortheastCoordination().getCoordination());

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            }
        });

        // TODO: add icon
        googleMap.addMarker(new MarkerOptions().position(from).title(routeItem.getFrom()));
        googleMap.addMarker(new MarkerOptions().position(to).title(routeItem.getTo()));

        // TODO: make them settable in options
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMyLocationEnabled(true);

    }
}
