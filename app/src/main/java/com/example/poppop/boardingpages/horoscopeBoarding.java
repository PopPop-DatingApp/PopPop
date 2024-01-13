package com.example.poppop.boardingpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.poppop.R;

public class horoscopeBoarding extends AppCompatActivity {
    private TextView tempDataTextView, tempDataTextView2, TempDataTextView3;
    private String userName, userGender, userHoro;
    private Integer userAge;
    private Button nextButton;
    NumberPicker horoPicker;
    String[] horoscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horoscope_boarding);
        nextButton = findViewById(R.id.boardingHoroBtn);
        //get the string array from the created array in strings.xml
        horoscope = getResources().getStringArray(R.array.horoscope);
        horoPicker = findViewById(R.id.horoPicker);
        horoPicker.setDisplayedValues(horoscope);


        horoPicker.setMaxValue(0);
        horoPicker.setMaxValue(11);

        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);
        userGender = getIntent().getStringExtra("userGender");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userHoro = String.valueOf(horoscope[horoPicker.getValue()]);
                Intent intent = new Intent(horoscopeBoarding.this, hobbyBoarding.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userAge", userAge);
                intent.putExtra("userGender", userGender);
                intent.putExtra("userHoro", userHoro);
                startActivity(intent);
            }
        });

    }
}