package com.example.poppop.boardingpages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.poppop.R;

public class boardingGender extends AppCompatActivity {
    private String userName, userGender;
    private Integer userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_gender);

        ImageView maleImageView = findViewById(R.id.maleImageView);
        ImageView femaleImageView = findViewById(R.id.femaleImageView);
        Button nextButton = findViewById(R.id.boardingGenderBtn);

        // Assuming you passed the user data from the previous activity
        maleImageView.setAlpha(0.5f);
        femaleImageView.setAlpha(0.5f);

        // Assuming you passed the user data from the previous activity
        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);

        maleImageView.setOnClickListener(v -> {
            userGender = "Male";
            maleImageView.setAlpha(1.0f);
            femaleImageView.setAlpha(0.5f);
        });

        femaleImageView.setOnClickListener(v -> {
            userGender = "Female";
            maleImageView.setAlpha(0.5f);
            femaleImageView.setAlpha(1.0f);
        });


        nextButton.setOnClickListener(v -> {
            // When the button is clicked, transfer all data to the next activity
            Intent intent = new Intent(boardingGender.this, horoscopeBoarding.class);
            intent.putExtra("userName", userName);
            intent.putExtra("userAge", userAge);
            intent.putExtra("userGender", userGender);
            startActivity(intent);
        });
    }


}
