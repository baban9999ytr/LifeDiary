package com.mustafagoksal.diary.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LocationFetcher {

    public static void fetchLocation(Activity activity, LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationFailed();
            return;
        }

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            callback.onLocationFailed();
            return;
        }

        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isNetworkEnabled && !isGpsEnabled) {
            callback.onLocationFailed();
            return;
        }

        String provider = isGpsEnabled ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation != null) {
                callback.onLocationFetched(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                return;
            }


            LocationListener listener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    locationManager.removeUpdates(this);
                    callback.onLocationFetched(location.getLatitude(), location.getLongitude());
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    locationManager.removeUpdates(this);
                    callback.onLocationFailed();
                }
            };
            

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestSingleUpdate(provider, listener, activity.getMainLooper());
            }

        } catch (SecurityException e) {
            callback.onLocationFailed();
        }
    }
}
