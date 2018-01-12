package com.thefloow.floowmap.view;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thefloow.floowmap.R;
import com.thefloow.floowmap.presenter.MVPPresenter;
import com.thefloow.floowmap.presenter.Presenter;
import com.thefloow.floowmap.presenter.permission.PermissionsHelper;
import com.thefloow.floowmap.view.history.JourneyHistoryActivity;

import java.util.List;

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

        setAndBindService();

        setContentView(R.layout.activity_maps);
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
        presenter.onActivityPaused();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG,"onRestart");
        super.onRestart();
        presenter.onActivityRestarted();
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
        presenter.onMapReady(this, this);

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

    @Override
    public void onDrawJourney(List<LatLng> latLngs) {
        Log.d(TAG, "onDrawJourney = " + latLngs.size() );

        LatLng latLngIni = latLngs.get(0);
        MarkerOptions startLocation = new MarkerOptions()
                .position(latLngIni)
                .title("Start Point");

        PolylineOptions polyline = new PolylineOptions()
                .addAll(latLngs)
                .width(5f)
                .color(Color.BLUE)
                .geodesic(true);
        mMap.clear();
        mMap.addMarker(startLocation);
        mMap.addPolyline(polyline);
    }

    @Override
    public void onNewLocation(LatLng latLng) {
        Log.d(TAG, "onNewLocation");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }

    public void onJourneySwitch(View view){
        Log.d(TAG,"onJourneySwitch");
        presenter.toggleJourneyOnOff();
    }

    public void onJourneyHistory(View view){
        Log.d(TAG,"onJourneySwitch");
        Intent i = new Intent(this, JourneyHistoryActivity.class);
        startActivity(i);
    }

    @Override
    public void onRecoveryState(boolean isJourneyOn) {
        Log.d(TAG,"onRecoveryState");
        Switch aSwitch = (Switch) findViewById(R.id.switch_btn_journey);
        aSwitch.setChecked(isJourneyOn);
    }
}
