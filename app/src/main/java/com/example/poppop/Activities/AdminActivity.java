package com.example.poppop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.poppop.Adapters.MyPagerAdapter;
import com.example.poppop.Adapters.UserAdapter;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.AdminUtils;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.ViewModel.AdminViewModel;
import com.example.poppop.ViewModel.AdminViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
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
