package com.example.poppop;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.poppop.Fragments.ChatFragment;
import com.example.poppop.Fragments.MainFragment;
import com.example.poppop.Fragments.ProfileFragment;
import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.LocationUtils;
import com.example.poppop.Utils.Utils;
import com.example.poppop.ViewModel.UsersViewModel;
import com.example.poppop.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

public class MainActivity extends AppCompatActivity implements LocationUtils.GeoPointResultListener {
    ActivityMainBinding binding;
    private boolean hasNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update the notification state
                MenuItem chatMenuItem = binding.bottomNavigationView.getMenu().findItem(R.id.chat);
                hasNotification = true; // or set it to whatever value you need
                chatMenuItem.setIcon(R.drawable.menu_item_icon_with_circle);
                Log.d("Messs", "Hello");
            }
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(notificationReceiver, new IntentFilter("notification_received"));


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            replaceFragment(new MainFragment(), new Bundle());
            binding.bottomNavigationView.setSelectedItemId(R.id.main);
            // If user is logged in, display user details
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.chat) {
                    // Update notification state
                    hasNotification = false;
                    // Set the menu item icon based on the notification state
                    MenuItem chatMenuItem = binding.bottomNavigationView.getMenu().findItem(R.id.chat);
                    if (hasNotification) {
                        chatMenuItem.setIcon(R.drawable.menu_item_icon_with_circle);
                    } else {
                        chatMenuItem.setIcon(R.drawable.chat);
                    }

                    replaceFragment(new ChatFragment(), new Bundle());
                } else if (itemId == R.id.main) {
                    replaceFragment(new MainFragment(), new Bundle());
                } else if (itemId == R.id.profile) {
                    replaceFragment(new ProfileFragment(), new Bundle());
                }
                return true;
            });

            Utils.checkNotificationPermission(MainActivity.this, this);
            Utils.checkLocationPermission(MainActivity.this, this);

            FirestoreUserUtils.getUserModelByUid(FirebaseUtils.currentUserId())
                    .addOnSuccessListener(userModel -> {
                        if (userModel != null) {
                            FirestoreUserUtils.updateFCMTokenForUser(userModel);
                            LocationUtils.getCurrentLocation(this, this, this);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure case
                        Log.e("Firestore", "Error retrieving UserModel: " + e.getMessage());
                        finish();
                    });
        }
    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        if (args != null)
            fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onGeoPointResult(GeoPoint geoPoint) {
        if (geoPoint != null) {
            // Handle the received GeoPoint (latitude and longitude)
            double latitude = geoPoint.getLatitude();
            double longitude = geoPoint.getLongitude();
//            Toast.makeText(this, "Location get", Toast.LENGTH_SHORT).show();
//            Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
            // Add your logic here
            FirestoreUserUtils.updateLocation(FirebaseUtils.currentUserId(), geoPoint);
        } else {
            // Handle case where the location is null
            Log.d("Location", "Location is null");
        }
    }
}
