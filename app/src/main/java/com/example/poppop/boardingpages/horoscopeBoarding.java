//package com.example.poppop.boardingpages;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.NumberPicker;
//import android.widget.TextView;
//
//import com.example.poppop.R;
//
//public class horoscopeBoarding extends AppCompatActivity {
//    private TextView tempDataTextView, tempDataTextView2, TempDataTextView3;
//    private String userName, userGender, userHoro;
//    private Integer userAge;
//    private Button nextButton;
//    NumberPicker horoPicker;
//    String[] horoscope;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_horoscope_boarding);
//        nextButton = findViewById(R.id.boardingHoroBtn);
//        //get the string array from the created array in strings.xml
//        horoscope = getResources().getStringArray(R.array.horoscope);
//        horoPicker = findViewById(R.id.horoPicker);
//        horoPicker.setDisplayedValues(horoscope);
//
//
//        horoPicker.setMaxValue(0);
//        horoPicker.setMaxValue(11);
//
//        userName = getIntent().getStringExtra("userName");
//        userAge = getIntent().getIntExtra("userAge", 18);
//        userGender = getIntent().getStringExtra("userGender");
//
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                userHoro = String.valueOf(horoscope[horoPicker.getValue()]);
//                Intent intent = new Intent(horoscopeBoarding.this, hobbyBoarding.class);
//                intent.putExtra("userName", userName);
//                intent.putExtra("userAge", userAge);
//                intent.putExtra("userGender", userGender);
//                intent.putExtra("userHoro", userHoro);
//                startActivity(intent);
//            }
//        });
//
//    }
//}
package com.example.poppop.boardingpages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.poppop.R;

public class horoscopeBoarding extends AppCompatActivity {
    private String userName, userGender, userHoro;
    private Integer userAge;
    private Button nextButton, displayHoroscopeButton;

    private NumberPicker horoPicker, monthPicker, dayPicker;
    private TextView horoscopeResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horoscope_boarding);

        nextButton = findViewById(R.id.boardingHoroBtn);
        displayHoroscopeButton = findViewById(R.id.displayHoroscopeButton);
        horoPicker = findViewById(R.id.horoPicker);
        monthPicker = findViewById(R.id.monthPicker);
        dayPicker = findViewById(R.id.dayPicker);
        horoscopeResultTextView = findViewById(R.id.horoscopeResultTextView);

        // Assuming you have the horoscope array defined in strings.xml
        String[] horoscopeArray = getResources().getStringArray(R.array.horoscope);
        horoPicker.setDisplayedValues(horoscopeArray);
        horoPicker.setMinValue(0);
        horoPicker.setMaxValue(horoscopeArray.length - 1);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        userName = getIntent().getStringExtra("userName");
        userAge = getIntent().getIntExtra("userAge", 18);
        userGender = getIntent().getStringExtra("userGender");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected date from custom pickers
                int selectedMonth = monthPicker.getValue();
                int selectedDay = dayPicker.getValue();

                // Determine the zodiac sign based on the date
                userHoro = getZodiacSign(selectedDay, selectedMonth);

                // Display the result to the user
                horoscopeResultTextView.setText("Your horoscope: " + userHoro);

                Intent intent = new Intent(horoscopeBoarding.this, hobbyBoarding.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userAge", userAge);
                intent.putExtra("userGender", userGender);
                intent.putExtra("userHoro", userHoro);
                startActivity(intent);
            }
        });

        displayHoroscopeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display the result to the user
                horoscopeResultTextView.setText("Your horoscope: " + getZodiacSign(dayPicker.getValue(), monthPicker.getValue()));
            }
        });
    }

    private String getZodiacSign(int day, int month) {
        // Define date ranges for each zodiac sign
        int[][] dateRanges = {
                {21, 3, 19, 4},   // Aries
                {20, 4, 20, 5},    // Taurus
                {21, 5, 20, 6},    // Gemini
                {21, 6, 22, 7},    // Cancer
                {23, 7, 22, 8},    // Leo
                {23, 8, 22, 9},    // Virgo
                {23, 9, 22, 10},   // Libra
                {23, 10, 21, 11},  // Scorpio
                {22, 11, 21, 12},  // Sagittarius
                {22, 12, 19, 1},   // Capricorn
                {20, 1, 18, 2},    // Aquarius
                {19, 2, 20, 3}     // Pisces
        };

        for (int i = 0; i < dateRanges.length; i++) {
            int startDay = dateRanges[i][0];
            int startMonth = dateRanges[i][1];
            int endDay = dateRanges[i][2];
            int endMonth = dateRanges[i][3];

            // Check if the selected date falls within the date range for the current zodiac sign
            if ((month == startMonth && day >= startDay) || (month == endMonth && day <= endDay)) {
                return getResources().getStringArray(R.array.horoscope)[i];
            }
        }

        // Default return (if no match is found)
        return "";
    }

}
