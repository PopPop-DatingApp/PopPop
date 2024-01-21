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
import com.example.poppop.Activities.ReportCaseDetailActivity;
import com.example.poppop.Activities.UserDetailsActivity;
import com.example.poppop.Adapters.ReportCaseAdapter;
import com.example.poppop.Adapters.UserAdapter;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.AdminUtils;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.ReportCaseUtils;
import com.example.poppop.ViewModel.AdminViewModel;
import com.example.poppop.ViewModel.AdminViewModelFactory;
import com.example.poppop.ViewModel.UserListViewModel;
import com.example.poppop.ViewModel.UserListViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {
    private static final String TAG = "UserListFragment";
    private RecyclerView recyclerView;
    private UserListViewModel userListViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userListViewModel = new ViewModelProvider(requireActivity(), new UserListViewModelFactory(new FirebaseUtils())).get(UserListViewModel.class);
        userListViewModel.listenToUserList();
        // Observe the LiveData for user list changes
        userListViewModel.getAllUser().observe(this, userModelList -> {
            if (userModelList != null) {
                Log.d(TAG, "User data size: " + userModelList.size());
                // User data has changed, update your UI accordingly
                displayUserList(userModelList);
            } else {
                // Handle the case where the user data is null or not found
                Log.d(TAG, "User data is null");
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewUsers);

        return view;
    }

    private void displayUserList(List<UserModel> userModelList) {
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create and set the adapter
        UserAdapter userAdapter = new UserAdapter(userModelList, userModel -> {
            // Handle user click
            Intent intent = new Intent(getContext(), UserDetailsActivity.class);
            intent.putExtra("userId", userModel.getUserId());
            startActivity(intent);
        });
        recyclerView.setAdapter(userAdapter);
    }
}
