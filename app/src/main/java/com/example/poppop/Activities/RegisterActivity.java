package com.example.poppop.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poppop.MainActivity;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.boardingpages.boardingName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView usernameRegisterText;
    TextView passwordRegisterText;
    Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        TextView loginLink = findViewById(R.id.LoginTextView);
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        usernameRegisterText= findViewById(R.id.usernameRegisterText);
        passwordRegisterText = findViewById(R.id.passwordRegisterText);
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(v -> {
            String username = usernameRegisterText.getText().toString();
            String password = passwordRegisterText.getText().toString();
            mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                FirestoreUserUtils.checkIfUserExistsThenAdd(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Info saved successfully", Toast.LENGTH_SHORT).show();
                                        UserModel userModel = task1.getResult();
                                        if (userModel.getAge() == null){
                                            startActivity(new Intent(RegisterActivity.this, boardingName.class));
                                        }else{
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();
                                        }
                                        FirestoreUserUtils.updateFCMTokenForUser(userModel)
                                                .thenAccept(result -> {
                                                    Toast.makeText(RegisterActivity.this, "FCM Token saved successfully", Toast.LENGTH_SHORT).show();
                                                })
                                                .exceptionally(throwable -> {
                                                    Log.e("FCM token update", "Failed: " + throwable.getMessage());
                                                    Toast.makeText(RegisterActivity.this, "Fail to save FCM Token", Toast.LENGTH_SHORT).show();
                                                    return null;
                                                });
                                    } else {
                                        Exception exception = task1.getException();
                                        Log.e("exception", exception.toString());
                                        Toast.makeText(RegisterActivity.this, "Fail to save", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Fail to sign in", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}