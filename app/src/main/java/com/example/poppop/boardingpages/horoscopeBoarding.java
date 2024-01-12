package com.example.poppop.boardingpages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.poppop.R;

public class horoscopeBoarding extends AppCompatActivity {
    private TextView tempDataTextView, tempDataTextView2, TempDataTextView3;
    private String userName, userGender;
    private Integer userAge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horoscope_boarding);
        tempDataTextView = findViewById(R.id.tempDataTextViewgend);
        tempDataTextView2 = findViewById(R.id.tempDataTextViewgend2);
        TempDataTextView3 = findViewById(R.id.tempDataTextViewgend3);

        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);
        userGender = getIntent().getStringExtra("userGender");
        tempDataTextView.setText("Temp Data: " + userName);
        tempDataTextView2.setText("Temp Data: " + userAge);
        TempDataTextView3.setText("Temp Data: " + userGender);
    }
}