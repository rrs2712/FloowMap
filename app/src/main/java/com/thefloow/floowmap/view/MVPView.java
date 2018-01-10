package com.thefloow.floowmap.view;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rrs27 on 2018-01-06.
 */

public interface MVPView {
    void onNewLocation(LatLng latLng);
}
