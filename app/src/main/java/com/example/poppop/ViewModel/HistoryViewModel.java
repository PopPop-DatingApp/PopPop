package com.example.poppop.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.poppop.Model.ReportCaseModel;
import com.example.poppop.Utils.FirebaseUtils;
import com.example.poppop.Utils.ReportCaseUtils;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends ViewModel {
    private final String TAG = "HistoryViewModel";
    private final ReportCaseUtils reportCaseUtils;


    private final MutableLiveData<List<ReportCaseModel>> completedReportCases = new MutableLiveData<>();

    // ListenerRegistration for the snapshot listener
    private ListenerRegistration completedReportCasesListenerRegistration;

    public HistoryViewModel(ReportCaseUtils reportCaseUtils) {
        this.reportCaseUtils = reportCaseUtils;
    }

    public void listenToReportCaseList() {
        completedReportCasesListenerRegistration = FirebaseUtils.getAllReportCasesCollectionReference().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Listen failed.", e);
                completedReportCases.setValue(null);
                return;
            }
            List<ReportCaseModel> completedCases = new ArrayList<>();

            if (queryDocumentSnapshots != null) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ReportCaseModel reportCaseModel = documentSnapshot.toObject(ReportCaseModel.class);

                    // Check if the report case is completed or pending
                    if (reportCaseModel.getDone() != null && reportCaseModel.getDone()) {
                        completedCases.add(reportCaseModel);
                    }
                }


                // Sort and update LiveData for completed report cases (oldest first)
                completedCases.sort((c1, c2) -> c1.getReportTime().compareTo(c2.getReportTime()));
                completedReportCases.setValue(completedCases);

            } else {
                Log.d(TAG, "Current data: null");
                completedReportCases.setValue(null);
            }
        });
    }

    public MutableLiveData<List<ReportCaseModel>> getCompletedReportCases() {
        return completedReportCases;
    }

    // Clean up the listener when the ViewModel is no longer used
    @Override
    protected void onCleared() {
        if (completedReportCasesListenerRegistration != null) {
            completedReportCasesListenerRegistration.remove();
        }
    }
}
