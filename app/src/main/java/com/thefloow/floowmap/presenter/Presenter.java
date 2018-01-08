package com.thefloow.floowmap.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.thefloow.floowmap.R;
import com.thefloow.floowmap.presenter.util.Util;

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


    private final String TITLE = "Floow Map";
    private final String CONTENT = "is tracking";
    private final String MSG = TITLE + " is now tracking in background";

    private int myVal = 0;


    private Util util = new Util();




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
        public void createNotification(Context context, Class<?> cls){
            Presenter.this.createNotification(context, cls);
        }
    }

    /**
     * Creates a notification that cancels itself once the user clicks it. Sorted above the regular
     * notifications.
     */
    private void createNotification(Context context, Class<?> cls){
        Log.d(TAG,"createNotification");

        Intent mainActivity = new Intent(context, cls);
        int requestCode =0;
        int flags=0;
        PendingIntent pi = PendingIntent.getActivity(context,requestCode,mainActivity,flags);

        String channelId = "default_channel";
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(TITLE)
                .setContentText(CONTENT)
                .setTicker(MSG)
                .setSmallIcon(R.drawable.ic_tracking_on)
                .setContentIntent(pi)
                .setAutoCancel(true) // Notification is automatically canceled when the user clicks it in the panel
                .setOngoing(true) //  Sorted above the regular notifications in the notification panel and do not have an 'X' close button, and are not affected by the "Clear all" button
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }

}
