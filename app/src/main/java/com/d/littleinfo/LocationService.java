package com.d.littleinfo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class LocationService extends Service {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    String LOCATION_UPDATE_INTENT_FILTER = "LOCATION_UPDATE_INTENT_FILTER";
    FirebaseDatabase database;
    DatabaseReference myRef;
    Intent intent;

    @SuppressLint("InvalidWakeLock  Tag")
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();

        intent = new
                Intent(LOCATION_UPDATE_INTENT_FILTER);
        intent.setPackage("com.d.littleinfo");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          Log.e("testrun", "run:  Hello Call" );

                                      }
                                  },
                10*1000, 1000 * 120 );



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.d.littleinfo";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FB is in Background ")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);
    }



    @Override
    public int onStartCommand(Intent intenTTTt, int flags, int startId)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                listener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
            }

        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);

        }

        Log.d("started", "onStartCommand: ");

        return Service.START_STICKY;

    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;
        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer)
        {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }
        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());
        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);

    }

    @Override
    public void onDestroy() {
        if (locationManager != null && listener != null)
            locationManager.removeUpdates(listener);




        super.onDestroy();

    }


    public class MyLocationListener implements LocationListener
    {


        @Override
        public void onLocationChanged(final Location loc)
        {




            Log.e("onlocvarion","onLocationChanged----->>>>>");
            if (loc != null)
            {
                if (isBetterLocation(loc, previousBestLocation))
                {
                    System.out.println("onLocationChanged--> success"+ loc.getLatitude() +""+ loc.getLongitude());

                    loc.getLatitude();
                    loc.getLongitude();

                    intent.putExtra("Latitude", String.valueOf(loc.getLatitude()));
                    intent.putExtra("Longitude", String.valueOf(loc.getLongitude()));
                    intent.putExtra("Provider", loc.getProvider());
                    myRef.child(SharedHelper.getKey(getApplicationContext(),"user")).child("lat").setValue(String.valueOf(loc.getLatitude()));
                    myRef.child(SharedHelper.getKey(getApplicationContext(),"user")).child("lang").setValue(String.valueOf(loc.getLongitude()));
                    myRef.child(SharedHelper.getKey(getApplicationContext(),"user")).child("time").setValue(String.valueOf(new Date().getTime()));


                    previousBestLocation=loc;


                }
            }
        }


        public void onProviderDisabled(String provider) {
         //   Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_LONG).show();

        }

        public void onProviderEnabled(String provider) {
           // Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_LONG).show();

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


    }





}