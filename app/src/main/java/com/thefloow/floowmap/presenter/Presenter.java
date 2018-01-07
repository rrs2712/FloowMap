package com.thefloow.floowmap.presenter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by rrs27 on 2018-01-07.
 */

public class Presenter extends Service implements MVPPresenter {

    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();

    // Public access variables, especially regarding user preferences
    public static final String AUTO_CREATE_NOTIFICATION = "create";

    // Private variables for binding, handling location manager and listener
    private final IBinder binder = new LocationServiceBinder();

    private int myVal = 0;




    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        if(myVal==0){
            myVal=1;
            Log.d(TAG, "Set myVal for the very first time=" + myVal);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        myVal = myVal + 1;
        Log.d(TAG, "A client has connected therefore myVal=" + myVal);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnBind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void requestModel() {

    }

    /**
     * Creates an interface between this service and client
     */
    public class LocationServiceBinder extends Binder {

        /**
         * Provides public access to create a notification
         */
        void createNotification(){
//            Presenter.this.createNotification();
        }
    }
}
