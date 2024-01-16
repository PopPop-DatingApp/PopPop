package com.example.poppop.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;

public class LocationUtils {

    public interface LocationResultListener {
        void onLocationResult(Location location);
    }

    public interface GeoPointResultListener {
        void onGeoPointResult(GeoPoint geoPoint);
    }

    public static void requestLocationPermission(Activity activity, Context context) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
        }
    }

    public static void getCurrentLocation(Activity activity, Context context, GeoPointResultListener geoPointResultListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, location -> {
                        if (location != null) {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            geoPointResultListener.onGeoPointResult(geoPoint);
                        } else {
                            // Location is null, handle accordingly
                            geoPointResultListener.onGeoPointResult(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        geoPointResultListener.onGeoPointResult(null);
                    });
        } else {
            // Location permission not granted, request it
            requestLocationPermission(activity, context);
        }
    }
}
