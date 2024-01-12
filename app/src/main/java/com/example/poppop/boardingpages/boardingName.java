package com.example.poppop.boardingpages;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.example.poppop.Model.UserModel;
import com.example.poppop.R;

public class boardingName extends AppCompatActivity {

    private EditText nameEditText;
    private Button registerButton;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_name);


        nameEditText = findViewById(R.id.boardingNameEditText);
        registerButton = findViewById(R.id.boardingNameBtn);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                userName = charSequence.toString();
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(boardingName.this, boardingAge.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        });
    }
}
