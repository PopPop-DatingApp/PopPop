package com.example.poppop.Fragments_Admin;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poppop.Activities.UserDetailsActivity;
import com.example.poppop.Adapters.ReportCaseAdapter;
import com.example.poppop.Adapters.UserAdapter;
import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.Model.UserModel;
import com.example.poppop.R;
import com.example.poppop.Utils.FirestoreUserUtils;
import com.example.poppop.Utils.ReportCaseUtils;

import java.util.List;

public class ReportCaseFragment extends Fragment {
    private final String TAG = "ReportCaseFragment";

    private ReportCaseViewModel mViewModel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_case, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewReport);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(), new ReportCaseViewModelFactory(new ReportCaseUtils())).get(ReportCaseViewModel.class);
        mViewModel.listenToReportCaseList();
        mViewModel.getReportCaseList().observe(this, reportCaseModels -> {
            if (reportCaseModels != null) {
                // User data has changed, update your UI accordingly
                Log.d(TAG, reportCaseModels.get(0).getReporterId());
                displayReport(reportCaseModels);
            } else {
                // Handle the case where the user data is null or not found
                Log.d(TAG, "User data is null");
            }
        });
    }

    private void displayReport(List<ReportCaseModel> reportCaseModels) {
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create and set the adapter
        ReportCaseAdapter reportCaseAdapter = new ReportCaseAdapter(reportCaseModels, reportCaseModel -> {
            // Handle user click
            Intent intent = new Intent(getContext(), UserDetailsActivity.class);
//            intent.putExtra("userModel", user);
//            intent.putExtra("userId", user.getUserId());
            startActivity(intent);
        });
        recyclerView.setAdapter(reportCaseAdapter);
    }

}