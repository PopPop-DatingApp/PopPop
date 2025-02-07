package com.example.poppop.Utils;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static List<String> getHoroscopeSigns() {
        return Arrays.asList(
                "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
                "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
        );
    }

    public static String getTextInterest(List<String> interestList){
        StringBuilder interests = new StringBuilder();
        for(int i = 0; i < interestList.size(); i++) {
            interests.append(interestList.get(i)).append(", ");
        }
        return interests.substring(0, interests.length() - 2);
    }

    public static void setProfilePic(Context context, String imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static String timestampToDateAndTime(Timestamp timestamp) {
        return new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(timestamp.toDate());
    }


    public static void checkNotificationPermission(Activity activity, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(activity,new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    public static void checkLocationPermission(Activity activity, Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
        }
    }
}
