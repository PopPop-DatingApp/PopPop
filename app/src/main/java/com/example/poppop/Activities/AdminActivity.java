package com.example.poppop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poppop.Adapters.UserAdapter;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.AdminUtils;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.ViewModel.AdminViewModel;
import com.example.poppop.ViewModel.AdminViewModelFactory;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Create an instance of AdminViewModel
        adminViewModel = new ViewModelProvider(this, new AdminViewModelFactory(new AdminUtils())).get(AdminViewModel.class);

        // Start listening to admin data
        adminViewModel.startListeningToAdminData(FirebaseUtils.currentUserId());

        // Observe changes in admin data
        adminViewModel.getAdminLiveData().observe(this, adminModel -> {
            if (adminModel != null) {
                // Admin data has changed, update your UI accordingly
                getAllUsers();
            } else {
                // Handle the case where the admin data is null or not found
                Log.d(TAG, "Admin data is null");
            }
        });
    }

    private void getAllUsers() {
        // Use AdminUtils to get the list of all users
        AdminUtils.getAllUsersList(new AdminUtils.FirestoreListener<List<UserModel>>() {
            @Override
            public void onSuccess(List<UserModel> userList) {
                displayUserList(userList);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the failure case
                Log.e(TAG, "Error getting user list", e);
            }
        });
    }

    private void displayUserList(List<UserModel> userList) {
        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter
        UserAdapter userAdapter = new UserAdapter(userList, user -> {
            // Handle user click
            Intent intent = new Intent(AdminActivity.this, UserDetailsActivity.class);
            intent.putExtra("userModel", user);
            intent.putExtra("userId", user.getUserId());
            startActivity(intent);
        });
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload your data here
        getAllUsers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening to admin data when the activity is destroyed
        adminViewModel.stopListeningToAdminData();
    }
}
