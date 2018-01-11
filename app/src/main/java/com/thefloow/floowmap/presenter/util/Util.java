package com.thefloow.floowmap.presenter.util;

import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.thefloow.floowmap.model.bo.DBJourney;
import com.thefloow.floowmap.model.db.DBHelper;

/**
 * Created by rrs27 on 2018-01-06.
 */

public class Util {

    public static LatLng getLatLngFrom(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return latLng;
    }

    public static DBJourney getDBJourneyFrom(Cursor cursor) {
        while (cursor.moveToNext()){
            int colIndex = cursor.getColumnIndex(DBHelper.JOURNEY_ID);
            int id = cursor.getInt(colIndex);

            colIndex = cursor.getColumnIndex(DBHelper.JOURNEY_NAME);
            int name = cursor.getInt(colIndex);

            colIndex = cursor.getColumnIndex(DBHelper.JOURNEY_STATUS);
            String status = cursor.getString(colIndex);

            colIndex = cursor.getColumnIndex(DBHelper.JOURNEY_TIMESTAMP);
            long timeStamp = cursor.getLong(colIndex);

            return new DBJourney(id,name,status, timeStamp);
        }
        return null;
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
