package com.example.poppop.boardingpages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.poppop.R;

public class boardingAge extends AppCompatActivity {

    private NumberPicker agePicker;
    private TextView tempDataTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_age);

        agePicker = findViewById(R.id.agePicker);
        agePicker.setValue(18);
        agePicker.setMinValue(18);
        agePicker.setMaxValue(60);

        nextButton = findViewById(R.id.boardingAgeBtn);

//        // Assuming you passed the tempData from the previous activity
//        final String userName = getIntent().getStringExtra("userName");
//        tempDataTextView.setText(userName);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the selected age (newVal) when the button is clicked
                Integer userAge = Integer.valueOf(agePicker.getValue());
                Intent getintent = getIntent();
                String userName = getintent.getStringExtra("userName");

                // Save the selected age along with the previous data
                Intent intent = new Intent(boardingAge.this, boardingGender.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userAge", userAge);
                startActivity(intent);
            }
        });
    }
}