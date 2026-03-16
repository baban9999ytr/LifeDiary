package com.mustafagoksal.diary.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationFetcher {

    public static void fetchLocation(Activity activity, LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onLocationFailed();
            return;
        }

        try {
            FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(activity);

            //noinspection MissingPermission
            fusedClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    callback.onLocationFetched(location.getLatitude(), location.getLongitude());
                } else {
                    callback.onLocationFailed();
                }
            }).addOnFailureListener(e -> {
                callback.onLocationFailed();
            });
        } catch (Exception e) {
            callback.onLocationFailed();
        }
    }
}
