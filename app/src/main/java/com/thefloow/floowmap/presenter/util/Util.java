package com.thefloow.floowmap.presenter.util;

import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thefloow.floowmap.model.bo.DBJourney;
import com.thefloow.floowmap.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rrs27 on 2018-01-06.
 */

public class Util {

    private static final String DEV = "RRS";
    private static final String TAG = DEV + ":Util";

    public static LatLng getLatLngFrom(Location location) {
        Log.v(TAG,"getLatLngFrom");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        return latLng;
    }

    public static DBJourney getDBJourneyFrom(Cursor cursor) {
        Log.v(TAG,"getDBJourneyFrom");

        if(isCursorEmpty(cursor)){return null;}

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

    public static List<DBJourney> getDBJourneyListFrom(Cursor cursor) {
        Log.v(TAG,"getDBJourneyFrom");
        if(isCursorEmpty(cursor)){return null;}

        List<DBJourney> dbJourneys = new ArrayList<>();
        while (cursor.moveToNext()){
            dbJourneys.add(getDBJourneyFrom(cursor));
        }
        return dbJourneys;
    }


    public static boolean isCursorEmpty(Cursor cursor){
        Log.d(TAG,"isCursorEmpty");
        if(cursor.equals(null)){return true;}
        if (cursor.getCount() == 0){ return true; }
        return false;
    }

    public static boolean isLocationEmpty(Location location){
        Log.d(TAG,"isLocationEmpty");
        if(location.equals(null)){return true;}
        return false;
    }

    public static long getUnixTime(){
        return System.currentTimeMillis();
    }

    public static List<LatLng> getLatLngsFrom(Cursor cursor) {
        Log.v(TAG,"getLatLngsFrom");

        if(isCursorEmpty(cursor)){return null;}

        List<LatLng> latLngs = new ArrayList<>();

        while (cursor.moveToNext()){
            int colIndex = cursor.getColumnIndex(DBHelper.LOCATION_LAT);
            double lat = cursor.getDouble(colIndex);

            colIndex = cursor.getColumnIndex(DBHelper.LOCATION_LON);
            double lon = cursor.getDouble(colIndex);

            LatLng latLng = new LatLng(lat,lon);
            latLngs.add(latLng);
        }

        return latLngs;
    }
}
