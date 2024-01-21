package com.example.poppop.Fragments_Admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.poppop.Activities.ReportCaseDetailActivity;
import com.example.poppop.Adapters.ReportCaseAdapter;
import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.R;
import com.example.poppop.Utils.ReportCaseUtils;
import com.example.poppop.ViewModel.HistoryViewModel;

import java.util.List;

public class HistoryFragment extends Fragment {

    private final String TAG = "HistoryFragment";

    private HistoryViewModel mViewModel;
    private RecyclerView recyclerView;
    private TextView no_data_textview;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HistoryFragment onCreate");
        mViewModel = new ViewModelProvider(requireActivity(), new ReportCaseViewModelFactory(new ReportCaseUtils())).get(HistoryViewModel.class);
        mViewModel.listenToReportCaseList();
        mViewModel.getCompletedReportCases().observe(this, reportCaseModels -> {
            if (reportCaseModels != null) {
                Log.d(TAG, "User data size: " + reportCaseModels.size());
                no_data_textview.setVisibility(View.GONE);
                displayReport(reportCaseModels);
            } else {
                no_data_textview.setVisibility(View.VISIBLE);
                Log.d(TAG, "Report data is null");
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewReport);
        no_data_textview = view.findViewById(R.id.no_data_text);
        return view;
    }

    private void displayReport(List<ReportCaseModel> reportCaseModels) {
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create and set the adapter
        ReportCaseAdapter reportCaseAdapter = new ReportCaseAdapter(reportCaseModels, reportCaseModel -> {
            // Handle user click
            Intent intent = new Intent(getContext(), ReportCaseDetailActivity.class);
//            intent.putExtra("userModel", user);
            intent.putExtra("reportCaseId", reportCaseModel.getReportCaseId());
            startActivity(intent);
        });
        recyclerView.setAdapter(reportCaseAdapter);
    }
}