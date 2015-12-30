package com.will.studio.bestroute.Frontend.main;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Bound;
import com.akexorcist.googledirection.model.Direction;
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
import com.will.studio.bestroute.Backend.RouteItem;
import com.will.studio.bestroute.R;

import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private RouteItem routeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        routeItem = (RouteItem) getIntent().getSerializableExtra(MainActivity.ITEM_NAME);
        if (routeItem == null) {
            Toast.makeText(getApplicationContext(), "routeItem is null", Toast.LENGTH_SHORT).show();
            return;
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final GoogleMap map = googleMap;

        DirectionCallback directionCallback = new DirectionCallback() {
            @Override
            public void onDirectionSuccess(Direction direction) {

                String status = direction.getStatus();
                if (status.equals(RequestResult.OK)) {

                    Route route = direction.getRouteList().get(0);
                    TextView view = (TextView) findViewById(R.id.map_view_text);
                    String summary = new StringBuilder()
                            .append("If you leave now you will arrive destination in ")
                            .append(route.getLegList().get(0).getDuration().getText())
//                            .append("\nIf you leave at: ")
//                            .append(route.getLegList().get(0).getDepartureTime().getText())
//                            .append(", you will arrive at: ")
//                            .append(route.getLegList().get(0).getArrivalTime().getText())
                            .toString();
                    view.setText(summary);

                    List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(
                            MapViewActivity.this, stepList, 5, Color.GREEN, 3, Color.BLUE);
                    for (PolylineOptions polylineOption : polylineOptionList) {
                        map.addPolyline(polylineOption);
                    }

                    Bound bound = route.getBound();
                    LatLngBounds bounds = new LatLngBounds(
                            bound.getSouthwestCoordination().getCoordination(),
                            bound.getNortheastCoordination().getCoordination());
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10));

                } else {
                    Toast.makeText(getApplicationContext(), "Direction status is not ok: " + status, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDirectionFailure(Throwable t) {
                Toast.makeText(getApplicationContext(), "Get direction failed", Toast.LENGTH_SHORT).show();
            }
        };

        LatLng from = getLocationFromAddress(getApplicationContext(), routeItem.getFrom());
        LatLng to = getLocationFromAddress(getApplicationContext(), routeItem.getTo());

        if (from == null || to == null) {
            Toast.makeText(getApplicationContext(), "Unable to find From or To location", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: add icon
        map.addMarker(new MarkerOptions().position(from).title(routeItem.getFrom()));
        map.addMarker(new MarkerOptions().position(to).title(routeItem.getTo()));

        // TODO: make them settable in options
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.setMyLocationEnabled(true);

        // TODO: make them settable in new item activity
        GoogleDirection.withServerKey("AIzaSyDPQ1GwAKKQZaxH1cmyVbx0FLDwKqKlJD8")
                .from(from)
                .to(to)
                .transitMode(TransportMode.DRIVING)
                .execute(directionCallback);

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng result = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            result = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
