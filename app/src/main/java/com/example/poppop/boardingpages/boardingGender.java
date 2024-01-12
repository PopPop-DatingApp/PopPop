package com.example.poppop.boardingpages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.poppop.R;

public class boardingGender extends AppCompatActivity {

    private TextView tempDataTextView, tempDataTextView2, genderViewCheck;
    private ImageView maleImageView;
    private ImageView femaleImageView;
    private Button nextButton;

    private String userName, userGender;
    private Integer userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_gender);

        tempDataTextView = findViewById(R.id.tempDataTextViewgend);
        tempDataTextView2 = findViewById(R.id.tempDataTextViewgend2);

        maleImageView = findViewById(R.id.maleImageView);
        femaleImageView = findViewById(R.id.femaleImageView);
        nextButton = findViewById(R.id.boardingGenderBtn);

        // Assuming you passed the user data from the previous activity
        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);
        tempDataTextView.setText("Temp Data: " + userName);
        tempDataTextView2.setText("Temp Data: " + userAge);

        maleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userGender = "Male";

            }
        });

        femaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userGender = "Female";
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the button is clicked, transfer all data to the next activity
                Intent intent = new Intent(boardingGender.this, horoscopeBoarding.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userAge", userAge);
                intent.putExtra("userGender", userGender);
                startActivity(intent);
            }
        });
    }


}
