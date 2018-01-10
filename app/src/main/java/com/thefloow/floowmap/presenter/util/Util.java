package com.thefloow.floowmap.presenter.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rrs27 on 2018-01-06.
 */

public class Util {

    public LatLng getLatLngFrom(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return latLng;
    }
}
