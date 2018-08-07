package com.muhendis.diabetwatch.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.muhendis.diabetwatch.R;
import com.muhendis.diabetwatch.helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service implements LocationListener {
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 20000;
    private static LocationService mLocationService;
    Location startLocation;
    LocationManager locationManager;
    public List<String> walkingSpeeds;
    private final String TAG = "LocationService";
    public int walkedDistance=0,minSpeed,maxSpeed;
    UIHelper mUIHelper;


    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"LOCATİONLİSTENER ONSTARTCOMMAND");
        minSpeed = intent.getIntExtra(getResources().getString(R.string.minimumSpeedIntentKey),0);
        maxSpeed = intent.getIntExtra(getResources().getString(R.string.maximumSpeedIntentKey),0);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (gps_enabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

        if (network_enabled)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    this);


        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent();
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext(), getResources().getString(R.string.default_notification_channel_id))
                    .setContentTitle("Yürüme Hızınız Kontrol Ediliyor")
                    .setContentText("-Sarı: Hızlan -Kırmızı:Yavaşla -Yeşil:Olduğun hızda devam et")
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        else{



            Intent resultIntent = new Intent();

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            notification =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("Yürüme Hızınız Kontrol Ediliyor")
                            .setContentText("-Sarı: Hızlan -Kırmızı:Yavaşla -Yeşil:Olduğun hızda devam et")
                            .setContentIntent(resultPendingIntent)
                            .build();

        }

        startForeground(002, notification);
        mLocationService = this;
        walkingSpeeds = new ArrayList<String>();
        mUIHelper = new UIHelper(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUIHelper.cancelPermanentSpeedNotification();
        locationManager.removeUpdates(this);
        Log.d(TAG,"LocationListener onDestroy");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"ON LOCATION CHANGED Speed: "+location.getSpeed());
        if(startLocation==null)
            startLocation = location;
        else{
            float currentSpeedKmOverHours = location.getSpeed()*60*60/1000;
            if(isBetterLocation(location,startLocation)){
                walkedDistance+=location.distanceTo(startLocation);
                startLocation = location;

                if(currentSpeedKmOverHours<minSpeed){
                    String title = "DAHA HIZLI - HIZINIZ: "+ String.format("%.1f",currentSpeedKmOverHours);
                    String body = "Minimum Hız: "+minSpeed+"\nMaksimum Hız: "+maxSpeed;
                    mUIHelper.createPermanentNotificationForSpeed(title,body,0);
                }
                else if(currentSpeedKmOverHours>maxSpeed)
                {
                    String title = "DAHA YAVAŞ - HIZINIZ: "+ String.format("%.1f",currentSpeedKmOverHours);
                    String body = "Minimum Hız: "+minSpeed+"\nMaksimum Hız: "+maxSpeed;
                    mUIHelper.createPermanentNotificationForSpeed(title,body,2);
                }
                else if(currentSpeedKmOverHours>=minSpeed && currentSpeedKmOverHours<=maxSpeed){
                    String title = "BU HIZDA DEVAM ET - HIZINIZ: "+ String.format("%.1f",currentSpeedKmOverHours);
                    String body = "Minimum Hız: "+minSpeed+"\nMaksimum Hız: "+maxSpeed;
                    mUIHelper.createPermanentNotificationForSpeed(title,body,1);
                }

                walkingSpeeds.add(String.format("%.1f",currentSpeedKmOverHours));
            }

        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public static LocationService getInstance(){
        return mLocationService;
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
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
        if (isSignificantlyNewer) {
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
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
