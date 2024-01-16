package com.example.poppop;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.poppop.Model.UserModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.Utils;
import com.example.poppop.boardingpages.hobbyBoarding;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

public class EditProfileActivity extends AppCompatActivity {
    UserModel userModel;
    ImageButton backBtn;
    AppCompatButton revertBtn, saveBtn;
    TextView ageValue, genderValue, horoSignValue, interestValue;
    EditText nameValue, aboutMeInput;
    TextView distancePrefValue, agePrefValue, genderPrefValue;

    LinearLayout nameBlock, ageBlock, genderBlock, horoSignBlock;
    LinearLayout genderPrefBlock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameValue = findViewById(R.id.nameValue);
        ageValue = findViewById(R.id.ageValue);
        genderValue = findViewById(R.id.genderValue);
        horoSignValue = findViewById(R.id.HoroSignValue);

        nameBlock = findViewById(R.id.nameBlock);
        ageBlock = findViewById(R.id.ageBlock);
        genderBlock = findViewById(R.id.genderBlock);
        horoSignBlock = findViewById(R.id.horoSignBlock);

        aboutMeInput = findViewById(R.id.aboutMeInput);
        interestValue = findViewById(R.id.interestBlock);

        distancePrefValue = findViewById(R.id.distancePrefValue);
        genderPrefValue = findViewById(R.id.genderPrefValue);

        revertBtn = findViewById(R.id.profile_revertBtn);
        saveBtn = findViewById(R.id.profile_saveBtn);

        nameBlock.setOnClickListener(v -> {
            nameValue.requestFocus();

            // Set cursor to the right (end of the text)
            nameValue.setSelection(nameValue.getText().length());

            // Show the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(nameValue, InputMethodManager.SHOW_IMPLICIT);

            Drawable pinkCircleDrawable = createDrawableWithPadding(R.drawable.circle_pink, 8); // Adjust the padding as needed

            // Set compound drawable to the right of the text with some padding
            nameValue.setCompoundDrawablesWithIntrinsicBounds(null, null, pinkCircleDrawable, null);
        });

        Intent getIntent = getIntent();
        userModel = getIntent.getParcelableExtra("userModel");
        if (userModel != null) {
            populateUserData();
        } else {
            // Handle the case where "userModel" extra is not present
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
            finish();
        }
        backBtn = findViewById(R.id.editProfile_backBtn);
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void populateUserData() {
        nameValue.setText(userModel.getName());
        ageValue.setText(userModel.getAge() != null ? userModel.getAge().toString() : "Empty");
        genderValue.setText(userModel.getGender() != null ? userModel.getGender() : "Empty");
        horoSignValue.setText(userModel.getHoroscopeSign() != null ? userModel.getHoroscopeSign() : "Empty");

        if(userModel.getBio() != null)
            aboutMeInput.setText(userModel.getBio());

        if(userModel.getInterests() != null){
            interestValue.setText(Utils.getTextInterest(userModel.getInterests()));
        }else{
            interestValue.setText("Add your interests");
        }

        setUpAgePicker();
        setupHoroscopeSpinner();
        setUpSliders();

        genderPrefBlock = findViewById(R.id.genderPrefBlock);
        genderPrefBlock.setOnClickListener(v -> showGenderPopupMenu(genderPrefBlock, true, genderPrefValue));
        genderBlock.setOnClickListener(v -> showGenderPopupMenu(genderBlock, false, genderValue));

        revertBtn.setOnClickListener(v -> convertChanges());
        saveBtn.setOnClickListener(v -> saveChanges());
    }

    private void updateDistanceText(float value) {
        Drawable pinkCircleDrawable = createDrawableWithPadding(R.drawable.circle_pink, 8); // Adjust the padding as needed

        // Set compound drawable to the right of the text with some padding
        distancePrefValue.setCompoundDrawablesWithIntrinsicBounds(null, null, pinkCircleDrawable, null);
        distancePrefValue.setText(getString(R.string.distance_format, Math.round(value)));
    }

    private void showGenderPopupMenu(View view, boolean includeEveryoneOption, TextView textView) {
        if (textView == null) {
            // Handle the case where textView is null
            return;
        }
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.gender_menu, popupMenu.getMenu());

