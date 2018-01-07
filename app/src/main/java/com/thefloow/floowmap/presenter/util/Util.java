package com.thefloow.floowmap.presenter.util;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by rrs27 on 2018-01-06.
 */

public class Util {

    public static final int LOCATION_PERMISSIONS_REQUEST_CODE = 100;
    public static final String[] locationPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private boolean isUserOkWithLocationPermissions = false;
    private boolean isOkGoingToAppSettings = false;

    public boolean isUserOkWithLocationPermissions() {
        return isUserOkWithLocationPermissions;
    }

    public void setUserOkWithLocationPermissions(boolean userOkWithLocationPermissions) {
        isUserOkWithLocationPermissions = userOkWithLocationPermissions;
    }

    public boolean isOkGoingToAppSettings() {
        return isOkGoingToAppSettings;
    }

    public void setOkGoingToAppSettings(boolean okGoingToAppSettings) {
        isOkGoingToAppSettings = okGoingToAppSettings;
    }

    public boolean areLocationPermissionsGranted(Context context) {

        if (isCoarseLocationPermissionGranted(context) == true &&
                isFineLocationPermissionGranted(context) == true) {
            return true;
        }

        return false;
    }

    public boolean isCoarseLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    public boolean isFineLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void validatePermissionsResult(Context context, int requestCode) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST_CODE:
                didUserGrantLocationPermissions(context);
                break;
            default:
                break;
        }
    }

    private void didUserGrantLocationPermissions(Context context) {
        if (areLocationPermissionsGranted(context)) {
            isUserOkWithLocationPermissions = true;
        } else {
            showSimpleDialog(context,
                    "Not showing your location",
                    "Change this later on Settings / Apps / Permissions");
        }
    }

    public void showSimpleDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

//    public boolean askUserIfWantToGoToAppSettings(Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder
//                .setCancelable(false)
//                .setTitle("Permissions Request")
//                .setMessage("Show your current location?")
//                .setPositiveButton("Yes, go to settings", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        isOkGoingToAppSettings = true;
//                    }
//                })
//                .setNegativeButton("No, maybe later", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        isOkGoingToAppSettings = false;
//                    }
//                })
//                .show();
//        return isOkGoingToAppSettings();
//    }
}
