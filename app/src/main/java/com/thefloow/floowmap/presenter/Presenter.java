package com.thefloow.floowmap.presenter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thefloow.floowmap.R;
import com.thefloow.floowmap.presenter.Location.LocationListenerImpl;
import com.thefloow.floowmap.presenter.permission.PermissionsHelper;
import com.thefloow.floowmap.presenter.util.Util;
import com.thefloow.floowmap.view.MVPView;

/**
 * Created by rrs27 on 2018-01-07.
 */

public class Presenter extends Service implements MVPPresenter {

    // Public access variables, especially regarding user preferences
    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();
    // Private variables for binding, handling location manager and listener
    private final IBinder binder = new PresenterBinder();
    private LocationManager locationManager;
    private LocationListenerImpl locationListener;

    private final String TITLE = "Floow Map";
    private final String CONTENT = "is tracking";
    private final String MSG = TITLE + " is now tracking in background";

    private boolean isTrackingOn = false;
    private boolean isMapReady = false;
    private boolean isActivityResumed = false;

    // Private variables representing retrieving location parameters
    private final long MIN_TIME_LOC_UPDATES_MILLI_SECS = 1000;
    private final float MIN_DIST_LOC_UPDATES_METERS = 5f;

    //
    MVPView mvpView;

    private Util util = new Util();
    private PermissionsHelper permHelp;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        permHelp = new PermissionsHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
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
        // todo: create method to stop service here and in onActivityDestroy
//        stopSelf();
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopLocationManager(this);
    }

    @Override
    public LatLng requestModel() {
        return getFirstPosition();
    }

    @Override
    public void onActivityResumed() {
        this.isActivityResumed = true;
    }

    @Override
    public void onMapReady(Context context, MVPView mvpView) {
        this.isMapReady = true;
        triggerPermissionsCheckUp(context);
        startLocationManager(this);
        this.mvpView = mvpView;
    }

    @Override
    public void onActivityDestroy(Context context, Class<?> cls) {
        if (isTrackingOn) {
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
    //    ## PERMISSIONS RELATED METHODS - END ##

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

    private LatLng getFirstPosition() {
        Log.d(TAG, "requestFirstPosition");

        // Add a marker in London and move the camera
        LatLng london = new LatLng(51.5, 0);
        return london;
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


    // ## Location Tracking ##

    /**
     * Stop requesting updates from the location manager
     */
    private void stopLocationManager(Context context) {
        Log.d(TAG,"stopLocationManager");

//        // IDE auto generated code for access permissions
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(
//                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(CLA,"Location permissions denied, can't stop LocationManager");
//            return;
//        }

        if(!permHelp.areLocationPermissionsGranted(context)){
            Log.d(TAG,"Location permissions denied, can't stop LocationManager");
            return;
        }

        try {
            locationManager.removeUpdates(locationListener);
        } catch (IllegalArgumentException e) {
            Log.d(TAG,e.toString());
            Log.d(TAG,"No worries, listener was already null.");
        }
    }

    /**
     * Start requesting updates from the location manager. Instantiates location manager and assigns
     * a listener.
     */
    @SuppressLint("MissingPermission")
    private void startLocationManager(Context context) {
        Log.d(TAG,"startLocationManager");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListenerImpl(this);

//        // IDE auto generated code for access permissions
//        if (ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(
//                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(CLA,"Location permissions denied, can't start LocationManager");
//            return;
//        }

        if(!permHelp.areLocationPermissionsGranted(context)){
            Log.d(TAG,"Location permissions denied, can't stop LocationManager");
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
            Log.d(TAG,"Provider is null or doesn't exist on this device or listener is null");
        } catch (SecurityException se) {
            Log.d(TAG,"No suitable permission is present");
        } catch (RuntimeException re) {
            Log.d(TAG,"The calling thread has no Looper");
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        Log.d(TAG,"onLocationReceived");
        LatLng latLng = util.getLatLngFrom(location);
        mvpView.onNewLocation(latLng);
    }
}
