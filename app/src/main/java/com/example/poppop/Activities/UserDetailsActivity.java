package com.example.poppop.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.ViewModel.UserViewModel;
import com.example.poppop.ViewModel.UserViewModelFactory;

public class UserDetailsActivity extends AppCompatActivity {
    private final String TAG = "UserDetailsActivity";
    UserViewModel userViewModel;
    Button banBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
//        UserModel userModel = getIntent().getParcelableExtra("userModel");
        String userId = getIntent().getStringExtra("userId");
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(new FirestoreUserUtils())).get(UserViewModel.class);
        userViewModel.startListeningToUserData(userId);
        userViewModel.getUserLiveData().observe(this, userModel -> {
            // Update your UI or perform actions based on the changes in user data
            if (userModel != null) {
                banBtn = findViewById(R.id.banBtn);

                // Handle the user data
                if(userModel.getBanned()){
                    banBtn.setText("Unban");
                    banBtn.setOnClickListener(v -> {
                        FirestoreUserUtils.updateStatus(userModel.getUserId(),false);
                    });
                }else{
                    banBtn.setText("Ban");
                    banBtn.setOnClickListener(v -> {
                        FirestoreUserUtils.updateStatus(userModel.getUserId(),true);
                    });
                }

            } else {
                // Handle the case when user data is null
                finish();
            }
        });
    }
}
