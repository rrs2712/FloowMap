package com.thefloow.floowmap.presenter;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.thefloow.floowmap.view.MVPView;

/**
 * Created by rrs27 on 2018-01-06.
 */

public interface MVPPresenter {
    LatLng requestModel();
    void onActivityDestroy(Context context, Class<?> cls);
    void onMapReady(Context context, MVPView view);
    void onActivityResumed();
    void onLocationReceived(Location location);
}
