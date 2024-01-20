package com.example.poppop.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.poppop.Model.UserModel;
import com.example.poppop.R;

public class UserDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Retrieve UserModel from the intent
        UserModel userModel = getIntent().getParcelableExtra("userModel");

        // Use userModel to display details in your activity UI
        if (userModel != null) {
            // Example: Display user name
            TextView textViewUserName = findViewById(R.id.textViewUserName);
            textViewUserName.setText(userModel.getName());
        }
    }
}
