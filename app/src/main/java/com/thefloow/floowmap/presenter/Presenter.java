package com.thefloow.floowmap.presenter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thefloow.floowmap.R;
import com.thefloow.floowmap.model.bo.DBJourney;
import com.thefloow.floowmap.model.db.DBHelper;
import com.thefloow.floowmap.presenter.Location.LocationListenerImpl;
import com.thefloow.floowmap.presenter.permission.PermissionsHelper;
import com.thefloow.floowmap.presenter.util.Util;
import com.thefloow.floowmap.view.MVPView;

import java.util.List;

/**
 * Created by rrs27 on 2018-01-07.
 */

public class Presenter extends Service implements MVPPresenter {

    public static final int FIRST_JOURNEY = 1;
    // Public access variables, especially regarding user preferences
    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();
    // Service and binder
    private final IBinder binder = new PresenterBinder();
    private final String TITLE = "Floow Map";
    private final String CONTENT = "is tracking";
    private final String MSG = TITLE + " is now tracking in background";
    // Private variables representing retrieving location parameters
    private final long MIN_TIME_LOC_UPDATES_MILLI_SECS = 1000;
    private final float MIN_DIST_LOC_UPDATES_METERS = 5f;
    // Model View Presenter
    MVPView mvpView;
    // Database
    private DBHelper dbHelper;
    // Location
    private LocationManager locationManager;
    private LocationListenerImpl locationListener;
    // Flow control
    private boolean isMapReady = false;
    private PermissionsHelper permHelp;
    private boolean isJourneyOn = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        this.dbHelper = new DBHelper(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        permHelp = new PermissionsHelper();
        // START_STICKY is used for services that are explicitly started and stopped
        // https://developer.android.com/reference/android/app/Service.html
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind");
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopLocationManager(this);
    }

    @Override
    public LatLng requestModel() {
        // i.e.
        LatLng london = new LatLng(51.5, 0);
        return london;
    }

    /**
     * Call this method in activities when all of the following is satisfied:
     * activity is onResume
     * activity is bound to this service
     * map view ready.
     * It basically means all is working and prepared to receive instructions from
     * the service
     *
     * @param context
     * @param mvpView
     */
    @Override
    public void onMapReady(Context context, MVPView mvpView) {
        this.isMapReady = true;
        this.mvpView = mvpView;

        triggerPermissionsCheckUp(context);

        // This method will self-check permissions in case they're not granted by the time
        // it is called.
        startLocationManager(this);

        // In case of exists, this method will send previous journey state to the activity
        validateIsJourneyOn();
        this.mvpView.onRecoveryState(isJourneyOn);
    }

    private void validateIsJourneyOn() {
        // This query will return a single row or none
        Cursor cursor = dbHelper.getLastJourney();

        // If there is no record then there is any previous journey
        if (Util.isCursorEmpty(cursor)) {
            isJourneyOn = false;
            return;
        }

        // If there are journeys then validate the status of the last one
        DBJourney dbJourney = Util.getDBJourneyFrom(cursor);

        if (dbJourney.getStatus().equals(DBHelper.JOURNEY_BEGINS)) {
            isJourneyOn = true;
        }
    }

    @Override
    public void onActivityPaused() {
        this.isMapReady = false;
        Log.d(TAG, "onActivityPaused");
    }

    @Override
    public void onActivityRestarted() {
        this.isMapReady = true;
        Log.d(TAG, "onActivityRestarted");
    }


    @Override
    public void onActivityDestroy(Context context, Class<?> cls) {
        if (isJourneyOn) {
            createNotification(context, cls);
        } else {
            stopSelf();
        }
    }

    //    ## PERMISSIONS RELATED METHODS - BEGIN ##

    private void triggerPermissionsCheckUp(Context context) {
        if (!permHelp.areLocationPermissionsGranted(context)) {
            permHelp.showLocationPermissionsDialog(context);
        }
    }

    /**
     * Creates a notification that cancels itself once the user clicks it. Sorted above the regular
     * notifications.
     */
    public void createNotification(Context context, Class<?> cls) {
        Log.d(TAG, "createNotification");

        Intent mainActivity = new Intent(context, cls);
        int requestCode = 0;
        int flags = 0;
        PendingIntent pi = PendingIntent.getActivity(context, requestCode, mainActivity, flags);

        String channelId = "default_channel";
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(TITLE)
                .setContentText(CONTENT)
                .setTicker(MSG)
                .setSmallIcon(R.drawable.ic_tracking_on)
                .setContentIntent(pi)
                .setAutoCancel(true) // Notification is automatically canceled when the user clicks it in the panel
                .setOngoing(true) //  Sorted above the regular notifications in the notification panel and do not have an 'X' close button, and are not affected by the "Clear all" button
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    /**
     * Stop requesting updates from the location manager
     */
    private void stopLocationManager(Context context) {
        Log.d(TAG, "stopLocationManager");

        if (!permHelp.areLocationPermissionsGranted(context)) {
            Log.d(TAG, "LocationManager is stopped due to permissions denied.");
            return;
        }

        try {
            locationManager.removeUpdates(locationListener);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString()); // This exception: no worries, listener was already null
        }
    }


