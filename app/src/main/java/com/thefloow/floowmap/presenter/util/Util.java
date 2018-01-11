package com.thefloow.floowmap.presenter.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rrs27 on 2018-01-06.
 */

public class Util {

    public static LatLng getLatLngFrom(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return latLng;
    }

//    public static void saveSP(Context context, String sharedPreferences, String key, boolean value) {
//        SharedPreferences settings = context.getSharedPreferences(sharedPreferences, context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putBoolean(key,value);
//        editor.commit();
//    }
//
//    public static boolean readSP(Context context, String sharedPreferences, String key, boolean defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(sharedPreferences, context.MODE_PRIVATE);
//        return settings.getBoolean(key,defaultValue);
//    }
//
//    public static void saveSP(Context context, String sharedPreferences, String key, int value) {
//        SharedPreferences settings = context.getSharedPreferences(sharedPreferences, context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putInt(key,value);
//        editor.commit();
//    }
//
//    public static int readSP(Context context, String sharedPreferences, String key, int defaultValue) {
//        SharedPreferences settings = context.getSharedPreferences(sharedPreferences, context.MODE_PRIVATE);
//        return settings.getInt(key,defaultValue);
//    }
}
