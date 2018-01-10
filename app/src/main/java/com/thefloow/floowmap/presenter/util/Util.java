package com.thefloow.floowmap.presenter.util;

/**
 * Created by rrs27 on 2018-01-06.
 */

public class Util {

//    public static final int LOCATION_PERMISSIONS_REQUEST_CODE = 100;
//    public static final String[] locationPermissions = new String[]{
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION};

    private boolean isOkGoingToAppSettings = false;

    public boolean isOkGoingToAppSettings() {
        return isOkGoingToAppSettings;
    }

    public void setOkGoingToAppSettings(boolean okGoingToAppSettings) {
        isOkGoingToAppSettings = okGoingToAppSettings;
    }

//    public boolean areLocationPermissionsGranted(Context context) {
//
//        if (isCoarseLocationPermissionGranted(context) == true &&
//                isFineLocationPermissionGranted(context) == true) {
//            return true;
//        }
//
//        return false;
//    }

//    public boolean isCoarseLocationPermissionGranted(Context context) {
//        if (ActivityCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        return false;
//    }
//
//
//    public boolean isFineLocationPermissionGranted(Context context) {
//        if (ActivityCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        return false;
//    }

//    public void validatePermissionsResult(Context context, int requestCode) {
//        switch (requestCode) {
//            case LOCATION_PERMISSIONS_REQUEST_CODE:
//                didUserGrantLocationPermissions(context);
//                break;
//            default:
//                break;
//        }
//    }

//    public boolean isLocationPermissionGranted(Context context, int requestCode){
//            if(isRequestCodeOk(requestCode,LOCATION_PERMISSIONS_REQUEST_CODE)){
//                if(areLocationPermissionsGranted(context)){
//                    return true;
//                }
//            }
//        return false;
//    }

//    public boolean isRequestCodeOk(int requestCode, int permissionRequested){
//        if(requestCode==permissionRequested){
//            return true;
//        }
//        return false;
//    }

//    public void didUserGrantLocationPermissions(Context context) {
//        boolean answer = false;
//
//        if (areLocationPermissionsGranted(context)) {
////            todo: delete this method if not needed
////            isUserOkWithLocationPermissions = true;
//        } else {
//            showSimpleDialog(context,
//                    "Not showing your location",
//                    "Change this later on Settings / Apps / Permissions");
//        }
//    }

//    public void showSimpleDialog(Context context, String title, String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        builder
//                .setCancelable(true)
//                .setTitle(title)
//                .setMessage(message)
//                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
//                .show();
//    }

}
