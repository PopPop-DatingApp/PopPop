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

public class Utils {
    public static List<String> getHoroscopeSigns() {
        return Arrays.asList(
                "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
                "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
        );
    }

    public static void setProfilePic(Context context, String imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static void checkNotificationPermission(Activity activity, Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(activity,new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    public static void checkLocationPermission(Activity activity, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
        }
    }
}
