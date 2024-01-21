package com.example.poppop.Fragments_Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poppop.Activities.AdminActivity;
import com.example.poppop.Activities.UserDetailsActivity;
import com.example.poppop.Adapters.UserAdapter;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.AdminUtils;
import com.example.poppop.ViewModel.AdminViewModel;
import com.example.poppop.ViewModel.AdminViewModelFactory;
import com.example.poppop.ViewModel.UserListViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {
    private static final String TAG = "UserListFragment";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private UserListViewModel userListViewModel;

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userListViewModel = new ViewModelProvider(this).get(UserListViewModel.class);
        userListViewModel.listenToUserList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewUsers);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(new ArrayList<>(), user -> {
            Intent intent = new Intent(getContext(), UserDetailsActivity.class);
            intent.putExtra("userId", user.getUserId());
            startActivity(intent);
        });
        recyclerView.setAdapter(userAdapter);

        // Observe the LiveData for user list changes
        userListViewModel.getAllUser().observe(getViewLifecycleOwner(), userModelList -> {
            if (userModelList != null) {
                // User data has changed, update your UI accordingly
                userAdapter.updateUserList(userModelList);
            } else {
                // Handle the case where the user data is null or not found
                Log.d(TAG, "User data is null");
            }
        });

        return view;
    }
}
