package com.thefloow.floowmap.presenter.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by rrs27 on 2018-01-10.
 */

public class PermissionsHelper /*extends FragmentActivity*/ {

//    private MVPPresenter presenter;

    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();


    public static final int LOCATION_PERMISSIONS_REQUEST_CODE = 100;
    public static final String[] locationPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    public static final String HELP_DIALOG_TITLE = "Your location is not shown";
    public static final String HELP_DIALOG_MSG ="Change this on Settings / Apps / Floow Map / Permissions";
    public static final String HELP_DIALOG_NEUTRAL_BTN = "Ok";

    public static final String PERMISSION_GRANTED_MSG = "Permissions granted, thank you!";

//    public PermissionsHelper(MVPPresenter presenter) {
//        Log.d(TAG,"PermissionsHelper");
//        this.presenter = presenter;
//    }

    public static boolean areLocationPermissionsGranted(Context context) {

        if (isCoarseLocationPermissionGranted(context) == true &&
                isFineLocationPermissionGranted(context) == true) {
            return true;
        }

        return false;
    }

    public static boolean isCoarseLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public static boolean isFineLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void showLocationPermissionsDialog(final Context context) {
        Log.d(TAG,"showLocationPermissionsDialog");

        // todo: consider using this class context instead of method
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setCancelable(false)
                .setTitle("Permissions Request")
                .setMessage("Show your current location?")
                .setPositiveButton("Yes, go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askUserForLocationPermissions(context);
                    }
                })
                .setNegativeButton("No, maybe later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        util.setUserOkWithLocationPermissions(false);
                    }
                })
                .show();
    }

    private void askUserForLocationPermissions(Context context) {
        Log.d(TAG,"askUserForLocationPermissions");
        // The callback to this method is given on onRequestPermissionsResult
        // therefore this class is extending from FragmentActivity
        ActivityCompat.requestPermissions((Activity) context,
                locationPermissions,
                LOCATION_PERMISSIONS_REQUEST_CODE);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if(isLocationPermissionGranted(this, requestCode)){
//            presenter.onReceivePermissionGranted(true);
//        }else{
//            presenter.onReceivePermissionGranted(false);
//            showHelpDialog(this,
//                    "Your location is not shown",
//                    "Change this later on Settings / Apps / Permissions");
//        }
//    }

    public boolean isLocationPermissionGranted(Context context, int requestCode){
        if(isRequestCodeOk(requestCode,LOCATION_PERMISSIONS_REQUEST_CODE)){
            if(areLocationPermissionsGranted(context)){
                return true;
            }
        }
        return false;
    }

    public boolean isRequestCodeOk(int requestCode, int permissionRequested){
        if(requestCode==permissionRequested){
            return true;
        }
        return false;
    }

    public static void showHelpDialog(Context context) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);

        builder
                .setCancelable(true)
                .setTitle(HELP_DIALOG_TITLE)
                .setMessage(HELP_DIALOG_MSG)
                .setNeutralButton(HELP_DIALOG_NEUTRAL_BTN, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
