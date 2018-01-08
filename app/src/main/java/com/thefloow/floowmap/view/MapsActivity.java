package com.thefloow.floowmap.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thefloow.floowmap.R;
import com.thefloow.floowmap.presenter.Presenter;
import com.thefloow.floowmap.presenter.util.Util;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Util util = new Util();

    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();

    private Presenter.LocationServiceBinder service;
    private boolean keepServiceRunning;

    /*## ACTIVITY LIFECYCLE ##*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        triggerPermissionsCheckUp();

        startAndBindLocationService();
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop");
        super.onStop();
    }

    /**
     * Destroy all fragments and loaders.
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();

        if(keepServiceRunning){
            service.createNotification(this, this.getClass());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        if (util.areLocationPermissionsGranted(this)) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void triggerPermissionsCheckUp() {
        if (!util.areLocationPermissionsGranted(this)) {
            showLocationPermissionsDialog();
        }
    }

//    ## ACTIVITY DEPENDANT METHODS ##

    private void askUserForLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                Util.locationPermissions,
                Util.LOCATION_PERMISSIONS_REQUEST_CODE);
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        util.validatePermissionsResult(this, requestCode);
    }

    public void showLocationPermissionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setCancelable(false)
                .setTitle("Permissions Request")
                .setMessage("Show your current location?")
                .setPositiveButton("Yes, go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askUserForLocationPermissions();
                    }
                })
                .setNegativeButton("No, maybe later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        util.setUserOkWithLocationPermissions(false);
                    }
                })
                .show();
    }

    /**
     * Starts and bind location service without creating a notification
     */
    private void startAndBindLocationService() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Presenter.AUTO_CREATE_NOTIFICATION,false);

        Intent serviceIntent = new Intent(this,Presenter.class);
        serviceIntent.putExtras(bundle);

        this.startService(serviceIntent);
        bindLocationService(serviceIntent);
        keepServiceRunning = false;
//        manageGUI();
    }

    /**
     * Binds to location service
     * @param serviceIntent
     */
    private void bindLocationService(Intent serviceIntent) {
        this.bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Connects to location service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            MapsActivity.this.service = (Presenter.LocationServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
        }
    };

}
