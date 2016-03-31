package com.will.studio.bestroute.routeData;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by egaozhi on 2016-01-04.
 * Project: BestRoute
 */
public class GoogleDirectionHelper {

    public static final String CLASS_NAME = "GoogleDirectionHelper";
    private static Direction direction;
    private static CommonDefinitions.DirectionStatus status = CommonDefinitions.DirectionStatus
            .NOTSTART;

    public static CommonDefinitions.DirectionStatus getStatus() {
        return status;
    }

    private static void setStatus(CommonDefinitions.DirectionStatus status) {
        GoogleDirectionHelper.status = status;
        Log.d(CLASS_NAME, "setStatus to " + GoogleDirectionHelper.status);
    }

    public static Direction getDirection() {
        return direction;
    }

    public static void setDirection(Direction inDirection) {
        direction = inDirection;
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng result = null;
        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            if (address.size() <= 0) {
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
