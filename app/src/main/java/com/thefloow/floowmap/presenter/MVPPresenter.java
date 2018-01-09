package com.thefloow.floowmap.presenter;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rrs27 on 2018-01-06.
 */

public interface MVPPresenter {
    LatLng requestModel();
    void onActivityDestroy(Context context, Class<?> cls);
}
