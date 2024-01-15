package com.example.poppop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poppop.Model.UserModel;
import com.google.android.material.slider.RangeSlider;

public class EditProfileActivity extends AppCompatActivity {
    UserModel userModel;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent getIntent = getIntent();
        userModel = getIntent.getParcelableExtra("userModel");
        if (userModel != null) {
            // Do something with the userModel
            Log.d("letanphong", userModel.getName());
            Log.d("letanphong", userModel.getAge().toString());

        } else {
            // Handle the case where "userModel" extra is not present
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
            finish();
        }
        backBtn = findViewById(R.id.editProfile_backBtn);
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        RangeSlider ageSlider = findViewById(R.id.ageSlider);
        TextView ageRangeTextView = findViewById(R.id.ageRangeTextView);

        // Set initial values
        float initialMinValue = 18.0f;
        float initialMaxValue = 19.0f;
        ageRangeTextView.setText(getString(R.string.age_range_format, Math.round(initialMinValue), Math.round(initialMaxValue)));
        ageSlider.setValues(initialMinValue, initialMaxValue);

        ageSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                float minValue = slider.getValues().get(0);
                float maxValue = slider.getValues().get(1);

                // Convert float values to integers
                int minAge = Math.round(minValue);
                int maxAge = Math.round(maxValue);

                ageRangeTextView.setText(getString(R.string.age_range_format, minAge, maxAge));
            }
        });

    }
}