    // ## Location Tracking ##

    /**
     * Start requesting updates from the location manager. Instantiates location manager and assigns
     * a listener.
     */
    @SuppressLint("MissingPermission")
    private void startLocationManager(Context context) {
        Log.d(TAG, "startLocationManager");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListenerImpl(this);

        if (!permHelp.areLocationPermissionsGranted(context)) {
            Log.d(TAG, "LocationManager is stopped due to permissions denied.");
            return;
        }

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_LOC_UPDATES_MILLI_SECS,
                    MIN_DIST_LOC_UPDATES_METERS,
                    locationListener
            );
        } catch (IllegalArgumentException iae) {
            Log.d(TAG, "Provider is null or doesn't exist on this device or listener is null");
        } catch (SecurityException se) {
            Log.d(TAG, "No suitable permission is present");
        } catch (RuntimeException re) {
            Log.d(TAG, "The calling thread has no Looper");
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        Log.d(TAG, "onLocationReceived");

        // Persist the model in the DB
        addToCurrentJourney(location);

        // If map is ready then send location to view
        addToCurrentMap(location);
    }

    private void addToCurrentMap(Location location) {
        if (isMapReady) {

            if (isJourneyOn) {
                Cursor cJourney = dbHelper.getLastJourney();
                DBJourney dbJourney = Util.getDBJourneyFrom(cJourney);

                Cursor cLocations = dbHelper.getJourneyLocations(dbJourney.getJourneyId());
                List<LatLng> latLngs = Util.getLatLngsFrom(cLocations);
                mvpView.onDrawJourney(latLngs);
            }

            LatLng latLng = Util.getLatLngFrom(location);
            mvpView.onNewLocation(latLng);
        }
    }

    private void addToCurrentJourney(Location location) {
        // if isJourneyOn = true, then, it means there is currently an ongoing journey
        if (!isJourneyOn) {
            Log.d(TAG, "addToCurrentJourney: can't add location to DB 'cause isJourneyOn = " + isJourneyOn);
            return;
        }
        if (Util.isLocationEmpty(location)) {
            Log.d(TAG, "addToCurrentJourney: can't add location to DB 'cause location is empty ");
            return;
        }

        // Obtain current journey
        Cursor cursor = dbHelper.getLastJourney();
        DBJourney dbJourney = Util.getDBJourneyFrom(cursor);
        if (dbJourney == null) {
            Log.d(TAG, "addToCurrentJourney: can't add location to DB 'cause DBJourney is null ");
            return;
        }

        if (dbJourney.getStatus().equals(DBHelper.JOURNEY_BEGINS)) {
            dbHelper.saveJourneyLocation(dbJourney.getJourneyId(), location);
        }
    }


    /**
     * This method allows:
     * to draw the user's journey in the map;
     * the service to know if the trip should be recorded;
     * if the activity is destroyed:
     * runs tracking in background;
     * creates a notification icon in the bar;
     * The initial state is journey off.
     */
    @Override
    public void toggleJourneyOnOff() {
        isJourneyOn = !isJourneyOn;
        Log.d(TAG, "isJourneyOn=" + isJourneyOn);

        manageJourney(isJourneyOn);

    }

    /**
     * Sets the journey to null if false, otherwise, returns a brand new
     * Journey object
     *
     * @param isNewJourney
     */
    private void manageJourney(boolean isNewJourney) {
        // This query will return a single row or none
        Cursor cursor = dbHelper.getLastJourney();

        if (isNewJourney) {
            startNewJourney(cursor);
        } else {
            stopJourney(cursor);
        }
    }

    private void stopJourney(Cursor cursor) {
        Log.d(TAG, "stopJourney");

        DBJourney dbJourney = Util.getDBJourneyFrom(cursor);
        dbHelper.saveJourney(dbJourney.getJourneyId(), Util.getUnixTime(), DBHelper.JOURNEY_ENDS);
    }

    private void startNewJourney(Cursor cursor) {
        Log.d(TAG, "startNewJourney");

        // If true we'll insert the record for the very first time
        if (Util.isCursorEmpty(cursor)) {
            startFirstJourney();
        } else {
            startJourney(cursor);
        }
    }

    private void startJourney(Cursor cursor) {
        DBJourney dbJourney = Util.getDBJourneyFrom(cursor);
        int newJourneyId = dbJourney.getJourneyId() + 1;
        dbHelper.saveJourney(newJourneyId, Util.getUnixTime(), DBHelper.JOURNEY_BEGINS);
    }

    /**
     * When the DB is brand new, this method will set the values for the first
     * record
     */
    private void startFirstJourney() {
        Log.d(TAG, "startFirstJourney");
        dbHelper.saveJourney(FIRST_JOURNEY, Util.getUnixTime(), DBHelper.JOURNEY_BEGINS);
    }

    /**
     * Creates an interface between this service (Presenter.java) and this client (MapsActivity.java).
     * No inter process communication allowed in this version.
     */
    public class PresenterBinder extends Binder {

        /**
         * @return Presenter - A reference to a service and, therefore, to it's public methods.
         */
        public Presenter getService() {
            return Presenter.this;
        }
    }
}
