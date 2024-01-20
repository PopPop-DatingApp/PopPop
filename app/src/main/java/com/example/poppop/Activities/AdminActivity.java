package com.example.poppop.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.poppop.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            // Sign out the user when the app is destroyed
        FirebaseAuth.getInstance().signOut();
    }
}