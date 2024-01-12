package com.example.poppop;


import static com.example.poppop.Utils.Utils.checkNotificationPermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.poppop.Fragments.ChatFragment;
import com.example.poppop.Fragments.MainFragment;
import com.example.poppop.Fragments.ProfileFragment;
import com.example.poppop.Utils.Utils;
import com.example.poppop.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
                    replaceFragment(new ChatFragment(), new Bundle());
                } else if (itemId == R.id.main) {
                    replaceFragment(new MainFragment(), new Bundle());
                } else if (itemId == R.id.profile) {
                    replaceFragment(new ProfileFragment(), new Bundle());
                }
                return true;
            });
        }

        Utils.checkNotificationPermission(MainActivity.this, this);
        Utils.checkLocationPermission(MainActivity.this, this);

    }

    private void replaceFragment(Fragment fragment, Bundle args) {
        if (args != null)
            fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
