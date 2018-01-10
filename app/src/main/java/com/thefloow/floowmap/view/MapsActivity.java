package com.thefloow.floowmap.view;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thefloow.floowmap.R;
import com.thefloow.floowmap.presenter.MVPPresenter;
import com.thefloow.floowmap.presenter.Presenter;
import com.thefloow.floowmap.presenter.permission.PermissionsHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MVPView {

//  You cannot instantiate a GoogleMap object directly, rather, you must obtain one from the getMapAsync() method on a MapFragment or MapView
//  therefore, this object is part of the UI.
//  https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap
    private GoogleMap mMap;

    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();

    private MVPPresenter presenter;
    private boolean isServiceBound=false;

    // Makes UI components available for all the activity
    SupportMapFragment mapFragment;
    OnMapReadyCallback onMapReadyCallback;

    /*## ACTIVITY LIFECYCLE ##*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setAndBindService();

        // Obtain the SupportMapFragment and set both as class variables
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        onMapReadyCallback = this;
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

        if(isServiceBound){
            presenter.onActivityDestroy(this, this.getClass());
            unbindService(serviceConnection);
            isServiceBound= false;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady");

        mMap = googleMap;

        // This will trigger permissions check-up in the service
        presenter.onMapReady(this);

        // todo: don't forget to remove these lines after testing
        LatLng london = presenter.requestModel();
        mMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(london));

        if (PermissionsHelper.areLocationPermissionsGranted(this)) {
            mMap.setMyLocationEnabled(true);
        }
    }


//    ## ACTIVITY DEPENDANT METHODS ##

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
        Log.d(TAG, "onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(PermissionsHelper.areLocationPermissionsGranted(this)){
            mMap.clear();
            mapFragment.getMapAsync(onMapReadyCallback);
            Toast.makeText(this,PermissionsHelper.PERMISSION_GRANTED_MSG, Toast.LENGTH_LONG).show();
        }else{
            PermissionsHelper.showHelpDialog(this);
        }
    }

    /**
     * Starts and bind location service without creating a notification
     */
    private void setAndBindService() {
        Intent serviceIntent = new Intent(this,Presenter.class);
        startService(serviceIntent);
        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Presenter.PresenterBinder binder = (Presenter.PresenterBinder) service;
            presenter = binder.getService();
            isServiceBound = true;

            // When service is bound then prepare map.
            // Call back in method: onMapReady(GoogleMap googleMap)
            mapFragment.getMapAsync(onMapReadyCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected");
            isServiceBound = false;
        }
    };

}