        if (!includeEveryoneOption) {
            // Remove the "Everyone" option from the menu
            popupMenu.getMenu().removeItem(R.id.menu_everyone);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            // Handle menu item click

            if (itemId == R.id.menu_male) {
                updateGenderPreference("Male", textView);
            } else if (itemId == R.id.menu_female) {
                updateGenderPreference("Female", textView);
            } else if (itemId == R.id.menu_everyone) {
                updateGenderPreference("Everyone", textView);
            }
            return true;
        });

        popupMenu.show();
    }

    private void updateGenderPreference(String selectedGender, TextView textView) {
        if (textView == null) {
            // Handle the case where textView is null
            return;
        }
        // Create a small pink circle drawable
        Drawable pinkCircleDrawable = createDrawableWithPadding(R.drawable.circle_pink, 8); // Adjust the padding as needed

        // Set compound drawable to the right of the text with some padding
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, pinkCircleDrawable, null);
        // Set the SpannableString as the text of the TextView
        textView.setText(selectedGender);
        // Additional logic based on the selected gender
        // ...

        // For example, you might want to trigger some actions or update UI
    }

    private Drawable createDrawableWithPadding(@DrawableRes int drawableRes, int padding) {
        Drawable originalDrawable = ContextCompat.getDrawable(this, drawableRes);
        if (originalDrawable == null) {
            return null;
        }

        // Create a LayerDrawable to add padding
        Drawable[] layers = new Drawable[1];
        layers[0] = originalDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setLayerInset(0, padding, 0, 0, 0); // Adjust the padding as needed

        return layerDrawable;
    }

    private void setUpSliders(){
        RangeSlider ageSlider = findViewById(R.id.ageSlider);
        TextView ageRangeTextView = findViewById(R.id.ageRangeTextView);

        // Set initial values
        float initialMinValue = 18.0f;
        float initialMaxValue = 19.0f;
        ageRangeTextView.setText(getString(R.string.age_range_format, Math.round(initialMinValue), Math.round(initialMaxValue)));
        ageSlider.setValues(initialMinValue, initialMaxValue);

        ageSlider.addOnChangeListener((slider, value, fromUser) -> {
            float minValue = slider.getValues().get(0);
            float maxValue = slider.getValues().get(1);

            // Convert float values to integers
            int minAge = Math.round(minValue);
            int maxAge = Math.round(maxValue);

            Drawable pinkCircleDrawable = createDrawableWithPadding(R.drawable.circle_pink, 8); // Adjust the padding as needed

            // Set compound drawable to the right of the text with some padding
            ageRangeTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, pinkCircleDrawable, null);

            ageRangeTextView.setText(getString(R.string.age_range_format, minAge, maxAge));
        });

        Slider distanceSlider = findViewById(R.id.distanceSlider);
        // Set initial values
        float initialDistanceValue = 2.0f;
        distancePrefValue.setText(getString(R.string.distance_format, Math.round(initialDistanceValue)));
        distanceSlider.setValue(initialDistanceValue);

        // Add a listener to the Slider to update the TextView when the value changes
        distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
            updateDistanceText(value);
        });
    }
    private void setUpAgePicker(){
        final NumberPicker ageNumberPicker = findViewById(R.id.ageNumberPicker);
        String initialValueString = ageValue.getText().toString();
        int initialValue = initialValueString.isEmpty() ? 1 : Integer.parseInt(initialValueString);
        ageNumberPicker.setValue(initialValue);

        ageNumberPicker.setMinValue(18);
        ageNumberPicker.setMaxValue(60);

        ageBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of TextView and NumberPicker
                if (ageValue.getVisibility() == View.VISIBLE) {
                    ageValue.setVisibility(View.GONE);
                    ageNumberPicker.setVisibility(View.VISIBLE);
                    ageNumberPicker.setValue(userModel.getAge());
                } else {
                    ageValue.setVisibility(View.VISIBLE);
                    ageNumberPicker.setVisibility(View.GONE);
                }
            }
        });
        ageNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Drawable pinkCircleDrawable = createDrawableWithPadding(R.drawable.circle_pink, 8); // Adjust the padding as needed
                // Set compound drawable to the right of the text with some padding
                ageValue.setCompoundDrawablesWithIntrinsicBounds(null, null, pinkCircleDrawable, null);
                // Update the value in your TextView or perform any other action
                ageValue.setText(String.valueOf(newVal));
            }
        });
    }

    private void setupHoroscopeSpinner() {
        Spinner horoSignSpinner = findViewById(R.id.HoroSignSpinner);

        // Get the horoscope array from resources
        String[] horoscopeArray = getResources().getStringArray(R.array.horoscope);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, horoscopeArray);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        horoSignSpinner.setAdapter(adapter);

        horoSignBlock.setOnClickListener(v -> {
            // Toggle visibility of Spinner
            if(horoSignSpinner.getVisibility() == View.VISIBLE){
                horoSignSpinner.performClick();
            }
            else if (horoSignSpinner.getVisibility() == View.GONE) {
                horoSignSpinner.setVisibility(View.VISIBLE);
                horoSignValue.setVisibility(View.GONE);
            }
        });

        // Set a listener to handle item selection
        horoSignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected horoscope (e.g., update UI or perform actions)
                String selectedHoroscope = (String) parentView.getItemAtPosition(position);
                Drawable pinkCircleDrawable = createDrawableWithPadding(R.drawable.circle_pink, 8); // Adjust the padding as needed

                // Set compound drawable to the right of the text with some padding
                horoSignValue.setCompoundDrawablesWithIntrinsicBounds(null, null, pinkCircleDrawable, null);
                // Set the SpannableString as the text of the TextView
                horoSignValue.setText(selectedHoroscope);
                // Hide the Spinner after selection
                horoSignSpinner.setVisibility(View.GONE);
                horoSignValue.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private void saveChanges() {
        FirestoreUserUtils.getUserModelByUid(FirebaseUtils.currentUserId())
                .addOnSuccessListener(userModel -> {
                    // This method is called when UserModel is successfully retrieved
//                    newUser = userModel;

                    if (userModel != null) {
                        userModel.setName(nameValue.getText().toString());
                        try {
                            userModel.setAge(Integer.parseInt(ageValue.getText().toString()));
                        } catch (NumberFormatException e) {
                            // Handle the case where the age input is not a valid integer
                            e.printStackTrace(); // or show an error message
                        }
                        userModel.setGender(genderValue.getText().toString());
                        userModel.setHoroscopeSign(horoSignValue.getText().toString());

                        FirestoreUserUtils.updateUserModel(userModel)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // If the update is successful, start the new activity
                                        Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(this, "Fail to update", Toast.LENGTH_SHORT).show();
                                        // Handle errors here
                                        Exception exception = task.getException();
                                        if (exception != null) {
                                            exception.printStackTrace();
                                        }
                                    }
                                });
                    } else {
                        Log.e("ChatActivity", "otherUser is null");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure case
                    Log.e("Firestore", "Error retrieving UserModel: " + e.getMessage());
                    finish();
                });
    }

    private void convertChanges() {

    }
}