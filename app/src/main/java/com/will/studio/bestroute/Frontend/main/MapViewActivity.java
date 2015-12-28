package com.will.studio.bestroute.Frontend.main;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.will.studio.bestroute.Backend.RouteItem;
import com.will.studio.bestroute.R;

import java.util.List;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private RouteItem routeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        routeItem = (RouteItem) getIntent().getSerializableExtra(MainActivity.ITEM_NAME);
        if (routeItem == null) {
            return;
        }
        TextView view = (TextView) findViewById(R.id.map_view_text);
        view.setText("From " + routeItem.getFrom() + " to " + routeItem.getTo());

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng from = getLocationFromAddress(getApplicationContext(), routeItem.getFrom());
        LatLng to = getLocationFromAddress(getApplicationContext(), routeItem.getTo());

        googleMap.addMarker(new MarkerOptions()
                .position(from)
                .title("From"));
        googleMap.addMarker(new MarkerOptions()
                .position(to)
                .title("To"));
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
